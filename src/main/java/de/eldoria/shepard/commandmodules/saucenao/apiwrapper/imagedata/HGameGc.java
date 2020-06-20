package de.eldoria.shepard.commandmodules.saucenao.apiwrapper.imagedata;

import com.google.gson.annotations.SerializedName;
import de.eldoria.shepard.commandmodules.saucenao.apiwrapper.imagedata.util.ImageMeta;

public class HGameGc implements ImageMeta {
    private String title;
    private String company;
    @SerializedName("getchu_id")
    private String id;
}
