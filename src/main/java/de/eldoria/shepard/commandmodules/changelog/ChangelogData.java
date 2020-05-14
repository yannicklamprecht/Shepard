package de.eldoria.shepard.commandmodules.changelog;

import de.eldoria.shepard.database.QueryObject;
import de.eldoria.shepard.wrapper.EventWrapper;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static de.eldoria.shepard.database.DbUtil.handleException;

class ChangelogData extends QueryObject {

    /**
     * Create a new changelog data object.
     *
     * @param source for connection handling.
     */
    ChangelogData(DataSource source) {
        super(source);
    }

    /**
     * Adds a role to changelog observation.
     *
     * @param guild          guild on which the role should be added
     * @param role           id of the role id of the role
     * @param wrapper wrapper from command sending for error handling. Can be null.
     * @return true if the query execution was successful
     */
    public boolean addRole(Guild guild, Role role, EventWrapper wrapper) {
        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT shepard_func.add_changelog_role(?,?)")) {
            statement.setString(1, guild.getId());
            statement.setString(2, role.getId());
            statement.execute();
        } catch (SQLException e) {
            handleException(e, wrapper);
            return false;
        }
        return true;
    }

    /**
     * Remove a role from changelog observation.
     *
     * @param guild          Guild object for lookup
     * @param role           id of the role
     * @param messageContext messageContext from command sending for error handling. Can be null.
     * @return true if the query execution was successful
     */
    public boolean removeRole(Guild guild, Role role, EventWrapper messageContext) {
        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT shepard_func.remove_changelog_role(?,?)")) {
            statement.setString(1, guild.getId());
            statement.setString(2, role.getId());
            statement.execute();
        } catch (SQLException e) {
            handleException(e, messageContext);
            return false;
        }
        return true;
    }

    /**
     * Sets the changelog channel.
     *
     * @param guild          Guild object for lookup
     * @param channel        Id of the channel
     * @param messageContext messageContext from command sending for error handling. Can be null.
     * @return true if the query execution was successful
     */
    public boolean setChannel(Guild guild, TextChannel channel, EventWrapper messageContext) {
        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT shepard_func.set_changelog_channel(?,?)")) {
            statement.setString(1, guild.getId());
            statement.setString(2, channel.getId());
            statement.execute();
        } catch (SQLException e) {
            handleException(e, messageContext);
            return false;
        }
        return true;
    }

    /**
     * Removes the changelog channel.
     *
     * @param guild          Guild object for lookup
     * @param messageContext messageContext from command sending for error handling. Can be null.
     * @return true if the query execution was successful
     */
    public boolean removeChannel(Guild guild, EventWrapper messageContext) {
        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT shepard_func.remove_changelog_channel(?)")) {
            statement.setString(1, guild.getId());
            statement.execute();
        } catch (SQLException e) {
            handleException(e, messageContext);
            return false;
        }
        return true;
    }

    /**
     * Get a list of all observed roles.
     *
     * @param guild          Guild object for lookup
     * @param messageContext messageContext from command sending for error handling. Can be null.
     * @return list of role ids
     */
    public List<String> getRoles(Guild guild, EventWrapper messageContext) {
        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT shepard_func.get_changelog_roles(?)")) {
            statement.setString(1, guild.getId());
            ResultSet result = statement.executeQuery();
            if (result.next() && result.getArray(1) != null) {
                return Arrays.asList((String[]) result.getArray(1).getArray());
            }
        } catch (SQLException e) {
            handleException(e, messageContext);
        }
        return Collections.emptyList();
    }

    /**
     * Get the changelog channel of the guild.
     *
     * @param guild          Guild object for lookup
     * @param messageContext messageContext from command sending for error handling. Can be null.
     * @return channel id as string
     */
    public String getChannel(Guild guild, EventWrapper messageContext) {
        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT shepard_func.get_changelog_channel(?)")) {
            statement.setString(1, guild.getId());
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                return result.getString(1);
            }
        } catch (SQLException e) {
            handleException(e, messageContext);
        }
        return null;
    }
}
