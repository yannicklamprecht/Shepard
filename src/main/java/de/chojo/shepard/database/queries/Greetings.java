package de.chojo.shepard.database.queries;

import de.chojo.shepard.database.DatabaseConnector;
import de.chojo.shepard.database.types.Greeting;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static de.chojo.shepard.database.DbUtil.handleException;

public class Greetings {
    public static void setGreetingChannel(String guildId, String channelId, MessageReceivedEvent event) {
        try (PreparedStatement statement = DatabaseConnector.getConn().
                prepareStatement("SELECT shepard_func.set_greeting_channel(?,?)")) {
            statement.setString(1, guildId);
            statement.setString(2, channelId);
            statement.execute();
        } catch (SQLException e) {
            handleException(e, event);
        }
    }

    public static void removeGreetingChannel(String guildId, MessageReceivedEvent event) {
        try (PreparedStatement statement = DatabaseConnector.getConn().
                prepareStatement("SELECT shepard_func.remove_greeting_channel(?)")) {
            statement.setString(1, guildId);
            statement.execute();
        } catch (SQLException e) {
            handleException(e, event);
        }
    }

    public static void setGreetingText(String guildId, String text, MessageReceivedEvent event) {
        try (PreparedStatement statement = DatabaseConnector.getConn().
                prepareStatement("SELECT shepard_func.set_greeting_text(?,?)")) {
            statement.setString(1, guildId);
            statement.setString(2, text);
            statement.execute();
        } catch (SQLException e) {
            handleException(e, event);
        }
    }

    public static Greeting getGreeting(String guildId) {
        try (PreparedStatement statement = DatabaseConnector.getConn().
                prepareStatement("SELECT shepard_func.set_greeting_data(?)")) {
            statement.setString(1, guildId);
            ResultSet result= statement.executeQuery();
            if(result.next()){
                return new Greeting(guildId,
                        result.getString("channel_id"),
                        result.getString("message"));
            }
        } catch (SQLException e) {
            handleException(e, null);
        }
        return null;
    }


}
