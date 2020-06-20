package de.eldoria.shepard.commandmodules.saucenao.apiwrapper;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import de.eldoria.shepard.commandmodules.saucenao.apiwrapper.imagedata.util.ImageMeta;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.OptionalInt;

@Slf4j
public final class SaucenaoApiWrapper {

    private final String request;
    private final HttpClient httpClient;
    private final OkHttpClient okHttpClient;
    private final Gson exposedParser = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
    private final Gson generalParser = new GsonBuilder().create();

    private SaucenaoApiWrapper(String request) {
        this.request = request;
        httpClient = HttpClient.newHttpClient();
        okHttpClient = null;
    }

    private SaucenaoApiWrapper(OkHttpClient okHttpClient, HttpClient httpClient, String request) {
        this.request = request;
        this.httpClient = httpClient;
        this.okHttpClient = okHttpClient;
    }

    /**
     * Get a new saucenao api wrapper builder.
     *
     * @param key key for api. can be null.
     * @return builder with key
     */
    public static SaucenaoApiWrapperBuilder builder(@Nullable String key) {
        return new SaucenaoApiWrapperBuilder(key);
    }

    /**
     * Get a new saucenao api wrapper builder.
     *
     * @return builder without api key
     */
    public static SaucenaoApiWrapperBuilder builder() {
        return new SaucenaoApiWrapperBuilder();
    }

    public SauceResponse requestImage(String url) {
        String httpResponse = requestSauce(url);
        if (httpResponse == null) return null;

        // build json element from string
        JsonElement jsonResponse = new JsonParser().parse(httpResponse);

        // create general response.
        SauceResponse sauceResponse = exposedParser.fromJson(jsonResponse, SauceResponse.class);

        List<ResultEntry> sauceResultEntries = parseResults(sauceResponse, jsonResponse);

        sauceResponse.setResults(sauceResultEntries);
        return sauceResponse;
    }

