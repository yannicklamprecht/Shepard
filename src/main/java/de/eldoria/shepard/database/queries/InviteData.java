package de.eldoria.shepard.database.queries;

import de.eldoria.shepard.database.types.DatabaseInvite;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Invite;

import java.sql.Array;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static de.eldoria.shepard.database.DatabaseConnector.getConn;
import static de.eldoria.shepard.database.DbUtil.handleExceptionAndIgnore;

public final class InviteData {

    private InviteData() {
    }

    /**
     * Adds a invite to a guild.
     *
     * @param guild Guild object for which the invite should be added
     * @param code  Code of the Invite
     * @param name  Name of the invite
     * @param count How often the invite was used
     * @param event event from command sending for error handling. Can be null.
     * @return true if the query execution was successful
     */
    public static boolean addInvite(Guild guild, String code, String name, int count, MessageEventDataWrapper event) {
        try (PreparedStatement statement = getConn()
                .prepareStatement("SELECT shepard_func.add_invite(?,?,?,?)")) {
            statement.setString(1, guild.getId());
            statement.setString(2, code);
            statement.setString(3, name);
            statement.setInt(4, count);
            statement.execute();
        } catch (SQLException e) {
            handleExceptionAndIgnore(e, event);
            return false;
        }
        return true;
    }

    /**
     * Gets the invites of a guild.
     *
     * @param guild Guild object for lookup
     * @param event event from command sending for error handling. Can be null.
     * @return list of invite objects
     */
    public static List<DatabaseInvite> getInvites(Guild guild, MessageEventDataWrapper event) {
        List<DatabaseInvite> invites = new ArrayList<>();
        try (PreparedStatement statement = getConn()
                .prepareStatement("SELECT * from shepard_func.get_invites(?)")) {
            statement.setString(1, guild.getId());
            ResultSet result = statement.executeQuery();

            while (result.next()) {
                String code = result.getString("inv_code");
                int used = result.getInt("inv_used");
                String name = result.getString("inv_source");

                invites.add(new DatabaseInvite(code, used, name));
            }
        } catch (SQLException e) {
            handleExceptionAndIgnore(e, event);
        }
        return invites;
    }

    /**
     * Removes a invite of a guild.
     *
     * @param guild Guild object for lookup
     * @param code  Code of the invite to remove
     * @param event event from command sending for error handling. Can be null.
     * @return true if the query execution was successful
     */
    public static boolean removeInvite(Guild guild, String code, MessageEventDataWrapper event) {
        try (PreparedStatement statement = getConn()
                .prepareStatement("SELECT shepard_func.remove_invite(?,?)")) {
            statement.setString(1, guild.getId());
            statement.setString(2, code);
            statement.execute();
        } catch (SQLException e) {
            handleExceptionAndIgnore(e, event);
            return false;
        }
        return true;
    }

    /**
     * Sets the counter of a invite +1.
     *
     * @param guild Guild object for lookup
     * @param code  Code of the invite for upcount
     * @param event event from command sending for error handling. Can be null.
     * @return true if the query execution was successful
     */
    public static boolean upCountInvite(Guild guild, String code, MessageEventDataWrapper event) {
        try (PreparedStatement statement = getConn()
                .prepareStatement("SELECT shepard_func.upcount_invite(?,?)")) {
            statement.setString(1, guild.getId());
            statement.setString(2, code);
            statement.execute();
        } catch (SQLException e) {
            handleExceptionAndIgnore(e, event);
            return false;
        }
        return true;
    }

    /**
     * Deletes all invites which are not present anymore.
     *
     * @param guild   Guild object for lookup
     * @param invites List of invites of a guild
     * @param event   event from command sending for error handling. Can be null.
     * @return true if the query execution was successful
     */
    public static boolean updateInvite(Guild guild, List<Invite> invites, MessageEventDataWrapper event) {
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
        } catch (SQLException e) {
            handleExceptionAndIgnore(e, event);
            return false;
        }
        return true;
    }
}

