package com.example.filmoteca.ViewModel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.filmoteca.Model.Serie;
import com.example.filmoteca.Repository.SeriesRepository;

import java.util.List;

public class SerieViewModel extends ViewModel {
    private SeriesRepository repository;
    public MutableLiveData<List<Serie>> series = new MutableLiveData<>();
    public MutableLiveData<Serie> serieSeleccionada = new MutableLiveData<>();

    public SerieViewModel() {
        repository = new SeriesRepository();
        series.setValue(repository.getSeries());
    }
    public void seleccionarSerie(Serie serie)  {
        serieSeleccionada.setValue(serie);
    }

}
