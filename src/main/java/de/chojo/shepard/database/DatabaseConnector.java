package de.chojo.shepard.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;


public class DatabaseConnector {

    private static DatabaseConnector instance;

    private static Connection conn = null;

    public static DatabaseConnector getInstance() {
        if (instance == null) {
            synchronized (DatabaseConnector.class) {
                if (instance == null) {
                    instance = new DatabaseConnector();
                }
            }
        }
        return instance;
    }

    private DatabaseConnector() {
        CreateConnection();
    }

    private static void CreateConnection() {
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost/shepard?" + "user=root&password=");
        } catch (SQLException ex) {
            handleException(ex);
        }
    }

    public static ArrayList<DatabaseInvite> getInvites(String discordServerId) {
        CheckConnection();
        Statement stmt = null;
        ResultSet rs = null;

        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM invites WHERE server_id = '" + DatabaseCache.getInstance().getServerId(discordServerId) + "'");

            ArrayList<DatabaseInvite> invites = new ArrayList<>();
            while (rs.next()) {
                invites.add(new DatabaseInvite(rs.getString("code"), rs.getString("name"), rs.getInt("count")));
            }
            return invites;

        } catch (SQLException ex) {
            handleException(ex);
        } finally {
            close(stmt, rs);
        }
        return null;
    }

    public static HashMap<String, Integer> getInternalServers() {
        CheckConnection();
        Statement stmt = null;
        ResultSet rs = null;

        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM server");

            HashMap<String, Integer> internalServers = new HashMap<>();
            rs.next();
            while (rs.next()) {
                internalServers.put(rs.getString("discord_server_id"), rs.getInt("id"));
                if (rs.isLast()) {
                    break;
                }
            }
            DatabaseCache.getInstance().setDatabaseServers(internalServers);
            return internalServers;

        } catch (SQLException ex) {
            handleException(ex);
        } finally {
            close(stmt, rs);
        }
        return null;
    }


    public static int getInternalServerID(String discordId) {
        CheckConnection();
        Statement stmt = null;
        ResultSet rs = null;

        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM server WHERE discord_server_id = '" + discordId + "';");

            if (rs.next()) {
                return rs.getInt("id");
            } else {
                return createInternalServer(discordId);
            }

        } catch (SQLException ex) {
            handleException(ex);
        } finally {
            close(stmt, rs);
        }
        return 0;
    }

    private static int createInternalServer(String discordId) {
        CheckConnection();
        Statement stmt = null;
        ResultSet rs = null;

        try {
            stmt = conn.createStatement();


            stmt.execute("INSERT INTO server (discord_server_id) VALUES ('" + discordId + "');");

            stmt.close();

            while (true) {
                stmt = conn.createStatement();

                rs = stmt.executeQuery("SELECT * FROM server WHERE discord_server_id = '" + discordId + "';");

                if (rs.next()) {
                    getInternalServers();
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

    private static void close(Statement stmt, ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException sqlEx) {
            } // ignore

            rs = null;
        }

        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException sqlEx) {
            } // ignore

            stmt = null;
        }
    }

    private static void handleException(SQLException ex) {
        System.out.println("SQLException: " + ex.getMessage());
        System.out.println("SQLState: " + ex.getSQLState());
        System.out.println("VendorError: " + ex.getErrorCode());
    }

    private static void CheckConnection() {
        try {
            if (conn == null || conn.isClosed()) {
                CreateConnection();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
