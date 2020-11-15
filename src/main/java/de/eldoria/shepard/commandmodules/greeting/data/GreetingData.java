package de.eldoria.shepard.commandmodules.greeting.data;

import de.eldoria.shepard.commandmodules.greeting.types.GreetingSettings;
import de.eldoria.shepard.database.QueryObject;
import de.eldoria.shepard.wrapper.EventWrapper;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.jetbrains.annotations.Nullable;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import static de.eldoria.shepard.database.DbUtil.handleException;

public class GreetingData extends QueryObject {

    private final ShardManager shardManager;

    /**
     * Create a new greeting data object.
     *
     * @param shardManager shardManager for user parsing
     * @param source       data source for connection retrieving
     */
    public GreetingData(ShardManager shardManager, DataSource source) {
        super(source);
        this.shardManager = shardManager;
    }

    /**
     * Sets a greeting channel for a guild.
     *
     * @param guild          Guild object for which the channel should be added
     * @param channel        channel which should be used for greetings. null to remove
     * @param messageContext messageContext from command sending for error handling. Can be null.
     * @return true if the query execution was successful
     */
    public boolean setGreetingChannel(Guild guild, @Nullable MessageChannel channel,
                                      EventWrapper messageContext) {
        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT shepard_func.set_greeting_channel(?,?)")) {
            statement.setLong(1, guild.getIdLong());
            if (channel == null) {
                statement.setNull(2, Types.BIGINT);
            } else {
                statement.setLong(2, channel.getIdLong());
            }
            statement.execute();
        } catch (SQLException e) {
            handleException(e, messageContext);
            return false;
        }
        return true;
    }

    /**
     * Sets the greeting text for a guild.
     *
     * @param guild          Guild object for lookup
     * @param message        text for greeting
     * @param messageContext messageContext from command sending for error handling. Can be null.
     * @return true if the query execution was successful
     */
    public boolean setGreetingMessage(Guild guild, String message, EventWrapper messageContext) {
        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT shepard_func.set_greeting_message(?,?)")) {
            statement.setLong(1, guild.getIdLong());
            statement.setString(2, message);
            statement.execute();
        } catch (SQLException e) {
            handleException(e, messageContext);
            return false;
        }
        return true;
    }

    /**
     * Sets the private greeting text for a guild.
     *
     * @param guild          Guild object for lookup
     * @param message        text for greeting. Null to remove.
     * @param messageContext messageContext from command sending for error handling. Can be null.
     * @return true if the query execution was successful
     */
    public boolean setPrivateGreetingMessage(Guild guild, @Nullable String message, EventWrapper messageContext) {
        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT shepard_func.set_private_greeting_message(?,?)")) {
            statement.setLong(1, guild.getIdLong());
            if (message == null) {
                statement.setNull(2, Types.VARCHAR);
            } else {
                statement.setString(2, message);
            }
            statement.execute();
        } catch (SQLException e) {
            handleException(e, messageContext);
            return false;
        }
        return true;
    }

    /**
     * Sets the join role for a guild.
     *
     * @param guild          Guild object for lookup
     * @param role           role to set. Null to remove
     * @param messageContext messageContext from command sending for error handling. Can be null.
     * @return true if the query execution was successful
     */
    public boolean setJoinRole(Guild guild, @Nullable Role role, EventWrapper messageContext) {
        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT shepard_func.set_join_role(?,?)")) {
            statement.setLong(1, guild.getIdLong());
            if (role == null) {
                statement.setNull(2, Types.BIGINT);
            } else {
                statement.setLong(2, role.getIdLong());
            }
            statement.execute();
        } catch (SQLException e) {
            handleException(e, messageContext);
            return false;
        }
        return true;
    }

    /**
     * Get a greeting object for a guild.
     *
     * @param guild Guild object for lookup.
     * @return Greeting object
     */
    public @Nullable GreetingSettings getGreeting(Guild guild) {
        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT * FROM shepard_func.get_greeting_data(?)")) {
            statement.setLong(1, guild.getIdLong());
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                return new GreetingSettings(shardManager,
                        guild.getIdLong(),
                        result.getLong("channel_id"),
                        result.getString("message"),
                        result.getString("private_message"),
                        result.getLong("role")
                );
            }

        } catch (SQLException e) {
            handleException(e, null);
        }
        return null;
    }
}
