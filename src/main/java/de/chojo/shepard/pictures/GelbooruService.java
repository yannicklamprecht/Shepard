package de.chojo.shepard.pictures;

import retrofit2.http.GET;
import retrofit2.Call;
import retrofit2.http.Query;

import java.util.List;



public interface GelbooruService {
    @GET("index.php?page=dapi&s=post&q=index&json=1")
    public Call<List<GelbooruPost>> getPost(@Query("id") int id);

    @GET("index.php?page=dapi&s=post&q=index&json=1")
    public Call<List<GelbooruPost>> getPosts();

    @GET("index.php?page=dapi&s=post&q=index&json=1")
    public Call<List<GelbooruPost>> getPosts(@Query("tags") String tags);

    @GET("index.php?page=dapi&s=post&q=index&json=1")
    public Call<List<GelbooruPost>> getPosts(
            @Query("pid") int pageId,
            @Query("tags") String tags);

    @GET("rule34.xxx/index.php?page=dapi&s=post&q=index&json=1")
    public Call<List<GelbooruPost>> getPosts(
            @Query("pid") int pageId,
            @Query("tags") String tags,
            @Query("limit") int limit);

}
