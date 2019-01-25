package de.chojo.shepard.pictures;

import com.google.gson.annotations.SerializedName;

public class GelbooruPost {
    private int id;

    @SerializedName("parent_id")
    private int parentId;

    private String directory;
    private String hash;
    private String owner;
    private String image;

    private int score;
    private String tags;

    private boolean sample;
    private int change;
    private int height, width;
    @SerializedName("sample_width")
    private int sampleWidth;
    @SerializedName("sample_height")
    private int sampleHeight;

    private String rating;

    public String url(String baseUrl) {
        return baseUrl + "images/" + directory + "/" + image;
    }

    public String sampleUrl(String baseUrl) {
        return baseUrl + "samples/" + directory + "/sample_" + image;
    }

    public String shareUrl(String baseUrl) {
        return baseUrl + "index.php?page=post&s=view&id=" + id;
    }


}
