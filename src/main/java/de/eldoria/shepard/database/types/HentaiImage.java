package de.eldoria.shepard.database.types;

public class HentaiImage {
    private String croppedImage;
    private String fullImage;
    private boolean hentai;

    public HentaiImage(String croppedImage, String fullImage, boolean hentai) {
        this.croppedImage = croppedImage;
        this.fullImage = fullImage;
        this.hentai = hentai;
    }

    public String getCroppedImage() {
        return croppedImage;
    }

    public String getFullImage() {
        return fullImage;
    }

    public boolean isHentai() {
        return hentai;
    }
}
