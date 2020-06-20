package de.eldoria.shepard.commandmodules.saucenao.apiwrapper.imagedata;

import com.google.gson.annotations.SerializedName;
import de.eldoria.shepard.commandmodules.saucenao.apiwrapper.imagedata.util.ImageboardMeta;
import lombok.Getter;

@Getter
public class YandeRe extends ImageboardMeta {
    @SerializedName("yandere_id")
    private int id;
}
