package de.eldoria.shepard.commandmodules.guessgame.util;

public enum ConfigurationState {
    /**
     * Object does not exist. No configuration process is running.
     */
    NONE,
    /**
     * waiting for cropped image.
     */
    CROPPED,
    /**
     * Waiting for full image.
     */
    FULL,
    /**
     * ready to be stored in database.
     */
    CONFIGURED
}
