package de.chojo.shepard.database;

import java.sql.*;


public final class DatabaseConnector {
    private static Connection conn = null;

    public static Connection getConn() {
        checkConnection();
        return conn;
    }

    private DatabaseConnector() { }

    private static void createConnection() {
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost/shepard?" + "user=root&password=");
        } catch (SQLException ex) {
            handleException(ex);
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

    public static void handleException(SQLException ex) {
        System.out.println("SQLException: " + ex.getMessage());
        System.out.println("SQLState: " + ex.getSQLState());
        System.out.println("VendorError: " + ex.getErrorCode());
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