    private String requestSauce(String url) {
        String requestUrl;
        try {
            requestUrl = this.request + URLEncoder.encode(url, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            log.warn("Could not encode url.");
            return null;
        }

        if (httpClient != null) {
            HttpRequest request;
            request = HttpRequest.newBuilder()
                    .uri(URI.create(requestUrl))
                    .build();

            try {
                return httpClient.send(request, HttpResponse.BodyHandlers.ofString()).body();
            } catch (IOException | InterruptedException e) {
                return null;
            }
        } else {
            Request request = new Request.Builder().url(requestUrl).get().build();
            try (Response execute = okHttpClient.newCall(request).execute()) {
                if (execute.body() == null) return null;
                return execute.body().string();
            } catch (IOException e) {
                log.error("Request failed", e);
                return null;
            }
        }
    }

    private List<ResultEntry> parseResults(SauceResponse sauceResponse, JsonElement jsonResponse) {
        JsonArray resultJson = jsonResponse.getAsJsonObject().getAsJsonArray("results");

        if (resultJson == null) return Collections.emptyList();

        ArrayList<ResultEntry> results = new ArrayList<>();

        for (JsonElement element : resultJson) {
            // build base result object.
            JsonElement header = element.getAsJsonObject().get("header");
            ResultMeta resultMeta = generalParser.fromJson(header, ResultMeta.class);

            ResultEntry result = new ResultEntry(resultMeta);

            // determine index
            int indexId = result.getResultMeta().getIndexId();
            SauceIndex index = SauceIndex.getIndex(indexId);
            if (index == SauceIndex.UNKOWN) {
                OptionalInt parentId = sauceResponse.getResponseMeta().getParentId(indexId);
                if (parentId.isPresent()) {
                    index = SauceIndex.getIndex(parentId.getAsInt());
                } else {
                    log.info("Index \"" + result.getResultMeta().getIndexName() + "\" with id "
                            + indexId + " is unknown.");
                    continue;
                }
            }

            if (index.getDataClass() == null) {
                log.info("Index \"" + result.getResultMeta().getIndexName() + "\" with id "
                        + indexId + " has no mapping class.");
                continue;
            }

            // Build data object based on index.
            ImageMeta data = generalParser.fromJson(element.getAsJsonObject().get("data"), index.getDataClass());

            result.setData(data);
            results.add(result);
        }
        return results;
    }

    public static final class SaucenaoApiWrapperBuilder {
        private final String key;
        private SauceIndex[] indices;
        private long excludeBitmask = 0;
        private int testmode = 0;
        private int count = 1;
        private HttpClient httpClient;
        private OkHttpClient okHttpClient;

        private SaucenaoApiWrapperBuilder(String key) {
            this.key = key;
        }

        private SaucenaoApiWrapperBuilder() {
            this.key = null;
        }

        /**
         * Add indices to the wrapper. Will override already set indiced.
         * Default value is {@link SauceIndex#ALL}
         *
         * @param indices indices to use.
         * @return builder with indices set.
         * @throws IllegalArgumentException when flag {@link SauceIndex#ALL} is used with other flags.
         */
        public SaucenaoApiWrapperBuilder withIndices(SauceIndex... indices) throws IllegalArgumentException {
            if (indices.length == 0) {
                throw new IllegalArgumentException("Indices cant be empty");
            }
            this.indices = indices;

            long bitmask = 0;
            for (SauceIndex index : indices) {
                if (index == SauceIndex.ALL && indices.length != 1) {
                    throw new IllegalArgumentException("ALL index was used with more indices");
                }
                if (index == SauceIndex.ALL) {
                    return this;
                }

                Deprecated deprecated;
                try {
                    deprecated = index.getClass().getField(index.name()).getAnnotation(Deprecated.class);
                } catch (NoSuchFieldException e) {
                    log.error("Error while checking deprecation", e);
                    continue;
                }

                if (deprecated != null) {
                    log.warn("Usage of deprecated index " + index.name() + " detected.");
                }
            }
            return this;
        }

        /**
         * Exclude indices. Usefull if {@link #withIndices(SauceIndex...)} is set to {@link SauceIndex#ALL}.
         *
         * @param indices indices to exclude
         * @return builder with excluded indices set
         * @throws IllegalArgumentException when flag {@link SauceIndex#ALL} is used
         */
        public SaucenaoApiWrapperBuilder excludeIndices(SauceIndex... indices) throws IllegalArgumentException {
            long bitmask = 0;
            for (SauceIndex index : indices) {
                bitmask += index.bitmask;
                if (index == SauceIndex.ALL) {
                    throw new IllegalArgumentException("ALL index was used with more indices");
                }
            }

            excludeBitmask = bitmask;
            return this;
        }

        /**
         * Activates the test mode. Causes each index which has a match to output at most 1 for testing.
         * Works best with a {@link #withResultCount(int)} greater than the number of indexes searched.
         *
         * @return builder with testmode activated
         */
        public SaucenaoApiWrapperBuilder inTestMode() {
            testmode = 1;
            return this;
        }

        /**
         * Set the result count.
         * For free account the max result count is 6.
         * For premium accounts the max result count is 60.
         *
         * @param count max result count.
         * @return builder with result count set
         */
        public SaucenaoApiWrapperBuilder withResultCount(int count) {
            this.count = count;
            return this;
        }

        public SaucenaoApiWrapperBuilder withHttpClient(OkHttpClient okHttpClient) {
            this.okHttpClient = okHttpClient;
            this.httpClient = null;
            return this;
        }

        public SaucenaoApiWrapperBuilder withHttpClient(HttpClient httpClient) {
            this.httpClient = httpClient;
            this.okHttpClient = null;
            return this;
        }

        /**
         * Build a api wrapper.
         *
         * @return a new api wrapper object
         */
        public SaucenaoApiWrapper build() {
            StringBuilder builder = new StringBuilder("https://saucenao.com/search.php?output_type=2");
            if (key != null) {
                builder.append("&api_key=").append(key);
            }
            if (testmode == 1) {
                builder.append("&testmode=").append(testmode);
            }

            // use bitmask if more than one index is required.
            if (indices.length > 1) {
                long bitmask = 0;
                for (SauceIndex index : indices) {
                    bitmask += index.bitmask;
                }
                builder.append("&dbmask=").append(bitmask);
            } else {
                builder.append("&db=").append(indices[0].index);
            }


            if (excludeBitmask != 0) {
                builder.append("&dbmaski=").append(excludeBitmask);
            }
            builder.append("&numres=").append(count);

            builder.append("&url=");
            if (okHttpClient != null || httpClient != null) {
                return new SaucenaoApiWrapper(okHttpClient, httpClient, builder.toString());

            }
            
            return new SaucenaoApiWrapper(builder.toString());
        }
    }
}
