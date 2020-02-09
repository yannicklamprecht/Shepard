package de.eldoria.shepard.configuration;

/**
 * Saves all required information of the botlist.
 */
public class BotList {
    private String token = null;
    private String authorization = null;

    /**
     * Get the botlist token.
     *
     * @return token as string
     */
    public String getToken() {
        return token;
    }

    /**
     * Set the botlist token.
     *
     * @param token token as string
     */
    public void setToken(String token) {
        this.token = token;
    }

    /**
     * Get the authorization key.
     *
     * @return authorization key
     */
    public String getAuthorization() {
        return authorization;
    }

    /**
     * Set the authorization key.
     *
     * @param authorization authorization key
     */
    public void setAuthorization(String authorization) {
        this.authorization = authorization;
    }
}
