package de.eldoria.shepard.database.types;

import org.jetbrains.annotations.NotNull;

public class GuessGameImage {
    private final String croppedImage;
    private final String fullImage;
    private final boolean hentai;

    public GuessGameImage(String croppedImage, String fullImage, boolean hentai) {
        this.croppedImage = croppedImage;
        this.fullImage = fullImage;
        this.hentai = hentai;
    }

    @NotNull
    public String getCroppedImage() {
        return croppedImage;
    }

    @NotNull
    public String getFullImage() {
        return fullImage;
    }

    public boolean isNsfw() {
        return hentai;
    }
}
