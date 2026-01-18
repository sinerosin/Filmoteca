package com.example.filmoteca.Repository;

import com.example.filmoteca.Api.SeriesApi;
import com.example.filmoteca.Model.Serie;
import com.example.filmoteca.Resource;
import com.example.filmoteca.Response.SerieResponse;
import com.example.filmoteca.Retrofit.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SeriesRepository {
    private final SeriesApi api;

    public SeriesRepository() {
        api = RetrofitClient.getSeriesApi();
    }

    // Callback propio para enviar el resultado al ViewModel
    public interface PokemonCallback {
        void onResult(Resource<Serie> result);
    }
    public interface SerieCallback {
        void onResult(Resource<Serie> result);
    }
    public void getSeries(SerieCallback callback) {
        callback.onResult(Resource.loading());

        // Llamamos a las series populares de TMDB
        api.getSeries("es-ES", 1).enqueue(new Callback<Serie>() {
            @Override
            public void onResponse(Call<SerieResponse> call, Response<Serie> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Enviamos la lista de series
                    callback.onResult(Resource.success(response.body()));
                } else {
                    callback.onResult(Resource.error("Error al obtener series"));
                }
            }

            @Override
            public void onFailure(Call<Serie> call, Throwable t) {
                callback.onResult(Resource.error("Fallo de red: " + t.getMessage()));
            }
        });
    }


}
