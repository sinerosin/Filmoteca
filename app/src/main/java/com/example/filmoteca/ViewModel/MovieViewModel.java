package com.example.filmoteca.ViewModel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.filmoteca.Model.Movie;
import com.example.filmoteca.Model.Serie;
import com.example.filmoteca.Repository.MovieRepository;
import com.example.filmoteca.Repository.SeriesRepository;
import com.example.filmoteca.Resource;

import java.util.List;

public class MovieViewModel extends ViewModel {
    private final MovieRepository repository;


    public MutableLiveData<Resource<List<Movie>>> movies = new MutableLiveData<>();
    public MutableLiveData<Movie> movieSeleccionada = new MutableLiveData<>();

    public MovieViewModel() {
        repository = new MovieRepository();
        cargarMovies();
    }

    public void cargarMovies() {

        repository.getMovie(result -> {
            movies.postValue(result);
        });
    }

    public void seleccionarMovie(Movie movie) {
        movieSeleccionada.setValue(movie);
    }
}
