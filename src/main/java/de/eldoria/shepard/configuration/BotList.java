package de.eldoria.shepard.configuration;

import lombok.Data;

/**
 * Saves all required information of the botlist.
 */
@Data
public class BotList {
    /**
     * Botlist api token.
     */
    private String token = null;
    /**
     * Botlist auth token.
     */
    private String authorization = null;
}
