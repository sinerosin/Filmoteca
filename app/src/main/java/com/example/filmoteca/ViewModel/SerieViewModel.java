package com.example.filmoteca.ViewModel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.filmoteca.Model.Serie;
import com.example.filmoteca.Repository.SeriesRepository;
import com.example.filmoteca.Resource;
import java.util.List;

public class SerieViewModel extends ViewModel {
    private final SeriesRepository repository;


    public MutableLiveData<Resource<List<Serie>>> series = new MutableLiveData<>();
    public MutableLiveData<Serie> serieSeleccionada = new MutableLiveData<>();

    public SerieViewModel() {
        repository = new SeriesRepository();
        cargarSeries();
    }

    public void cargarSeries() {

        repository.getSeries(result -> {
            series.postValue(result);
        });
    }

    public void seleccionarSerie(Serie serie) {
        serieSeleccionada.setValue(serie);
    }
}