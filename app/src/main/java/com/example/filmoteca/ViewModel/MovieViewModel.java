package com.example.filmoteca.ViewModel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.filmoteca.Model.Movie;
import com.example.filmoteca.Repository.MovieRepository;
import com.example.filmoteca.Api.Resource;

import java.util.List;

public class MovieViewModel extends ViewModel {
    private final MovieRepository repository;

    public MutableLiveData<Resource<List<Movie>>> movies = new MutableLiveData<>();
    public MutableLiveData<Movie> movieSeleccionada = new MutableLiveData<>();
    public String idiomaActual = "";
    public boolean cleanAdapter = false;

    public MovieViewModel() {
        repository = new MovieRepository();
    }
    public void cargarMovies(String idioma) {
        movies.postValue(Resource.loading());

        repository.getMovie(idioma, result -> {
            if (result.status == Resource.Status.SUCCESS && result.data != null) {
                List<Movie> listaMovie = result.data;

                for (Movie m : listaMovie) {
                    repository.getTrailer(m.getId(), key -> {
                        if (key != null) {
                            m.setVideoKey(key);
                            movies.postValue(Resource.success(listaMovie));
                        }
                    });
                }
            }
            movies.postValue(result);
        });
    }
    public void reset(String nuevoIdioma) {
        this.idiomaActual = nuevoIdioma;
        this.cleanAdapter = true;

        repository.reiniciarPaginacion();

        movies.setValue(Resource.loading());
        cargarMovies(nuevoIdioma);
    }
    public void seleccionarMovie(Movie movie) {
        movieSeleccionada.setValue(movie);
    }

    public void cleanSeleccion() {
        movieSeleccionada.setValue(null);
    }
}