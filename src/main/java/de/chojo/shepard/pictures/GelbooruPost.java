package de.chojo.shepard.pictures;

import com.google.gson.annotations.SerializedName;

@SuppressWarnings("unused")
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
    private int height;
    private int width;

    @SerializedName("sample_width")
    private int sampleWidth;

    @SerializedName("sample_height")
    private int sampleHeight;

    private String rating;

    /**
     * URL of the image.
     * @param baseUrl base url as string
     * @return String
     */
    public String url(String baseUrl) {
        return baseUrl + "images/" + directory + "/" + image;
    }

    /**
     * Sample url.
     * @param baseUrl base url as string
     * @return String
     */
    public String sampleUrl(String baseUrl) {
        return baseUrl + "samples/" + directory + "/sample_" + image;
    }

    /**
     * Share url.
     * @param baseUrl base url as string
     * @return String
     */
    public String shareUrl(String baseUrl) {
        return baseUrl + "index.php?page=post&s=view&id=" + id;
    }


}
