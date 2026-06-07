package com.example.filmoteca.ViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import com.example.filmoteca.Model.Actor;
import com.example.filmoteca.Model.Movie;
import com.example.filmoteca.Model.Serie;
import com.example.filmoteca.Repository.ActorRepository;
import java.util.List;

public class ActorViewModel extends ViewModel {

    private final ActorRepository repository = new ActorRepository();
    public LiveData<List<Actor>> getRepartoMovie(int movieId, String idioma) {
        return repository.obtenerRepartoMovie(movieId, idioma);
    }
    public LiveData<List<Actor>> getRepartoSerie(int serieId, String idioma) {
        return repository.obtenerRepartoSerie(serieId, idioma);
    }
    public LiveData<List<Movie>> getMoviesActor(int actorId, String idioma) {
        return repository.obtenerMovieActor(actorId, idioma);
    }
    public LiveData<List<Serie>> getSeriesActor(int actorId, String idioma) {
        return repository.obtenerSeriesActor(actorId, idioma);
    }
}