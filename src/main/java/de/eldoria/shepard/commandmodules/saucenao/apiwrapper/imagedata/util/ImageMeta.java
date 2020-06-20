package de.eldoria.shepard.commandmodules.saucenao.apiwrapper.imagedata.util;

public interface ImageMeta {
    @SuppressWarnings("unchecked")
    default <T extends ImageMeta> T getImageData() {
        return (T) this;
    }
}
