package com.example.filmoteca.Repository;

import android.app.Application;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.filmoteca.Model.Media;
import com.example.filmoteca.Model.Seguimiento;
import com.example.filmoteca.Model.SeguimientoDao;
import com.example.filmoteca.Model.SeguimientoDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MediaRepository {
    private SeguimientoDao seguimientoDao;
    private Executor executor;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public MediaRepository(Application application) {
        seguimientoDao = SeguimientoDatabase.getInstance(application).seguimientoDao();
        executor = Executors.newSingleThreadExecutor();
    }

    public void insertarMedia(Media media) {
        if (media == null || media.getUser() == null) return;
        db.collection("usuarios")
                .document(media.getUser())
                .collection("pendientes")
                .document(String.valueOf(media.getId()))
                .set(media);
    }
    public LiveData<List<Media>> obtenerTodas(String user) {
        MutableLiveData<List<Media>> liveData = new MutableLiveData<>();

        db.collection("usuarios")
                .document(user)
                .collection("pendientes")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        liveData.setValue(new ArrayList<>());
                        return;
                    }

                    List<Media> listaPendientes = new ArrayList<>();
                    if (value != null) {
                        for (QueryDocumentSnapshot doc : value) {
                            Media media = doc.toObject(Media.class);
                            listaPendientes.add(media);
                        }
                    }
                    liveData.setValue(listaPendientes);
                });

        return liveData;
    }

    public void eliminarMedia(Media media) {
        if (media == null || media.getUser() == null) return;

        db.collection("usuarios")
                .document(media.getUser())
                .collection("pendientes")
                .document(String.valueOf(media.getId()))
                .delete();
    }
    public void insertarSeguimiento(Seguimiento seguimiento) {
        executor.execute(() -> {
            seguimientoDao.insertarSeguimiento(seguimiento);
        });
    }

    public LiveData<List<Seguimiento>> obtenerSeguimientos(String user) {
        return seguimientoDao.obtenerSeguimientos(user);
    }

    public void eliminarSeguimiento(Seguimiento seguimiento) {
        executor.execute(() -> seguimientoDao.eliminarSeguimiento(seguimiento));
    }
}