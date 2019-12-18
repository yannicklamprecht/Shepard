package de.eldoria.shepard.minigames.guessgame;

import de.eldoria.shepard.database.queries.GuessGameData;

public class ImageConfiguration {
    private final boolean nsfw;
    private String croppedImage;
    private String fullImage;

    /**
     * Create a new image configuration.
     *
     * @param nsfw true if nsfw
     */
    ImageConfiguration(boolean nsfw) {
        this.nsfw = nsfw;
    }

    /**
     * add a image.
     *
     * @param url url of cropped or full image. cropped first!
     */
    void addImage(String url) {
        if (croppedImage == null) {
            croppedImage = url;
            return;
        }
        if (fullImage == null) {
            fullImage = url;
        }
    }

    /**
     * Register the images at database.
     *
     * @return true if registration was successfully
     */
    boolean registerAtDatabase() {
        return GuessGameData.addHentaiImage(croppedImage, fullImage, nsfw, null);
    }

    /**
     * Configuration state.
     *
     * @return configuration state of the current registration.
     */
    ConfigurationState getConfigurationState() {
        if (croppedImage == null) {
            return ConfigurationState.CROPPED;
        }
        if (fullImage == null) {
            return ConfigurationState.FULL;
        }
        return ConfigurationState.CONFIGURED;
    }

    /**
     * Check if the image ist nsfw.
     *
     * @return true if nsfw.
     */
    public boolean isNsfw() {
        return nsfw;
    }

    /**
     * Get the cropped image url.
     *
     * @return copped image url.
     */
    public String getCroppedImage() {
        return croppedImage;
    }

    /**
     * Get the full image url.
     *
     * @return full image url.
     */
    public String getFullImage() {
        return fullImage;
    }
}
