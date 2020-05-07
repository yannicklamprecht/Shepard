package de.eldoria.shepard.core.configuration.configdata;

import lombok.Data;

/**
 * Saves all required information of the botlist.
 */
@Data
public class BotList {
    /**
     * Object which holds several botlist token.
     */
    private BotlistToken token = null;
    /**
     * Array of botlist guild ids.
     */
    private long[] guildIds = new long[0];
}
