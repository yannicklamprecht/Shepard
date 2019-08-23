package de.chojo.shepard.database;

import de.chojo.shepard.ShepardBot;
import de.chojo.shepard.configuration.Database;

import java.sql.*;

import static de.chojo.shepard.database.DbUtil.handleException;


public final class DatabaseConnector {
    private static Connection conn = null;

    public static Connection getConn() {
        checkConnection();
        return conn;
    }

    private DatabaseConnector() {
    }

    private static void createConnection() {
        Database config = ShepardBot.getConfig().getDatabase();
        try {
            conn = DriverManager.getConnection(String.format("jdbc:postgresql://%s:%s/%s", config.getAdress(), config.getPort(), config.getDb()), config.getUsername(), config.getPassword());
            System.out.println(
                    String.format("SQL connection established on %s:%s@%s:%s/%s",
                            config.getUsername(), config.getPassword(), config.getAdress(), config.getPort(), config.getDb()));
        } catch (SQLException ex) {
            handleException(ex, null);
        }

    }

    public static void close(Statement stmt, ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException ignore) { } // ignore
        }

        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException ignore) { } // ignore
        }
    }


    private static void checkConnection() {
        try {
            if (conn == null || conn.isClosed()) {
                createConnection();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
