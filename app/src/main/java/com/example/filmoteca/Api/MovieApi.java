package com.example.filmoteca.Api;

import com.example.filmoteca.Response.MovieResponse;
import com.example.filmoteca.Response.SerieResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MovieApi {
    @GET("movie/popular")
    Call<MovieResponse> getMovie(
            @Query("language") String language,
            @Query("page") int page
    );
}
