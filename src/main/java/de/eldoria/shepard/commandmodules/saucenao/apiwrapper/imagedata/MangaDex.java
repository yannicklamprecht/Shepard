package de.eldoria.shepard.commandmodules.saucenao.apiwrapper.imagedata;

import com.google.gson.annotations.SerializedName;
import de.eldoria.shepard.commandmodules.saucenao.apiwrapper.imagedata.util.ExternalUrlMeta;
import lombok.Getter;

@Getter
public class MangaDex extends ExternalUrlMeta {
    @SerializedName("md_id")
    private int id;
    private String source;
    private String part;
    private String artist;
    private String author;
}
