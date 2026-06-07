package com.example.filmoteca.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.filmoteca.Model.Serie;
import com.example.filmoteca.Repository.SeriesRepository;
import com.example.filmoteca.Api.Resource;

import java.util.List;

public class SerieViewModel extends AndroidViewModel {
    private final SeriesRepository repository;

    public MutableLiveData<Resource<List<Serie>>> series = new MutableLiveData<>();
    public MutableLiveData<Serie> serieSeleccionada = new MutableLiveData<>();
    public String idiomaActual = "";
    public boolean cleanAdapter = false;

    public SerieViewModel(@NonNull Application application) {
        super(application);
        repository = new SeriesRepository(application);
    }
    public void cargarSeries(String idioma) {
        series.postValue(Resource.loading());

        repository.getSeries(idioma, result -> {
            if (result.status == Resource.Status.SUCCESS && result.data != null) {
                List<Serie> listaSeries = result.data;

                for (Serie s : listaSeries) {
                    repository.getTrailer(s.getId(), key -> {
                        if (key != null) {
                            s.setVideoKey(key);
                            series.postValue(Resource.success(listaSeries));
                        }
                    });
                }
            }
            series.postValue(result);
        });
    }

    public void reset(String nuevoIdioma) {
        this.idiomaActual = nuevoIdioma;
        this.cleanAdapter = true;

        repository.reiniciarPaginacion();

        series.setValue(Resource.loading());

        cargarSeries(nuevoIdioma);
    }

    public void seleccionarSerie(Serie serie) {
        serieSeleccionada.setValue(serie);
    }

    public void cleanSeleccion() {
        serieSeleccionada.setValue(null);
    }
}