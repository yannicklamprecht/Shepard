package de.eldoria.shepard.database.queries;

import de.eldoria.shepard.database.DatabaseConnector;
import de.eldoria.shepard.database.types.MinecraftLink;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static de.eldoria.shepard.database.DbUtil.getIdRaw;
import static de.eldoria.shepard.database.DbUtil.handleException;

public final class MinecraftLinkData {
    private MinecraftLinkData() {
    }

    /**
     * Get a Minecraft link by user.
     *
     * @param user  User object.
     * @param event event from command sending for error handling. Can be null.
     * @return Minecraft Link object or null if no link was found
     */
    public static MinecraftLink getLinkByUserId(User user, MessageReceivedEvent event) throws SQLException {
        try (PreparedStatement statement = DatabaseConnector.getConn()
                .prepareStatement("SELECT * from shepard_func.get_minecraft_link_user_id(?)")) {
            statement.setString(1, getIdRaw(user.getId()));
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                return new MinecraftLink(user, result.getString("uuid"));
            }
        } catch (SQLException e) {
            handleException(e, event);
        }
        return null;
    }

    /**
     * Get a Minecraft link by uuid of a minecraft account.
     *
     * @param uuid  uuid of a minecraft account
     * @param event event from command sending for error handling. Can be null.
     * @return Minecraft Link object or null if no link was found
     */
    public static MinecraftLink getLinkByUUID(String uuid, MessageReceivedEvent event) throws SQLException {
        try (PreparedStatement statement = DatabaseConnector.getConn()
                .prepareStatement("SELECT * from shepard_func.get_minecraft_link_uuid(?)")) {
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

    /**
     * Add a link code to database.
     *
     * @param code  Code to add
     * @param uuid  uuid of player
     * @param event event from command sending for error handling. Can be null.
     */
    public static void addLinkCode(String code, String uuid, MessageReceivedEvent event) throws SQLException {
        try (PreparedStatement statement = DatabaseConnector.getConn()
                .prepareStatement("SELECT * from shepard_func.add_minecraft_link_code(?,?)")) {
            statement.setString(1, code);
            statement.setString(2, uuid);
            statement.execute();
        } catch (SQLException e) {
            handleException(e, event);
        }
    }

    /**
     * Get a uuid by code.
     *
     * @param code  code for lookup
     * @param event event from command sending for error handling. Can be null.
     * @return UUID as String.
     */
    public static String getUUIDByCode(String code, MessageReceivedEvent event) throws SQLException {
        try (PreparedStatement statement = DatabaseConnector.getConn()
                .prepareStatement("SELECT * from shepard_func.add_minecraft_link_code(?)")) {
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
