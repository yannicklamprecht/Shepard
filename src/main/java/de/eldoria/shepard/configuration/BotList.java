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
     * Array of botlist guild ids.
     */
    private long[] guildIds = new long[0];
}
