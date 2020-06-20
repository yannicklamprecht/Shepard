package de.eldoria.shepard.commandmodules.saucenao.apiwrapper.imagedata;

import com.google.gson.annotations.SerializedName;
import de.eldoria.shepard.commandmodules.saucenao.apiwrapper.imagedata.util.ImageMeta;
import lombok.Getter;

@Getter
public class HMisc implements ImageMeta {
    private String source;
    private String[] creator;
    @SerializedName("eng_name")
    private String engName;
    @SerializedName("jp_name")
    private String jpName;
}
