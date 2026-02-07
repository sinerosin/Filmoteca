package com.example.filmoteca.Api;

import com.example.filmoteca.Model.Movie;
import com.example.filmoteca.Model.Serie;
import com.example.filmoteca.Response.MovieResponse;
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
    @GET("tv/{tv_id}/videos")
    Call<Serie.VideoResponse> getSerieVideos(
            @Path("tv_id") int tv_id,
            @Query("language") String language
    );

    @GET("movie/{movie_id}/videos")
    Call<Movie.VideoResponse> getMovieVideos(
            @Path("movie_id") int movie_id,
            @Query("language") String language
    );

    @GET("movie/popular")
    Call<MovieResponse> getMovie(
            @Query("language") String language,
            @Query("page") int page
    );
}
