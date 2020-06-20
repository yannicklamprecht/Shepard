package de.eldoria.shepard.commandmodules.saucenao.apiwrapper;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class SauceResponse {
    @SerializedName("header")
    @Expose
    private ResponseMeta responseMeta;
    @Expose(serialize = false)
    private final List<ResultEntry> results = new ArrayList<>();

    protected void setResults(List<ResultEntry> results) {
        this.results.clear();
        this.results.addAll(results);
    }
}
