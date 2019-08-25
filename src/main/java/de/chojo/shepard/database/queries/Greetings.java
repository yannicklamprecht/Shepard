package de.chojo.shepard.database.queries;

import de.chojo.shepard.database.DatabaseConnector;
import de.chojo.shepard.database.types.Greeting;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.w3c.dom.Text;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static de.chojo.shepard.database.DbUtil.handleException;

public final class Greetings {

    private Greetings() {
    }

    /**
     * Sets a greeting channel for a guild.
     *
     * @param guild     Guild object for which the channel should be added
     * @param channel channel which should be used for greetings
     * @param event     event from command sending for error handling. Can be null.
     */
    public static void setGreetingChannel(Guild guild, TextChannel channel, MessageReceivedEvent event) {
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
    public static Greeting getGreeting(Guild guild) {
        try (PreparedStatement statement = DatabaseConnector.getConn()
                .prepareStatement("SELECT shepard_func.set_greeting_data(?)")) {
            statement.setString(1, guild.getId());
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                return new Greeting(guild.getId(),
                        result.getString("channel_id"),
                        result.getString("message"));
            }

        } catch (SQLException e) {
            handleException(e, null);
        }
        return null;
    }


}
