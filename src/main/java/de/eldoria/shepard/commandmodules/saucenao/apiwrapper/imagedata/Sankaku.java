package de.eldoria.shepard.commandmodules.saucenao.apiwrapper.imagedata;

import com.google.gson.annotations.SerializedName;
import de.eldoria.shepard.commandmodules.saucenao.apiwrapper.imagedata.util.ImageboardMeta;
import lombok.Getter;

@Getter
public class Sankaku extends ImageboardMeta {
    @SerializedName("sankaku_id")
    private int id;
}
