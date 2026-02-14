package com.example.filmoteca.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.filmoteca.Model.Media;
import com.example.filmoteca.Model.Movie;
import com.example.filmoteca.Model.Seguimiento;
import com.example.filmoteca.Repository.MediaRepository;

import java.util.List;

public class MediaViewModel extends AndroidViewModel {
    private MediaRepository mediaRepository;
    public MutableLiveData<Media> mediaSeleccionada = new MutableLiveData<>();
    public MutableLiveData<Seguimiento> seguimientoSeleccionado=new MutableLiveData<>();;


    public MediaViewModel(@NonNull Application application) {
        super(application);

        mediaRepository = new MediaRepository(application);
    }

    public LiveData<List<Media>> obtenerMedia() {

        return mediaRepository.obtenerTodas();
    }
    public LiveData<List<Seguimiento>> obtenerSeguimiento() {
        return mediaRepository.obtenerSeguimientos();
    }

    public void insertarMedia(Media Media) {
        mediaRepository.insertarMedia(Media);
    }
    public void seleccionarMedia(Media Media) {
        mediaSeleccionada.setValue(Media);
    }
    public void insertarSeguimiento(Seguimiento seguimiento) {
        mediaRepository.insertarSeguimiento(seguimiento);
    }
    public void seleccionarSeguimiento(Seguimiento seguimiento) {
        seguimientoSeleccionado.setValue(seguimiento);
    }
    public void eliminarSeguimiento(Seguimiento seguimiento) {
        mediaRepository.eliminarSeguimiento(seguimiento); // Puente hacia el repositorio
    }
}
