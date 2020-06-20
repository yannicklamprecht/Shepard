package de.eldoria.shepard.commandmodules.saucenao.apiwrapper;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;

import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;


@Getter
public class ResponseMeta {
    @SerializedName("user_id")
    @Expose
    private int userId;
    /**
     * Type of the account as int.
     * 0 is premium and 1 is free
     */
    @SerializedName("account_type")
    @Expose
    private int accountType;
    /**
     * The limit of requests in 30 seconds.
     */
    @SerializedName("short_limit")
    @Expose
    private long shortLimit;
    /**
     * The limit of requests in 24 hours.
     */
    @SerializedName("long_limit")
    @Expose
    private long longLimit;
    /**
     * Remaining requests for next 30 seconds.
     */
    @SerializedName("short_remaining")
    @Expose
    private long shortRemaining;
    /**
     * Remaining requests for next 24 hours.
     */
    @SerializedName("long_remaining")
    @Expose
    private long longRemaining;
    /**
     * status fields are 0 if success,
     * >0 for server side errors (failed descriptor gen, failed query, etc),
     * <0 for client side errors (bad image, out of searches, etc).
     * Not all errors are properly tagged yet, and some may exit the api.
     * If the status is not 0. a message should be available in the message field.
     */
    @Expose
    private int status;
    /**
     * Amount of requests results.
     * This will be less than defined in the api wrapper, when the count exceeds the account limit.
     */
    @SerializedName("results_requested")
    @Expose
    private int resultsRequested;
    /**
     * Amount of returned results.
     */
    @SerializedName("results_returned")
    @Expose
    private int resultsReturned;
    /**
     * The minimum similarity returned.
     * If testmode is activated results can be less than this.
     */
    @SerializedName("minimum_similarity")
    @Expose
    private float minimumSimilarity;
    /**
     * Error message, if the request failed.
     */
    @Expose
    private String message = null;
    /**
     * Status of requested indices.
     */
    @SerializedName("index")
    @Expose
    private Map<String, IndexData> indexData;

    /**
     * Get the parent id of an index.
     *
     * @param id id of child index
     * @return id of parent index.
     */
    public OptionalInt getParentId(int id) {
        IndexData indexData = this.indexData.get(Integer.toString(id));
        if (indexData == null) return OptionalInt.empty();
        return OptionalInt.of(indexData.getParentId());
    }
}
