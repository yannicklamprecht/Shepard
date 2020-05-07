package de.eldoria.shepard.core.configuration.configdata;

import lombok.Data;

/**
 * Class to save the presence config.
 */
@Data
public class Presence {
    /**
     * Playing status.
     */
    private String[] playing;
    /**
     * Listening status.
     */
    private String[] listening;
}
