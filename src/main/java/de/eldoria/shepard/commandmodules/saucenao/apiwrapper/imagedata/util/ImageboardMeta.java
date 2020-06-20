package de.eldoria.shepard.commandmodules.saucenao.apiwrapper.imagedata.util;

import lombok.Getter;

@Getter
public abstract class ImageboardMeta extends ExternalUrlMeta {
    private String creator;
    private String material;
    private String characters;
    private String source;
}
