package de.chojo.shepard.database;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Invite;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import static de.chojo.shepard.database.DbUtil.getIdRaw;
import static de.chojo.shepard.database.DbUtil.handleException;

/**
 * A class for querying and updating tha database.
 */
@Deprecated
public final class DatabaseQuery {

    private DatabaseQuery() { }

    /**
     * Get a set off all invites from a guild which where saved to the database.
     *
     * @param guildId the guild's id to look for.
     * @return a set of all invites.
     */
    public static Set<DatabaseInvite> getInvites(String guildId) {
        try (PreparedStatement statement = DatabaseConnector.getConn().prepareStatement("SELECT * FROM invites WHERE server_id=?")) {
            statement.setInt(1, getInternalServerID(guildId));
            ResultSet set = statement.executeQuery();
            Set<DatabaseInvite> invites = new HashSet<>();
            while (set.next()) {
                invites.add(new DatabaseInvite(set.getString("code"), set.getString("name"), set.getInt("count")));
            }
            return invites;
        } catch (SQLException e) {
            handleException(e);
        }
        return Collections.emptySet();
    }

    /**
     * Get the internal auto-incremented id associated with a guild.
     *
     * @param guildId the guild's id to look for.
     * @return the numeric id. May return {@code -1} if an error occurred.
     */
    public static int getInternalServerID(String guildId) {
        try (PreparedStatement statement = DatabaseConnector.getConn().prepareStatement("SELECT * FROM server WHERE discord_server_id=?")) {
            statement.setString(1, guildId);
            ResultSet set = statement.executeQuery();
            if (set.next()) {
                return set.getInt("id");
            } else {
                return DatabaseQuery.createInternalServer(guildId);
            }
        } catch (SQLException e) {
            handleException(e);
        }
        return -1;
    }

    /**
     * Get the greeting channel of a guild.
     *
     * @param guildId the guild's id to look for.
     * @return the id of the channel.
     */
    public static String getGreetingChannel(String guildId) {
        guildId = getIdRaw(guildId);
        try (PreparedStatement statement = DatabaseConnector.getConn().prepareStatement("SELECT * FROM greeting WHERE id=?")) {
            statement.setInt(1, getInternalServerID(guildId));
            ResultSet set = statement.executeQuery();
            if (set.first()) {
                return set.getString("channel");
            }
        } catch (SQLException e) {
            handleException(e);
        }
        return "";
    }

    /**
     * Update the greeting channel for a guild.
     *
     * @param guildId the guild's id to look for.
     * @param channelId the channel's id to look for.
     */
    public static void saveGreetingChannel(String guildId, String channelId){
        int id = getInternalServerID(guildId);
        try (PreparedStatement statement = DatabaseConnector.getConn().prepareStatement("INSERT INTO greeting (id, channel) VALUES (?, ?) ON DUPLICATE KEY UPDATE channel=?")) {
            statement.setInt(1, id);
            statement.setString(2, channelId);
            statement.setString(3, channelId);
            statement.executeUpdate();

        } catch (SQLException ex){
            handleException(ex);
        }
    }

    /**
     * Save a new invite associated with a service to the database
     *
     * @param invite the invite to save.
     * @param name the associated service name.
     * @param guildId the guild's id to save the invites for.
     */
    public static void saveInvite(Invite invite, String name, String guildId) {
        try (PreparedStatement statement = DatabaseConnector.getConn().prepareStatement("INSERT INTO invites (server_id, code, name, count) VALUES (?, ?, ?, ?)")) {
            statement.setInt(1, getInternalServerID(getIdRaw(guildId)));
            statement.setString(2, invite.getCode());
            statement.setString(3, name);
            statement.setInt(4, invite.getUses());
            statement.executeUpdate();
        } catch (SQLException e) {
            handleException(e);
        }
    }

    /**
     * Update the uses of a specific invite.
     *
     * @param invite the invite to update.
     */
    public static void updateInvite(Invite invite) {
        try (PreparedStatement statement = DatabaseConnector.getConn().prepareStatement("UPDATE invites SET count=? WHERE code=?")) {
            statement.setInt(1, invite.getUses());
            statement.setString(2, invite.getCode());
            statement.executeUpdate();
        } catch (SQLException e) {
            handleException(e);
        }
    }

    /**
     * Save a property for a guild to the database.
     *
     * @param guild the guild to save the property for.
     * @param key the property key.
     * @param value the property value.
     */
    public static void saveProperty(Guild guild, String key, String value) {
        try (PreparedStatement statement = DatabaseConnector.getConn()
                .prepareStatement("INSERT INTO settings (guild_id, property_key, property_value) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE property_value=?")) {
            statement.setInt(1, getInternalServerID(guild.getId()));
            statement.setString(2, key);
            statement.setString(3, value);
            statement.setString(4, value);
            statement.executeUpdate();
        } catch (SQLException e) {
            handleException(e);
        }
    }

    /**
     * Load the properties of a guild.
     *
     * @param guild the guild to load properties for.
     * @return the properties loaded for the given guild.
     */
    public static Properties loadProperties(Guild guild) {
        try (PreparedStatement statement = DatabaseConnector.getConn().prepareStatement("SELECT * FROM settings WHERE guild_id=?")) {
            statement.setInt(1, getInternalServerID(guild.getId()));
            ResultSet set = statement.executeQuery();
            Properties properties = new Properties(set.getFetchSize());
            while (set.next()) {
                properties.setProperty(set.getString("property_key"), set.getString("property_value"));
            }
            return properties;
        } catch (SQLException e) {
            handleException(e);
        }
        return new Properties();
    }


    /**
     * Create a database entry for a guild.
     *
     * @param guildId the guild to create the database entry for.
     * @return the id associated with the guild.
     */
    private static int createInternalServer(String guildId) {
        guildId = getIdRaw(guildId);

        try (PreparedStatement createStatement = DatabaseConnector.getConn()
                .prepareStatement("INSERT INTO server (discord_server_id) VALUES (?);");
             PreparedStatement receiveStatement = DatabaseConnector.getConn()
                     .prepareStatement("SELECT id FROM server WHERE discord_server_id=?;")) {

            createStatement.setString(1, guildId);
            createStatement.executeUpdate();
            receiveStatement.setString(1, guildId);
            ResultSet set = receiveStatement.executeQuery();
            if (set.first()) {
                return set.getInt("id");
            }
        } catch (SQLException e) {
            handleException(e);
        }
        return -1;
    }
}
