package de.chojo.shepard.configuration;

public class Database {
    private String username = null;
    private String password = null;
    private String db = null;
    private String address = null;
    private String port = null;

    /**
     * Get the database username.
     *
     * @return username as string. Can be null.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Set the database username. Only works, when the current username is not set.
     *
     * @param username username to set
     */
    public void setUsername(String username) {
        if (this.username != null) return;
        this.username = username;
    }

    /**
     * get the database password.
     *
     * @return password as string. Can be null.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Set the database password. Only works, when the current password is not set.
     *
     * @param password password to set.
     */
    public void setPassword(String password) {
        if (this.password != null) return;
        this.password = password;
    }

    /**
     * Get the database name.
     *
     * @return database name as string. Can be null.
     */
    public String getDb() {
        return db;
    }

    /**
     * Set the database name. Only works, when the current database name is not set.
     *
     * @param db database name to set.
     */
    public void setDb(String db) {
        if (this.db != null) return;
        this.db = db;
    }

    /**
     * Get the database ip or address.
     *
     * @return database address as string. Can be null.
     */
    public String getAddress() {
        return address;
    }

    /**
     * Set the database ip or address. Only works, when the current database ip or address is not set.
     *
     * @param address address to set.
     */
    public void setAddress(String address) {
        if (this.address != null) return;
        this.address = address;
    }

    /**
     * Gets the port of the database.
     *
     * @return Port as string. Can be null.
     */
    public String getPort() {
        return port;
    }

    /**
     * Sets the port of the database. Only works, when the current port is not set.
     *
     * @param port Port to set.
     */
    public void setPort(String port) {
        if (this.port != null) return;
        this.port = port;
    }
}
