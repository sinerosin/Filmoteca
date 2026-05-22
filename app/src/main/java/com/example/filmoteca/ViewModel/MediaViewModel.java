package com.example.filmoteca.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.filmoteca.Model.Comentario;
import com.example.filmoteca.Model.Media;
import com.example.filmoteca.Model.Seguimiento;
import com.example.filmoteca.Repository.MediaRepository;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class MediaViewModel extends AndroidViewModel {
    private MediaRepository mediaRepository;
    public MutableLiveData<Media> mediaSeleccionada = new MutableLiveData<>();
    public MutableLiveData<Seguimiento> seguimientoSeleccionado = new MutableLiveData<>();

    public MediaViewModel(@NonNull Application application) {
        super(application);
        mediaRepository = new MediaRepository(application);
    }
    public LiveData<List<Media>> obtenerMedia(String user) {
        return mediaRepository.obtenerTodas(user);
    }

    public void insertarMedia(Media media) {
        mediaRepository.insertarMedia(media);
    }

    public void eliminarMedia(Media media) {
        mediaRepository.eliminarMedia(media);
    }

    public void seleccionarMedia(Media media) {
        mediaSeleccionada.setValue(media);
    }
    public LiveData<List<Seguimiento>> obtenerSeguimiento(String user) {
        return mediaRepository.obtenerSeguimientos(user);
    }

    public void insertarSeguimiento(Seguimiento seguimiento) {
        mediaRepository.insertarSeguimiento(seguimiento);
    }

    public void seleccionarSeguimiento(Seguimiento seguimiento) {
        seguimientoSeleccionado.setValue(seguimiento);
    }

    public void eliminarSeguimiento(Seguimiento seguimiento) {
        mediaRepository.eliminarSeguimiento(seguimiento);
    }
    public void enviarComentario(String idMedia, String userName, String mensaje) {
        if (mensaje.trim().isEmpty()) return;

        Comentario nuevoComentario = new Comentario(userName, mensaje, Timestamp.now());

        FirebaseFirestore.getInstance()
                .collection("comentarios")
                .document(idMedia)
                .collection("mensajes")
                .add(nuevoComentario); // Genera un ID automático para el comentario
    }

    public LiveData<List<Comentario>> obtenerComentarios(String idMedia) {
        MutableLiveData<List<Comentario>> liveData = new MutableLiveData<>();

        FirebaseFirestore.getInstance()
                .collection("comentarios")
                .document(idMedia)
                .collection("mensajes")
                .orderBy("timestamp", Query.Direction.DESCENDING) // Los más recientes primero
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        liveData.setValue(new ArrayList<>());
                        return;
                    }
                    List<Comentario> comentarios = new ArrayList<>();
                    if (value != null) {
                        for (QueryDocumentSnapshot doc : value) {
                            comentarios.add(doc.toObject(Comentario.class));
                        }
                    }
                    liveData.setValue(comentarios);
                });

        return liveData;
    }
}