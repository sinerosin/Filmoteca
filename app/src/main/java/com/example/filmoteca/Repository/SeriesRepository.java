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
    private int PAGE=1;
    public SeriesRepository() {
        api = RetrofitClient.getSeriesApi();
    }
    public interface SerieCallback {
        void onResult(Resource<List<Serie>> result);
    }
    public void getSeries(SerieCallback callback) {
        callback.onResult(Resource.loading());


        api.getSeries("es-ES", PAGE).enqueue(new Callback<SerieResponse>() {
            @Override
            public void onResponse(Call<SerieResponse> call, Response<SerieResponse> response) {
                if (response.isSuccessful() && response.body() != null) {

                    List<Serie> lista = response.body().getResults();

                    callback.onResult(Resource.success(lista));
                } else {
                    callback.onResult(Resource.error("No se pudo cargar la Lista"));
                }
                PAGE++;
            }

            @Override
            public void onFailure(Call<SerieResponse> call, Throwable t) {
                callback.onResult(Resource.error("Fallo de red: " + t.getMessage()));
            }
        });
    }

}
