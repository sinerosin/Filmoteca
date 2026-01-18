package com.example.filmoteca.Api;

import com.example.filmoteca.Response.SerieResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface SeriesApi {

    @GET("tv/popular")
    Call<SerieResponse> getSeries(
            @Query("language") String language,
            @Query("page") int page
    );

}
