package de.eldoria.shepard.commandmodules.greeting.data;

import de.eldoria.shepard.commandmodules.greeting.types.GreetingSettings;
import de.eldoria.shepard.database.QueryObject;
import de.eldoria.shepard.wrapper.EventWrapper;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.sharding.ShardManager;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static de.eldoria.shepard.database.DbUtil.handleException;

public class GreetingData extends QueryObject {

    private final ShardManager shardManager;

    /**
     * Create a new greeting data object.
     *  @param shardManager    shardManager for user parsing
     * @param source data source for connection retrieving
     */
    public GreetingData(ShardManager shardManager, DataSource source) {
        super(source);
        this.shardManager = shardManager;
    }

    /**
     * Sets a greeting channel for a guild.
     *
     * @param guild          Guild object for which the channel should be added
     * @param channel        channel which should be used for greetings
     * @param messageContext messageContext from command sending for error handling. Can be null.
     * @return true if the query execution was successful
     */
    public boolean setGreetingChannel(Guild guild, MessageChannel channel,
                                      EventWrapper messageContext) {
        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT shepard_func.set_greeting_channel(?,?)")) {
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
     * Remove a greeting channel from a guild.
     *
     * @param guild          Guild object for lookup
     * @param messageContext messageContext from command sending for error handling. Can be null.
     * @return true if the query execution was successful
     */
    public boolean removeGreetingChannel(Guild guild, EventWrapper messageContext) {
        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT shepard_func.remove_greeting_channel(?)")) {
            statement.setString(1, guild.getId());
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
     * @param text           text for greeting
     * @param messageContext messageContext from command sending for error handling. Can be null.
     * @return true if the query execution was successful
     */
    public boolean setGreetingText(Guild guild, String text, EventWrapper messageContext) {
        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT shepard_func.set_greeting_text(?,?)")) {
            statement.setString(1, guild.getId());
            statement.setString(2, text);
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
    public GreetingSettings getGreeting(Guild guild) {
        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT * from shepard_func.get_greeting_data(?)")) {
            statement.setString(1, guild.getId());
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                return new GreetingSettings(shardManager,
                        guild.getId(),
                        result.getString("channel_id"),
                        result.getString("message"));
            }

        } catch (SQLException e) {
            handleException(e, null);
        }
        return null;
    }
}
