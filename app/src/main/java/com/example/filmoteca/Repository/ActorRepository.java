package com.example.filmoteca.Repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.filmoteca.Api.RetrofitClient;
import com.example.filmoteca.Api.SeriesApi;
import com.example.filmoteca.Model.Actor;
import com.example.filmoteca.Model.Movie;
import com.example.filmoteca.Model.Serie;
import com.example.filmoteca.Response.ActorResponse;
import com.example.filmoteca.Response.MovieCreditResponse;
import com.example.filmoteca.Response.SerieCreditResponse;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActorRepository {
    private final SeriesApi api = RetrofitClient.getSeriesApi();
    public LiveData<List<Actor>> obtenerRepartoMovie(int movieId, String idioma) {
        MutableLiveData<List<Actor>> liveData = new MutableLiveData<>();

        api.getMovieCredits(movieId, idioma).enqueue(new Callback<ActorResponse>() {
            @Override
            public void onResponse(Call<ActorResponse> call, Response<ActorResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.setValue(response.body().getResults());
                }
            }

            @Override
            public void onFailure(Call<ActorResponse> call, Throwable t) {
                liveData.setValue(null);
            }
        });
        return liveData;
    }

    public LiveData<List<Actor>> obtenerRepartoSerie(int serieId, String idioma) {
        MutableLiveData<List<Actor>> liveData = new MutableLiveData<>();

        api.getSerieCredits(serieId, idioma).enqueue(new Callback<ActorResponse>() {
            @Override
            public void onResponse(Call<ActorResponse> call, Response<ActorResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.setValue(response.body().getResults());
                }
            }

            @Override
            public void onFailure(Call<ActorResponse> call, Throwable t) {
                liveData.setValue(null);
            }
        });
        return liveData;
    }

    public LiveData<List<Movie>> obtenerMovieActor(int actorId, String idioma) {
        MutableLiveData<List<Movie>> liveData = new MutableLiveData<>();

        api.getActorMovieCredits(actorId, idioma).enqueue(new Callback<MovieCreditResponse>() {
            @Override
            public void onResponse(Call<MovieCreditResponse> call, Response<MovieCreditResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.setValue(response.body().getResults());
                }
            }

            @Override
            public void onFailure(Call<MovieCreditResponse> call, Throwable t) {
                liveData.setValue(null);
            }
        });
        return liveData;
    }

    public LiveData<List<Serie>> obtenerSeriesActor(int actorId, String idioma) {
        MutableLiveData<List<Serie>> liveData = new MutableLiveData<>();

        api.getActorTvCredits(actorId, idioma).enqueue(new Callback<SerieCreditResponse>() {
            @Override
            public void onResponse(Call<SerieCreditResponse> call, Response<SerieCreditResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.setValue(response.body().getResults());
                }
            }

            @Override
            public void onFailure(Call<SerieCreditResponse> call, Throwable t) {
                liveData.setValue(null);
            }
        });
        return liveData;
    }
}