package de.eldoria.shepard.configuration;

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
    private String port = null;
}
