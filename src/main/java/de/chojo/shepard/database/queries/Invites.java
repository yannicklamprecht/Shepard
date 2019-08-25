package de.chojo.shepard.database.queries;

import de.chojo.shepard.database.types.DatabaseInvite;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Invite;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.sql.Array;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static de.chojo.shepard.database.DatabaseConnector.getConn;
import static de.chojo.shepard.database.DbUtil.handleException;

public final class Invites {

    private Invites() {
    }

    /**
     * Adds a invite to a guild.
     *
     * @param guild Guild object for which the invite should be added
     * @param code  Code of the Invite
     * @param name  Name of the invite
     * @param count How often the invite was used
     * @param event event from command sending for error handling. Can be null.
     */
    public static void addInvite(Guild guild, String code, String name, int count, MessageReceivedEvent event) {
        try (PreparedStatement statement = getConn()
                .prepareStatement("SELECT shepard_func.add_invite(?,?,?,?)")) {
            statement.setString(1, guild.getId());
            statement.setString(2, code);
            statement.setString(3, name);
            statement.setInt(4, count);
            statement.execute();
        } catch (SQLException e) {
            handleException(e, event);
        }
    }

    /**
     * Gets the invites of a guild.
     *
     * @param guild Guild object for lookup
     * @param event event from command sending for error handling. Can be null.
     * @return list of invite objects
     */
    public static List<DatabaseInvite> getInvites(Guild guild, MessageReceivedEvent event) {
        List<DatabaseInvite> invites = new ArrayList<>();
        try (PreparedStatement statement = getConn()
                .prepareStatement("SELECT * from shepard_func.get_invites(?)")) {
            statement.setString(1, guild.getId());
            ResultSet result = statement.executeQuery();

            if (result.next()) {
                String code = result.getString("inv_code");
                int used = result.getInt("inv_used");
                String name = result.getString("inv_source");

                invites.add(new DatabaseInvite(code, used, name));
            }
        } catch (SQLException e) {
            handleException(e, event);
        }
        return invites;
    }

    /**
     * Removes a invite of a guild.
     *
     * @param guild Guild object for lookup
     * @param code  Code of the invite to remove
     * @param event event from command sending for error handling. Can be null.
     */
    public static void removeInvite(Guild guild, String code, MessageReceivedEvent event) {
        try (PreparedStatement statement = getConn()
                .prepareStatement("SELECT shepard_func.remove_invite(?,?)")) {
            statement.setString(1, guild.getId());
            statement.setString(2, code);
            statement.execute();
        } catch (SQLException e) {
            handleException(e, event);
        }
    }

    /**
     * Sets the counter of a invite +1.
     *
     * @param guild Guild object for lookup
     * @param code  Code of the invite for upcount
     * @param event event from command sending for error handling. Can be null.
     */
    public static void upcountInvite(Guild guild, String code, MessageReceivedEvent event) {
        try (PreparedStatement statement = getConn()
                .prepareStatement("SELECT shepard_func.upcount_invite(?,?)")) {
            statement.setString(1, guild.getId());
            statement.setString(2, code);
            statement.execute();
        } catch (SQLException e) {
            handleException(e, event);
        }
    }

    /**
     * Deletes all invites which are not present anymore.
     *
     * @param guild   Guild object for lookup
     * @param invites List of invites of a guild
     * @param event   event from command sending for error handling. Can be null.
     */
    public static void updateInvite(Guild guild, List<Invite> invites, MessageReceivedEvent event) {
        try (PreparedStatement statement = getConn()
                .prepareStatement("SELECT shepard_func.update_invites(?,?)")) {
            statement.setString(1, guild.getId());

            String[] codeStrings = new String[invites.size()];
            for (int i = 0; i < invites.size(); i++) {
                codeStrings[i] = invites.get(i).getCode();
            }
            Array codes = getConn().createArrayOf("varchar", codeStrings);
            statement.setArray(2, codes);

            statement.execute();

            codes.free();
        } catch (SQLException e) {
            handleException(e, event);
        }
    }
}

