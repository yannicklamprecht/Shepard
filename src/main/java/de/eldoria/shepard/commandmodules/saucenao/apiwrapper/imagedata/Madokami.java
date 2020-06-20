package de.eldoria.shepard.commandmodules.saucenao.apiwrapper.imagedata;

import de.eldoria.shepard.commandmodules.saucenao.apiwrapper.imagedata.util.ImageMeta;
import lombok.Getter;

@Getter
public class Madokami implements ImageMeta {
    private String source;
    private String part;
    private String type;
}
