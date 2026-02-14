package com.example.filmoteca.Repository;

import com.example.filmoteca.Api.SeriesApi;
import com.example.filmoteca.Model.Movie;

import com.example.filmoteca.Api.Resource;
import com.example.filmoteca.Model.Serie;
import com.example.filmoteca.Response.MovieResponse;

import com.example.filmoteca.Api.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieRepository {
    private final SeriesApi api;
    private int PAGE=1;
    public MovieRepository() {
        api = RetrofitClient.getSeriesApi();
    }
    public interface MovieCallback {
        void onResult(Resource<List<Movie>> result);
    }
    public void getMovie(MovieRepository.MovieCallback callback) {
        callback.onResult(Resource.loading());


        api.getMovie("es-ES", PAGE).enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                if (response.isSuccessful() && response.body() != null) {

                    List<Movie> lista = response.body().getResults();

                    callback.onResult(Resource.success(lista));
                } else {
                    callback.onResult(Resource.error("No se pudo cargar la Lista"));
                }
                PAGE++;
            }

            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) {
                callback.onResult(Resource.error("Fallo de red: " + t.getMessage()));
            }
        });
    }
    public void getTrailer(int id, MovieRepository.VideoKeyCallback callback) {

        api.getMovieVideos(id, "es-ES").enqueue(new Callback<Movie.VideoResponse>() {
            @Override
            public void onResponse(Call<Movie.VideoResponse> call, Response<Movie.VideoResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String foundKey = null;

                    for (Movie.MovieVideo video : response.body().getResults()) {
                        if ("Trailer".equalsIgnoreCase(video.getType()) &&
                                "YouTube".equalsIgnoreCase(video.getSite())) {
                            foundKey = video.getKey();
                            break;
                        }
                    }
                    callback.onKeyReceived(foundKey);
                } else {
                    callback.onKeyReceived(null);
                }
            }


            @Override
            public void onFailure(Call<Movie.VideoResponse> call, Throwable t) {
                callback.onKeyReceived(null);
            }
        });
    }
    public interface VideoKeyCallback {
        void onKeyReceived(String key);
    }
}
