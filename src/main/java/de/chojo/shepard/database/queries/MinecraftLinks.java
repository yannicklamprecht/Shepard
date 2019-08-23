package de.chojo.shepard.database.queries;

import de.chojo.shepard.database.DatabaseConnector;
import de.chojo.shepard.database.types.MinecraftLink;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static de.chojo.shepard.database.DbUtil.getIdRaw;
import static de.chojo.shepard.database.DbUtil.handleException;

public class MinecraftLinks {
    public static void setMinecraftLink(String userId, String uuid, MessageReceivedEvent event) {
        try (PreparedStatement statement = DatabaseConnector.getConn().
                prepareStatement("SELECT shepard_func.set_minecraft_link(?,?)")) {
            statement.setString(1, userId);
            statement.setString(2, uuid);
            statement.execute();
        } catch (SQLException e) {
            handleException(e, event);
        }
    }

    public static MinecraftLink getLinkByUserId(String userId, MessageReceivedEvent event) {
        try (PreparedStatement statement = DatabaseConnector.getConn().
                prepareStatement("SELECT * from shepard_func.get_minecraft_link_user_id(?)")) {
            statement.setString(1, getIdRaw(userId));
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                return new MinecraftLink(userId, result.getString("uuid"));
            }
        } catch (SQLException e) {
            handleException(e, event);
        }
        return null;
    }

    public static MinecraftLink getLinkByUUID(String uuid, MessageReceivedEvent event) {
        try (PreparedStatement statement = DatabaseConnector.getConn().
                prepareStatement("SELECT * from shepard_func.get_minecraft_link_uuid(?)")) {
            statement.setString(1, getIdRaw(uuid));
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                return new MinecraftLink(result.getString("user_id"), uuid.replace("-", ""));
            }
        } catch (SQLException e) {
            handleException(e, event);
        }
        return null;
    }

    public static void addLinkCode(String code, String uuid, MessageReceivedEvent event) {
        try (PreparedStatement statement = DatabaseConnector.getConn().
                prepareStatement("SELECT * from shepard_func.add_minecraft_link_code(?,?)")) {
            statement.setString(1, code);
            statement.setString(2, uuid);
            ResultSet result = statement.executeQuery();
        } catch (SQLException e) {
            handleException(e, event);
        }
    }

    public static String getUuidByCode(String code, MessageReceivedEvent event) {
        try (PreparedStatement statement = DatabaseConnector.getConn().
                prepareStatement("SELECT * from shepard_func.add_minecraft_link_code(?)")) {
            statement.setString(1, code);
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
