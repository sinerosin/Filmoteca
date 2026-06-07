package com.example.filmoteca.Repository;

import android.app.Application;

import com.example.filmoteca.Api.SeriesApi;
import com.example.filmoteca.Model.Serie;
import com.example.filmoteca.Api.Resource;
import com.example.filmoteca.Response.SerieResponse;
import com.example.filmoteca.Api.RetrofitClient;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SeriesRepository {
    private final SeriesApi api;
    private int PAGE = 1;

    public SeriesRepository(Application application) {
        api = RetrofitClient.getSeriesApi();
    }

    public interface SerieCallback {
        void onResult(Resource<List<Serie>> result);
    }

    public void getSeries(String idioma, SerieCallback callback) {
        callback.onResult(Resource.loading());

        api.getSeries(idioma, PAGE).enqueue(new Callback<SerieResponse>() {
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
    public void reiniciarPaginacion() {
        this.PAGE = 1;
    }

    public void getTrailer(int id, VideoKeyCallback callback) {
        api.getSerieVideos(id, "es-ES").enqueue(new Callback<Serie.VideoResponse>() {
            @Override
            public void onResponse(Call<Serie.VideoResponse> call, Response<Serie.VideoResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String foundKey = null;
                    for (Serie.serieVideo video : response.body().getResults()) {
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
            public void onFailure(Call<Serie.VideoResponse> call, Throwable t) {
                callback.onKeyReceived(null);
            }
        });
    }

    public interface VideoKeyCallback {
        void onKeyReceived(String key);
    }
}