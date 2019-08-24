package de.chojo.shepard.database.queries;

import de.chojo.shepard.database.DatabaseConnector;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static de.chojo.shepard.database.DbUtil.handleException;

public class Changelog {
    public static void addRole(String guildId, String roleId, MessageReceivedEvent event){
        try (PreparedStatement statement = DatabaseConnector.getConn().
                prepareStatement("SELECT shepard_func.add_changelog_role(?,?)")) {
            statement.setString(1, guildId);
            statement.setString(2, roleId);
            statement.execute();
        } catch (SQLException e) {
            handleException(e,event);
        }
    }
    public static void removeRole(String guildId, String roleId, MessageReceivedEvent event){
        try (PreparedStatement statement = DatabaseConnector.getConn().
                prepareStatement("SELECT shepard_func.remove_changelog_role(?,?)")) {
            statement.setString(1, guildId);
            statement.setString(2, roleId);
            statement.execute();
        } catch (SQLException e) {
            handleException(e,event);
        }
    }
    public static void setChannel(String guildId, String channelId, MessageReceivedEvent event){
        try (PreparedStatement statement = DatabaseConnector.getConn().
                prepareStatement("SELECT shepard_func.set_changelog_channel(?,?)")) {
            statement.setString(1, guildId);
            statement.setString(2, channelId);
            statement.execute();
        } catch (SQLException e) {
            handleException(e,event);
        }
    }
    public static void removeChannel(String guildId, MessageReceivedEvent event){
        try (PreparedStatement statement = DatabaseConnector.getConn().
                prepareStatement("SELECT shepard_func.remove_changelog_channel(?)")) {
            statement.setString(1, guildId);
            statement.execute();
        } catch (SQLException e) {
            handleException(e,event);
        }
    }

    public static List<String> getRoles(String guildId, MessageReceivedEvent event) {
        try (PreparedStatement statement = DatabaseConnector.getConn().
                prepareStatement("SELECT shepard_func.get_changelog_roles(?)")) {
            statement.setString(1, guildId);
            ResultSet result = statement.executeQuery();
            if (result.getArray(1) != null) {
                return Arrays.asList((String[]) result.getArray(1).getArray());
            }
        } catch (SQLException e) {
            handleException(e, event);
        }
        return Collections.emptyList();
    }
    public static String getChannel(String guildId, MessageReceivedEvent event) {
        try (PreparedStatement statement = DatabaseConnector.getConn().
                prepareStatement("SELECT shepard_func.get_changelog_channel(?)")) {
            statement.setString(1, guildId);
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                return result.getString(1);
            }
        } catch (SQLException e) {
            handleException(e, event);
        }
        return null;
    }
}
