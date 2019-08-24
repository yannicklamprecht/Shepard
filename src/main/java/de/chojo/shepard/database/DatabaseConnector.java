package de.chojo.shepard.database;

import de.chojo.shepard.ShepardBot;
import de.chojo.shepard.configuration.Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static de.chojo.shepard.database.DbUtil.handleException;


public final class DatabaseConnector {
    private static Connection conn = null;

    private DatabaseConnector() {
    }

    /**
     * Returns a connection.
     *
     * @return Connection object.
     */
    public static Connection getConn() {
        checkConnection();
        return conn;
    }

    private static void createConnection() {
        Database config = ShepardBot.getConfig().getDatabase();
        try {
            conn = DriverManager.getConnection(String.format("jdbc:postgresql://%s:%s/%s",
                    config.getAddress(), config.getPort(), config.getDb()),
                    config.getUsername(), config.getPassword());
            System.out.println(
                    String.format("SQL connection established on %s:%s@%s:%s/%s",
                            config.getUsername(), config.getPassword(), config.getAddress(),
                            config.getPort(), config.getDb()));
        } catch (SQLException ex) {
            handleException(ex, null);
        }
    }


    /**
     * close statement.
     *
     * @param stmt statement
     */
    public static void close(Statement stmt) {
        close(stmt, null);
    }

    /**
     * close statement.
     *
     * @param stmt statement
     * @param rs   result set
     */
    public static void close(Statement stmt, ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                handleException(e, null);
            } // ignore
        }

        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                handleException(e, null);
            } // ignore
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
