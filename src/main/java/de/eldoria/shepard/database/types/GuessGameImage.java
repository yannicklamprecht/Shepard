package de.eldoria.shepard.database.types;

import org.jetbrains.annotations.NotNull;

public class GuessGameImage {
    private final String croppedImage;
    private final String fullImage;
    private final boolean hentai;

    /**
     * creates a new Guess game image.
     *
     * @param croppedImage url of cropped image
     * @param fullImage    url of full image
     * @param nsfw         true if it is a nsfw image
     */
    public GuessGameImage(String croppedImage, String fullImage, boolean nsfw) {
        this.croppedImage = croppedImage;
        this.fullImage = fullImage;
        this.hentai = nsfw;
    }

    /**
     * Get the cropped image url.
     *
     * @return url of cropped image
     */
    @NotNull
    public String getCroppedImage() {
        return croppedImage;
    }

    /**
     * Get the full image url.
     *
     * @return url of full image
     */
    @NotNull
    public String getFullImage() {
        return fullImage;
    }

    /**
     * Check if the image is nsfw.
     *
     * @return true if nsfw
     */
    public boolean isNsfw() {
        return hentai;
    }
}
