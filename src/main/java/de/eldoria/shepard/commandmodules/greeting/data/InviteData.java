package de.eldoria.shepard.commandmodules.greeting.data;

import de.eldoria.shepard.commandmodules.greeting.types.DatabaseInvite;
import de.eldoria.shepard.database.QueryObject;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Invite;

import javax.sql.DataSource;
import java.sql.Array;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static de.eldoria.shepard.database.DbUtil.handleException;

public class InviteData extends QueryObject {

    /**
     * Create a new invite data object.
     *
     * @param source data source for connection retrieval
     */
    public InviteData(DataSource source) {
        super(source);
    }

    /**
     * Adds a invite to a guild.
     *
     * @param guild          Guild object for which the invite should be added
     * @param code           Code of the Invite
     * @param name           Name of the invite
     * @param count          How often the invite was used
     * @param messageContext messageContext from command sending for error handling. Can be null.
     * @return true if the query execution was successful
     */
    public boolean addInvite(Guild guild, String code, String name, int count,
                             MessageEventDataWrapper messageContext) {
        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT shepard_func.add_invite(?,?,?,?)")) {
            statement.setString(1, guild.getId());
            statement.setString(2, code);
            statement.setString(3, name);
            statement.setInt(4, count);
            statement.execute();
        } catch (SQLException e) {
            handleException(e, messageContext);
            return false;
        }
        return true;
    }

    /**
     * Gets the invites of a guild.
     *
     * @param guild          Guild object for lookup
     * @param messageContext messageContext from command sending for error handling. Can be null.
     * @return list of invite objects
     */
    public List<DatabaseInvite> getInvites(Guild guild, MessageEventDataWrapper messageContext) {
        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT * from shepard_func.get_invites(?)")) {
            List<DatabaseInvite> invites = new ArrayList<>();
            statement.setString(1, guild.getId());
            ResultSet result = statement.executeQuery();

            while (result.next()) {
                String code = result.getString("inv_code");
                int used = result.getInt("inv_used");
                String name = result.getString("inv_source");

                invites.add(new DatabaseInvite(code, used, name));
            }
            return invites;
        } catch (SQLException e) {
            handleException(e, messageContext);
        }
        return Collections.emptyList();
    }

    /**
     * Removes a invite of a guild.
     *
     * @param guild          Guild object for lookup
     * @param code           Code of the invite to remove
     * @param messageContext messageContext from command sending for error handling. Can be null.
     * @return true if the query execution was successful
     */
    public boolean removeInvite(Guild guild, String code, MessageEventDataWrapper messageContext) {
        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT shepard_func.remove_invite(?,?)")) {
            statement.setString(1, guild.getId());
            statement.setString(2, code);
            statement.execute();
        } catch (SQLException e) {
            handleException(e, messageContext);
            return false;
        }
        return true;
    }

    /**
     * Sets the counter of a invite +1.
     *
     * @param guild          Guild object for lookup
     * @param code           Code of the invite for upcount
     * @param messageContext messageContext from command sending for error handling. Can be null.
     */
    public void upCountInvite(Guild guild, String code, MessageEventDataWrapper messageContext) {
        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT shepard_func.upcount_invite(?,?)")) {
            statement.setString(1, guild.getId());
            statement.setString(2, code);
            statement.execute();
        } catch (SQLException e) {
            handleException(e, messageContext);
        }
    }

    /**
     * Deletes all invites which are not present anymore.
     *
     * @param guild          Guild object for lookup
     * @param invites        List of invites of a guild
     * @param messageContext messageContext from command sending for error handling. Can be null.
     * @return true if the query execution was successful
     */
    public boolean updateInvite(Guild guild, List<Invite> invites, MessageEventDataWrapper messageContext) {
        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT shepard_func.update_invites(?,?)")) {
            statement.setString(1, guild.getId());

            String[] codeStrings = new String[invites.size()];
            for (int i = 0; i < invites.size(); i++) {
                codeStrings[i] = invites.get(i).getCode();
            }
            Array codes = conn.createArrayOf("varchar", codeStrings);
            statement.setArray(2, codes);
            statement.execute();
        } catch (SQLException e) {
            handleException(e, messageContext);
            return false;
        }
        return true;
    }
}

