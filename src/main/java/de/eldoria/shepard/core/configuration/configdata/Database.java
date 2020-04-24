package de.eldoria.shepard.core.configuration.configdata;

import lombok.Data;

/**
 * Class to save Database Configuration.
 */
@Data
public class Database {
    /**
     * Database username.
     */
    private String username = null;
    /**
     * Database password.
     */
    private String password = null;
    /**
     * Database name.
     */
    private String db = null;
    /**
     * Database address.
     */
    private String address = null;
    /**
     * Database port.
     */
    private int port;

    /**
     * The version settings of the database.
     */
    private DatabaseVersion databaseVersion;
}
