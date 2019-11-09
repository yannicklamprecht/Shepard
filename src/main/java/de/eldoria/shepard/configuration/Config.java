package de.eldoria.shepard.configuration;

public class Config {
    private String token = null;
    private boolean debug = false;
    private BotList botlist = null;
    private Presence presence = null;
    private Database database = null;
    private String prefix = null;

    /**
     * get the Database Object.
     *
     * @return Database object. Can ben null.
     */
    public Database getDatabase() {
        return database;
    }

    /**
     * Sets the database object. Only works when the current database object is not set
     *
     * @param database Database object
     */
    public void setDatabase(Database database) {
        if (this.database != null) return;
        this.database = database;
    }

    /**
     * Gets the application token of the bot.
     *
     * @return Token as string. Can be null.
     */
    public String getToken() {
        return token;
    }

    /**
     * Sets the application token. Only works when the current token is not set.
     *
     * @param token Token to set.
     */
    public void setToken(String token) {
        if (this.token != null) return;
        this.token = token;
    }

    /**
     * Get the default prefix of the bot.
     *
     * @return Prefix as string. Can be null
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * Sets the default prefix of the bot. Only works, when the current prefix is not set.
     *
     * @param prefix Prefix to set
     */
    public void setPrefix(String prefix) {
        if (this.prefix != null) return;
        this.prefix = prefix;
    }

    /**
     * Check if the debug mode is active.
     *
     * @return true if active
     */
    public boolean debugActive() {
        return debug;
    }

    /**
     * Set the debug mode.
     *
     * @param debug true if active
     */
    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    /**
     * Get the botlist object.
     *
     * @return botlist object.
     */
    public BotList getBotlist() {
        return botlist;
    }

    /**
     * Set the botlist object.
     *
     * @param botlist botlist object
     */
    public void setBotlist(BotList botlist) {
        this.botlist = botlist;
    }

    /**
     * Get the presence object.
     * @return presence object
     */
    public Presence getPresence() {
        return presence;
    }

    /**
     * Set the presence object.
     * @param presence presence object
     */
    public void setPresence(Presence presence) {
        this.presence = presence;
    }
}
