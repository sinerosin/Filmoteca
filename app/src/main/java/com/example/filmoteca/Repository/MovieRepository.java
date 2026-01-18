package com.example.filmoteca.Repository;

import com.example.filmoteca.Api.MovieApi;

import com.example.filmoteca.Model.Movie;

import com.example.filmoteca.Resource;
import com.example.filmoteca.Response.MovieResponse;

import com.example.filmoteca.Retrofit.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieRepository {
    private final MovieApi api;
    private int PAGE=1;
    public MovieRepository() {
        api = RetrofitClient.getMovieApi();
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
}
