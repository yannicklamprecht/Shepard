package de.eldoria.shepard.commandmodules.saucenao.apiwrapper.imagedata;

import com.google.gson.annotations.SerializedName;
import de.eldoria.shepard.commandmodules.saucenao.apiwrapper.imagedata.util.ExternalUrlMeta;
import lombok.Getter;

@Getter
public class DeviantArt extends ExternalUrlMeta {
    private String title;
    @SerializedName("da_id")
    private int id;
    @SerializedName("author_name")
    private String authorName;
    @SerializedName("author_url")
    private String authorUrl;
}
