package com.example.filmoteca.Repository;

import android.app.Application;
import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.filmoteca.Model.Media;
import com.example.filmoteca.Model.Seguimiento;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class MediaRepository {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public MediaRepository(Application application) {
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
        if (seguimiento == null || seguimiento.getUser() == null) return;
        if (seguimiento.getIdInternal() == 0) {
            db.collection("usuarios")
                    .document(seguimiento.getUser())
                    .collection("seguimientos")
                    .add(seguimiento);
        } else {
            db.collection("usuarios")
                    .document(seguimiento.getUser())
                    .collection("seguimientos")
                    .document(String.valueOf(seguimiento.getIdInternal()))
                    .set(seguimiento);
        }
    }
    public LiveData<List<Seguimiento>> obtenerSeguimientosFiltrados(
            String user, String titulo, int criterioOrden,
            String fechaDesde, String fechaHasta, float puntMin, float puntMax) {

        MutableLiveData<List<Seguimiento>> liveData = new MutableLiveData<>();
        Query query = db.collection("usuarios").document(user).collection("seguimientos");

        boolean filtroTitulo = (titulo != null && !titulo.isEmpty());
        boolean filtroPuntuacion = (puntMin > 0f || puntMax < 5f);
        boolean filtroFechas = ((fechaDesde != null && !fechaDesde.isEmpty()) || (fechaHasta != null && !fechaHasta.isEmpty()));
        if (filtroTitulo) {
            query = query.orderBy("titulo")
                    .startAt(titulo)
                    .endAt(titulo + "\uf8ff");
        } else {
            if (filtroPuntuacion) {
                query = query.whereGreaterThanOrEqualTo("puntuacion", puntMin)
                        .whereLessThanOrEqualTo("puntuacion", puntMax);
            }

            if (filtroFechas && !filtroPuntuacion) {
                if (fechaDesde != null && !fechaDesde.isEmpty()) {
                    query = query.whereGreaterThanOrEqualTo("fechaVisualizacion", fechaDesde);
                }
                if (fechaHasta != null && !fechaHasta.isEmpty()) {
                    query = query.whereLessThanOrEqualTo("fechaVisualizacion", fechaHasta);
                }
            }

            if (filtroPuntuacion) {
                query = query.orderBy("puntuacion", (criterioOrden == 4) ? Query.Direction.ASCENDING : Query.Direction.DESCENDING);
            } else if (filtroFechas) {
                query = query.orderBy("fechaVisualizacion", (criterioOrden == 2) ? Query.Direction.ASCENDING : Query.Direction.DESCENDING);
            } else {
                switch (criterioOrden) {
                    case 1:
                        query = query.orderBy("fechaVisualizacion", Query.Direction.DESCENDING);
                        break;
                    case 2:
                        query = query.orderBy("fechaVisualizacion", Query.Direction.ASCENDING);
                        break;
                    case 3:
                        query = query.orderBy("puntuacion", Query.Direction.DESCENDING);
                        break;
                    case 4:
                        query = query.orderBy("puntuacion", Query.Direction.ASCENDING);
                        break;
                }
            }
        }

        query.addSnapshotListener((value, error) -> {
            if (error != null) {
                Log.e("FirestoreDB", "Error en consulta nativa: " + error.getMessage());
                liveData.setValue(new ArrayList<>());
                return;
            }

            List<Seguimiento> listaFiltrada = new ArrayList<>();
            if (value != null) {
                for (QueryDocumentSnapshot doc : value) {
                    Seguimiento seguimiento = doc.toObject(Seguimiento.class);
                    if (seguimiento.getIdInternal() == 0) {
                        seguimiento.setIdInternal(doc.getId().hashCode());
                    }
                    listaFiltrada.add(seguimiento);
                }
            }
            liveData.setValue(listaFiltrada);
        });

        return liveData;
    }

    public LiveData<List<Seguimiento>> obtenerSeguimientos(String user) {
        return obtenerSeguimientosFiltrados(user, "", 1, "", "", 0f, 5f);
    }

    public void eliminarSeguimiento(Seguimiento seguimiento) {
        if (seguimiento == null || seguimiento.getUser() == null) return;
        db.collection("usuarios")
                .document(seguimiento.getUser())
                .collection("seguimientos")
                .addSnapshotListener((value, error) -> {
                    if (value != null) {
                        for (QueryDocumentSnapshot doc : value) {
                            Seguimiento s = doc.toObject(Seguimiento.class);
                            if (s.getTitulo().equals(seguimiento.getTitulo()) || doc.getId().hashCode() == seguimiento.getIdInternal()) {
                                db.collection("usuarios")
                                        .document(seguimiento.getUser())
                                        .collection("seguimientos")
                                        .document(doc.getId())
                                        .delete();
                                break;
                            }
                        }
                    }
                });
    }
}