package de.chojo.shepard.database;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Invite;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static de.chojo.shepard.database.DatabaseConnector.close;
import static de.chojo.shepard.database.DatabaseConnector.handleException;

public class DatabaseQuery {
    private static final Pattern ID_PATTERN = Pattern.compile("(?:<[@#!&]{1,2})?(?<id>[0-9]{18})(?:>)?");

    static int createInternalServer(String discordId) {
        discordId = getIdRaw(discordId);

        Statement stmt = null;
        ResultSet rs = null;

        try {
            stmt = DatabaseConnector.getConn().createStatement();
            stmt.execute("INSERT INTO server (discord_server_id) VALUES ('" + discordId + "');");
            stmt.close();

            while (true) {
                stmt = DatabaseConnector.getConn().createStatement();

                rs = stmt.executeQuery("SELECT * FROM server WHERE discord_server_id = '" + discordId + "';");

                if (rs.next()) {
                    getInternalServerID(discordId);
                    return rs.getInt("id");
                }
                Thread.sleep(250);
            }


        } catch (SQLException ex) {
            handleException(ex);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    public static HashMap<String, DatabaseInvite> getInvites(String discordServerId) {
        Statement stmt = null;
        ResultSet rs = null;

        try {
            stmt = DatabaseConnector.getConn().createStatement();
            rs = stmt.executeQuery("SELECT * FROM invites WHERE server_id = '" + getInternalServerID(discordServerId) + "'");

            HashMap<String, DatabaseInvite> invites = new HashMap<>();
            while (rs.next()) {
                invites.put(rs.getString("code"), new DatabaseInvite(rs.getString("code"), rs.getString("name"), rs.getInt("count")));
            }
            return invites;

        } catch (SQLException ex) {
            handleException(ex);
        } finally {
            close(stmt, rs);
        }
        return new HashMap<>();
    }

    public static int getInternalServerID(String discordId) {
        Statement stmt = null;
        ResultSet rs = null;

        try {
            stmt = DatabaseConnector.getConn().createStatement();
            rs = stmt.executeQuery("SELECT * FROM server WHERE discord_server_id = '" + discordId + "';");

            if (rs.next()) {
                return rs.getInt("id");
            } else {
                return DatabaseQuery.createInternalServer(discordId);
            }

        } catch (SQLException ex) {
            handleException(ex);
        } finally {
            close(stmt, rs);
        }
        return 0;
    }

    public static String getGreetingChannel(String discordId) {
        discordId = getIdRaw(discordId);
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = DatabaseConnector.getConn().createStatement();
            rs = stmt.executeQuery("SELECT * FROM greeting WHERE id = '" + getInternalServerID(discordId) + "';");
            if (rs.next()) {
                return rs.getString("channel");
            }
        } catch (SQLException ex) {
            handleException(ex);
        } finally {
            close(stmt, rs);
        }
        return "";
    }

    public static void saveGreetingChannel(String discordId, String channel){
        PreparedStatement stmt;

        int id = getInternalServerID(discordId);
        try {
            stmt = DatabaseConnector.getConn().prepareStatement("INSERT INTO greeting (id, channel) VALUES (?, ?) ON DUPLICATE KEY UPDATE channel=?");
            stmt.setInt(1, id);
            stmt.setString(2, channel);
            stmt.setString(3, channel);
            stmt.executeUpdate();
            // stmt.executeQuery("INSERT INTO greeting (id, channel) VALUES (:id, :channel) ON DUPLICATE KEY UPDATE channel=:channel");

        }catch (SQLException ex){
            handleException(ex);
        }
    }

    public static void saveInvite(Invite invite, String name, String serverId) {
        try (PreparedStatement statement = DatabaseConnector.getConn().prepareStatement("INSERT INTO invites (server_id, code, name, count) VALUES (?, ?, ?, ?)")) {
            statement.setInt(1, getInternalServerID(getIdRaw(serverId)));
            statement.setString(2, invite.getCode());
            statement.setString(3, name);
            statement.setInt(4, invite.getUses());
            statement.executeUpdate();
        } catch (SQLException e) {
            handleException(e);
        }
    }

    public static void updateInvite(Invite invite) {
        try (PreparedStatement statement = DatabaseConnector.getConn().prepareStatement("UPDATE invites SET count=? WHERE code=?")) {
            statement.setInt(1, invite.getUses());
            statement.setString(2, invite.getCode());
            statement.executeUpdate();
        } catch (SQLException e) {
            handleException(e);
        }
    }

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

    private static String getIdRaw(String id){
        Matcher matcher = ID_PATTERN.matcher(id);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("lol dis is not a channel");
        }
        return matcher.group(1);
    }
}
