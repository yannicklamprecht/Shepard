package de.eldoria.shepard.core.configuration.configdata;

import lombok.Data;

@Data
public class GeneralSettings {
    /**
     * Bot API token.
     */
    private String token = null;
    /**
     * Beta state of bot.
     */
    private boolean beta = false;
    /**
     * Default prefix.
     */
    private String prefix = null;

    /**
     * Maximum amount of threads used for async command execution.
     */
    private int commandExecutionThreads = 20;
}
