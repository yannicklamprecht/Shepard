package de.eldoria.shepard.commandmodules.saucenao.apiwrapper.imagedata;

import com.google.gson.annotations.SerializedName;
import de.eldoria.shepard.commandmodules.saucenao.apiwrapper.imagedata.util.ImageboardMeta;
import lombok.Getter;

@Getter
public class Gelbooru extends ImageboardMeta {
    @SerializedName("gelbooru_id")
    private int id;
}
