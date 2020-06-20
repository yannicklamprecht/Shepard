package de.eldoria.shepard.commandmodules.saucenao.apiwrapper.imagedata;

import com.google.gson.annotations.SerializedName;
import de.eldoria.shepard.commandmodules.saucenao.apiwrapper.imagedata.util.ImageboardMeta;
import lombok.Getter;

@Getter
public class E621Net extends ImageboardMeta {
    @SerializedName("e621_id")
    private int id;
}
