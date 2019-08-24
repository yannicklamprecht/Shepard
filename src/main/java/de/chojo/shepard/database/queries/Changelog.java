package de.chojo.shepard.database.queries;

import de.chojo.shepard.database.DatabaseConnector;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static de.chojo.shepard.database.DbUtil.getIdRaw;
import static de.chojo.shepard.database.DbUtil.handleException;

final class Changelog {

    private Changelog(){}
    public static void addRole(Guild guild, String roleId, MessageReceivedEvent event){
        try (PreparedStatement statement = DatabaseConnector.getConn().
                prepareStatement("SELECT shepard_func.add_changelog_role(?,?)")) {
            statement.setString(1, guild.getId());
            statement.setString(2, roleId);
            statement.execute();
        } catch (SQLException e) {
            handleException(e,event);
        }
    }
    public static void removeRole(Guild guild, String roleId, MessageReceivedEvent event){
        try (PreparedStatement statement = DatabaseConnector.getConn().
                prepareStatement("SELECT shepard_func.remove_changelog_role(?,?)")) {
            statement.setString(1, guild.getId());
            statement.setString(2, roleId);
            statement.execute();
        } catch (SQLException e) {
            handleException(e,event);
        }
    }
    public static void setChannel(Guild guild, String channelId, MessageReceivedEvent event){
        try (PreparedStatement statement = DatabaseConnector.getConn().
                prepareStatement("SELECT shepard_func.set_changelog_channel(?,?)")) {
            statement.setString(1, guild.getId());
            statement.setString(2, getIdRaw(channelId));
            statement.execute();
        } catch (SQLException e) {
            handleException(e,event);
        }
    }
    public static void removeChannel(Guild guild, MessageReceivedEvent event){
        try (PreparedStatement statement = DatabaseConnector.getConn().
                prepareStatement("SELECT shepard_func.remove_changelog_channel(?)")) {
            statement.setString(1, guild.getId());
            statement.execute();
        } catch (SQLException e) {
            handleException(e,event);
        }
    }

    public static List<String> getRoles(Guild guild, MessageReceivedEvent event) {
        try (PreparedStatement statement = DatabaseConnector.getConn().
                prepareStatement("SELECT shepard_func.get_changelog_roles(?)")) {
            statement.setString(1, guild.getId());
            ResultSet result = statement.executeQuery();
            if (result.getArray(1) != null) {
                return Arrays.asList((String[]) result.getArray(1).getArray());
            }
        } catch (SQLException e) {
            handleException(e, event);
        }
        return Collections.emptyList();
    }
    public static String getChannel(Guild guild, MessageReceivedEvent event) {
        try (PreparedStatement statement = DatabaseConnector.getConn().
                prepareStatement("SELECT shepard_func.get_changelog_channel(?)")) {
            statement.setString(1, guild.getId());
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
