package de.eldoria.shepard.database;

import de.eldoria.shepard.ShepardBot;
import de.eldoria.shepard.configuration.Database;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static de.eldoria.shepard.database.DbUtil.handleExceptionAndIgnore;

@Slf4j
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
            log.info("SQL connection established on {}:*******@{}:{}/{}", config.getUsername(), config.getAddress(), config.getPort(), config.getDb());
        } catch (SQLException ex) {
            handleExceptionAndIgnore(ex, null);
        }
    }

    private static void checkConnection() {
        try {
            if (conn == null || conn.isClosed()) {
                createConnection();
            }
        } catch (SQLException e) {
            log.error("failed to create db connect", e);
        }
    }
}
