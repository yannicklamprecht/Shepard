package de.eldoria.shepard.commandmodules.saucenao.apiwrapper;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import de.eldoria.shepard.commandmodules.saucenao.apiwrapper.imagedata.util.ImageMeta;
import lombok.Getter;

@Getter
public class ResultEntry {
    @SerializedName("header")
    private ResultMeta resultMeta;
    @Expose(deserialize = false)
    private ImageMeta data;

    public ResultEntry(ResultMeta resultMeta) {
        this.resultMeta = resultMeta;
    }

    void setData(ImageMeta data) {
        this.data = data;
    }
}
