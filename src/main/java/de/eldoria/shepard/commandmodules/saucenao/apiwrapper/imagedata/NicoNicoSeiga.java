package de.eldoria.shepard.commandmodules.saucenao.apiwrapper.imagedata;

import com.google.gson.annotations.SerializedName;
import de.eldoria.shepard.commandmodules.saucenao.apiwrapper.imagedata.util.ExternalUrlMeta;
import lombok.Getter;

@Getter
public class NicoNicoSeiga extends ExternalUrlMeta {
    private String title;
    @SerializedName("seiga_id")
    private int id;
    @SerializedName("member_name")
    private String memberName;
    @SerializedName("member_id")
    private int memberId;
}
