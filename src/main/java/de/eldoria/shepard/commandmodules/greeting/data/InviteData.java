package de.eldoria.shepard.commandmodules.greeting.data;

import de.eldoria.shepard.commandmodules.greeting.types.DatabaseInvite;
import de.eldoria.shepard.database.QueryObject;
import de.eldoria.shepard.wrapper.EventWrapper;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Invite;
import net.dv8tion.jda.api.entities.Role;

import javax.sql.DataSource;
import java.sql.*;
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
                             EventWrapper messageContext) {
        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT shepard_func.add_invite(?,?,?,?)")) {
            statement.setLong(1, guild.getIdLong());
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
     * @param wrapper messageContext from command sending for error handling. Can be null.
     * @return list of invite objects
     */
    public List<DatabaseInvite> getInvites(Guild guild, EventWrapper wrapper) {
        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT * FROM shepard_func.get_invites(?)")) {
            List<DatabaseInvite> invites = new ArrayList<>();
            statement.setLong(1, guild.getIdLong());
            ResultSet result = statement.executeQuery();

            while (result.next()) {
                String code = result.getString("inv_code");
                int used = result.getInt("inv_used");
                String name = result.getString("inv_source");
                long roleId = result.getLong("role_id");
                Role role = guild.getRoleById(roleId);
                invites.add(new DatabaseInvite(code, used, name, role));
            }
            return invites;
        } catch (SQLException e) {
            handleException(e, wrapper);
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
    public boolean removeInvite(Guild guild, String code, EventWrapper messageContext) {
        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT shepard_func.remove_invite(?,?)")) {
            statement.setLong(1, guild.getIdLong());
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
    public void upCountInvite(Guild guild, String code, EventWrapper messageContext) {
        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT shepard_func.upcount_invite(?,?)")) {
            statement.setLong(1, guild.getIdLong());
            statement.setString(2, code);
            statement.execute();
        } catch (SQLException e) {
            handleException(e, messageContext);
        }
    }

    /**
     * Sets the counter of a invite +1.
     *
     * @param guild          Guild object for lookup
     * @param code           Code of the invite for upcount
     * @param messageContext messageContext from command sending for error handling. Can be null.
     */
    public boolean inviteRegistered(Guild guild, String code, EventWrapper messageContext) {
        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT shepard_func.upcount_invite(?,?)")) {
            statement.setLong(1, guild.getIdLong());
            statement.setString(2, code);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getBoolean(1);
            }
        } catch (SQLException e) {
            handleException(e, messageContext);
        }
        return false;
    }

    /**
     * Set the invite role for an invite.
     *
     * @param guild          Guild object for lookup
     * @param code           Code of the invite for upcount
     * @param role           role to receive when using the invite. null to remove
     * @param messageContext messageContext from command sending for error handling. Can be null.
     * @return true if the transaction was successful.
     */
    public boolean setInviteRole(Guild guild, String code, Role role, EventWrapper messageContext) {
        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT shepard_func.add_invite_role(?,?,?)")) {
            statement.setLong(1, guild.getIdLong());
            statement.setString(2, code);
            if (role == null) {
                statement.setNull(3, Types.BIGINT);
            } else {
                statement.setLong(3, role.getIdLong());

            }
            statement.execute();
        } catch (SQLException e) {
            handleException(e, messageContext);
            return false;
        }
        return true;

    }


    /**
     * Deletes all invites which are not present anymore.
     *
     * @param guild          Guild object for lookup
     * @param invites        List of invites of a guild
     * @param messageContext messageContext from command sending for error handling. Can be null.
     * @return true if the query execution was successful
     */
    public boolean updateInvite(Guild guild, List<Invite> invites, EventWrapper messageContext) {
        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT shepard_func.update_invites(?,?)")) {
            statement.setLong(1, guild.getIdLong());

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

