package de.eldoria.shepard.pictures;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

import java.util.List;

@SuppressWarnings("unused")
public interface GelbooruService {

    /**
     * Get Post with Id.
     *
     * @param id Id of the post
     * @return get request
     */
    @GET("index.php?page=dapi&s=post&q=index&json=1")
    Call<List<GelbooruPost>> getPost(@Query("id") int id);

    /**
     * Get Posts.
     *
     * @return get request
     */
    @GET("index.php?page=dapi&s=post&q=index&json=1")
    Call<List<GelbooruPost>> getPosts();

    /**
     * Get Posts via tags.
     *
     * @param tags tags for lookup
     * @return get request
     */
    @GET("index.php?page=dapi&s=post&q=index&json=1")
    Call<List<GelbooruPost>> getPosts(@Query("tags") String tags);

    /**
     * Get Posts on a site with tags.
     *
     * @param pageId page site
     * @param tags   tags for lookup
     * @return get request
     */
    @GET("index.php?page=dapi&s=post&q=index&json=1")
    Call<List<GelbooruPost>> getPosts(
            @Query("pid") int pageId,
            @Query("tags") String tags);

    /**
     * Get Posts on page with tags.
     *
     * @param pageId page site
     * @param tags   tags for lookup
     * @param limit  limit of posts
     * @return get request
     */
    @GET("rule34.xxx/index.php?page=dapi&s=post&q=index&json=1")
    Call<List<GelbooruPost>> getPosts(
            @Query("pid") int pageId,
            @Query("tags") String tags,
            @Query("limit") int limit);

}
