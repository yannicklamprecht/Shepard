package de.eldoria.shepard.commandmodules.saucenao.apiwrapper.imagedata;

import com.google.gson.annotations.SerializedName;
import de.eldoria.shepard.commandmodules.saucenao.apiwrapper.imagedata.util.ExternalUrlMeta;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class PixivImages extends ExternalUrlMeta {
    private String title;
    @SerializedName("pixiv_id")
    private int id;
    @SerializedName("member_name")
    private String memberName;
    @SerializedName("member_id")
    private String memberId;
}
