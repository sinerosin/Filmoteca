package com.example.filmoteca.Repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.filmoteca.Model.Media;
import com.example.filmoteca.Model.MediaDao;
import com.example.filmoteca.Model.MediaDatabase;
import com.example.filmoteca.Model.Seguimiento;
import com.example.filmoteca.Model.SeguimientoDao;
import com.example.filmoteca.Model.SeguimientoDatabase;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MediaRepository {
    private MediaDao mediaDao;
    private SeguimientoDao seguimientoDao;
    private Executor executor;
    public MediaRepository(Application application) {
        mediaDao = MediaDatabase.getInstance(application).mediaDao();
        seguimientoDao= SeguimientoDatabase.getInstance(application).seguimientoDao();
        executor = Executors.newSingleThreadExecutor();
    }
    public void insertarMedia(Media media) {
        executor.execute(() -> mediaDao.insertarMedia(media));
    }
    public LiveData<List<Media>> obtenerTodas() {
        return mediaDao.obtenerTodos();
    }
    public void insertarSeguimiento(Seguimiento seguimiento) {
        executor.execute(() -> {
            seguimientoDao.insertarSeguimiento(seguimiento);
        });
    }
    public LiveData<List<Seguimiento>> obtenerSeguimientos() {
        return seguimientoDao.obtenerSeguimientos();
    }

}
