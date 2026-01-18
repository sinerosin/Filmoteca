package com.example.filmoteca.Api;

import com.example.filmoteca.Model.Serie;
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
    @GET("tv/{series_id}")
    Call<Serie> getSerieDetail(
            @Path("series_id") int seriesId,
            @Query("api_key") String apiKey,
            @Query("language") String language,
            @Query("append_to_response") String appendToResponse
    );

}
