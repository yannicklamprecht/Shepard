package de.eldoria.shepard.commandmodules.saucenao.apiwrapper;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.Getter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
@Data
public class ResultMeta {
    private static Pattern indexNamePattern = Pattern.compile("(Index #[0-9]{1,3}:\\s.+?)\\s-");

    private double similarity;
    private String thumbnail;
    @SerializedName("index_id")
    private int indexId;
    @SerializedName("index_name")
    private String indexName;

    public SauceIndex getIndex() {
        return SauceIndex.getIndex(indexId);
    }

    public String getStrippedIndexName() {
        Matcher matcher = indexNamePattern.matcher(indexName);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return indexName;
    }
}
