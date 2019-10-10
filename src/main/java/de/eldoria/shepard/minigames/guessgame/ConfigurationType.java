package de.eldoria.shepard.minigames.guessgame;

public enum ConfigurationType {
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
