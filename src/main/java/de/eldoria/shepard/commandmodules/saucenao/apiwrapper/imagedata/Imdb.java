package de.eldoria.shepard.commandmodules.saucenao.apiwrapper.imagedata;

import com.google.gson.annotations.SerializedName;
import de.eldoria.shepard.commandmodules.saucenao.apiwrapper.imagedata.util.ExternalUrlMeta;
import lombok.Getter;

@Getter
public class Imdb extends ExternalUrlMeta {
    protected String source;
    @SerializedName("imdb_id")
    protected String id;
    protected String part;
    protected String year;
    @SerializedName("est_time")
    protected String estimatedTime;
}
