package com.example.filmoteca.ViewModel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.filmoteca.Model.Movie;
import com.example.filmoteca.Model.Serie;
import com.example.filmoteca.Repository.MovieRepository;
import com.example.filmoteca.Api.Resource;

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


    public void seleccionarMovie(Movie movie) {
        movieSeleccionada.setValue(movie);
    }
    public void limpiarSeleccion() {
        movieSeleccionada.setValue(null);
    }
}
