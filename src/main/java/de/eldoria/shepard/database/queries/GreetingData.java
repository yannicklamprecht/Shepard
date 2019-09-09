package de.eldoria.shepard.database.queries;

import de.eldoria.shepard.database.DatabaseConnector;
import de.eldoria.shepard.database.types.GreetingSettings;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static de.eldoria.shepard.database.DbUtil.handleException;

public final class GreetingData {

    private GreetingData() {
    }

    /**
     * Sets a greeting channel for a guild.
     *
     * @param guild     Guild object for which the channel should be added
     * @param channel channel which should be used for greetings
     * @param event     event from command sending for error handling. Can be null.
     */
    public static void setGreetingChannel(Guild guild, MessageChannel channel, MessageReceivedEvent event) {
        try (PreparedStatement statement = DatabaseConnector.getConn()
                .prepareStatement("SELECT shepard_func.set_greeting_channel(?,?)")) {
            statement.setString(1, guild.getId());
            statement.setString(2, channel.getId());
            statement.execute();
        } catch (SQLException e) {
            handleException(e, event);
        }
    }

    /**
     * Remove a greeting channel from a guild.
     *
     * @param guild Guild object for lookup
     * @param event event from command sending for error handling. Can be null.
     */
    public static void removeGreetingChannel(Guild guild, MessageReceivedEvent event) {
        try (PreparedStatement statement = DatabaseConnector.getConn()
                .prepareStatement("SELECT shepard_func.remove_greeting_channel(?)")) {
            statement.setString(1, guild.getId());
            statement.execute();
        } catch (SQLException e) {
            handleException(e, event);
        }
    }

    /**
     * Sets the greeting text for a guild.
     *
     * @param guild Guild object for lookup
     * @param text text for greeting
     * @param event event from command sending for error handling. Can be null.
     */
    public static void setGreetingText(Guild guild, String text, MessageReceivedEvent event) {
        try (PreparedStatement statement = DatabaseConnector.getConn()
                .prepareStatement("SELECT shepard_func.set_greeting_text(?,?)")) {
            statement.setString(1, guild.getId());
            statement.setString(2, text);
            statement.execute();
        } catch (SQLException e) {
            handleException(e, event);
        }
    }

    /**
     * Get a greeting object for a guild.
     *
     * @param guild Guild object for lookup.
     * @return Greeting object
     */
    public static GreetingSettings getGreeting(Guild guild) {
        try (PreparedStatement statement = DatabaseConnector.getConn()
                .prepareStatement("SELECT * from shepard_func.get_greeting_data(?)")) {
            statement.setString(1, guild.getId());
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                return new GreetingSettings(guild.getId(),
                        result.getString("channel_id"),
                        result.getString("message"));
            }

        } catch (SQLException e) {
            handleException(e, null);
        }
        return null;
    }


}
