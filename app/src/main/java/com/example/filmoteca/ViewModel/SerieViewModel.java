package com.example.filmoteca.ViewModel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.filmoteca.Model.Serie;
import com.example.filmoteca.Repository.SeriesRepository;
import com.example.filmoteca.Resource;
import com.example.filmoteca.Response.SerieResponse;

import java.util.List;

public class SerieViewModel extends ViewModel {
    private final SeriesRepository repository;
    public MutableLiveData<Resource<SerieResponse>> informacionSerie=new MutableLiveData<>();
    public MutableLiveData<Resource<List<SerieResponse>>> Series = new MutableLiveData<>();
    public SerieViewModel() {
        repository = new SeriesRepository();
    }
    public void cargarSeries()  {
        repository.getSeries( result -> {
            Series.postValue(result);
        });
    }

}
