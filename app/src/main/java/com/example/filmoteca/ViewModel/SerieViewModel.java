package com.example.filmoteca.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.filmoteca.Model.Serie;
import com.example.filmoteca.Repository.SeriesRepository;
import com.example.filmoteca.Api.Resource;
import java.util.List;

public class SerieViewModel extends AndroidViewModel {
    private final SeriesRepository repository;


    public MutableLiveData<Resource<List<Serie>>> series = new MutableLiveData<>();
    public MutableLiveData<Serie> serieSeleccionada = new MutableLiveData<>();
    public LiveData<List<Serie>> seriesPendiente;

    public SerieViewModel(@NonNull Application application) {
        super(application);
        repository = new SeriesRepository(application);

        cargarSeries();
    }

    public void cargarSeries() {
        repository.getSeries(result -> {
            if (result.status == Resource.Status.SUCCESS && result.data != null) {
                List<Serie> listaSeries = result.data;

                // Para cada serie en la lista, buscamos su trailer
                for (Serie s : listaSeries) {
                    repository.getTrailer(s.getId(), key -> {
                        if (key != null) {
                            // Guardamos la key en el objeto serie
                            s.setVideoKey(key);

                            // Notificamos a la UI que los datos han cambiado
                            // postValue se usa para actualizar la lista completa con las keys nuevas
                            series.postValue(Resource.success(listaSeries));
                        }
                    });
                }
            }
            series.postValue(result);
        });
    }

    public void seleccionarSerie(Serie serie) {
        serieSeleccionada.setValue(serie);
    }
    public void limpiarSeleccion() {
        serieSeleccionada.setValue(null);
    }

}