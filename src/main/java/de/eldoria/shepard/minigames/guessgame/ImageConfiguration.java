package de.eldoria.shepard.minigames.guessgame;

import de.eldoria.shepard.database.queries.GuessGameData;

public class ImageConfiguration {
    private boolean hentai;
    private String croppedImage;
    private String fullImage;

    ImageConfiguration(boolean hentai) {
        this.hentai = hentai;
    }

    void addImage(String url) {
        if (croppedImage == null) {
            croppedImage = url;
            return;
        }
        if (fullImage == null) {
            fullImage = url;
        }
    }


    boolean registerAtDatabase() {
        return GuessGameData.addHentaiImage(croppedImage, fullImage, hentai,null);
    }

    ConfigurationType getConfigurationState() {
        if (croppedImage == null) {
            return ConfigurationType.CROPPED;
        }
        if (fullImage == null) {
            return ConfigurationType.FULL;
        }
        return ConfigurationType.CONFIGURED;
    }

    public boolean isHentai() {
        return hentai;
    }

    public String getCroppedImage() {
        return croppedImage;
    }

    public String getFullImage() {
        return fullImage;
    }
}
