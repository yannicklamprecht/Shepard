package de.eldoria.shepard.commandmodules.saucenao.apiwrapper.imagedata.util;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;

@Getter
public abstract class ExternalUrlMeta implements ImageMeta {
    @SerializedName("ext_urls")
    private String[] externalUrls;
}
