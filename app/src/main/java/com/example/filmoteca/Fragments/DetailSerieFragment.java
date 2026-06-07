package com.example.filmoteca.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.filmoteca.Adapter.ActorAdapter;
import com.example.filmoteca.Adapter.ComentarioAdapter;
import com.example.filmoteca.Model.Actor;
import com.example.filmoteca.R;
import com.example.filmoteca.ViewModel.ActorViewModel;
import com.example.filmoteca.ViewModel.AuthViewModel;
import com.example.filmoteca.ViewModel.MediaViewModel;
import com.example.filmoteca.ViewModel.MovieViewModel;
import com.example.filmoteca.ViewModel.SerieViewModel;
import com.example.filmoteca.databinding.FragmentDetailSerieBinding;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class DetailSerieFragment extends Fragment {

    private SerieViewModel sviewModel;
    private MovieViewModel mviewModel;
    private MediaViewModel meviewModel;
    private AuthViewModel authViewModel;
    private ActorViewModel actorViewModel;

    private FragmentDetailSerieBinding binding;
    private ComentarioAdapter comentarioAdapter;
    private ActorAdapter actorAdapter;

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String nombreUsuarioActual = "Anónimo";
    private String idMediaActual = "";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDetailSerieBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sviewModel = new ViewModelProvider(requireActivity()).get(SerieViewModel.class);
        mviewModel = new ViewModelProvider(requireActivity()).get(MovieViewModel.class);
        meviewModel = new ViewModelProvider(requireActivity()).get(MediaViewModel.class);
        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);
        actorViewModel = new ViewModelProvider(requireActivity()).get(ActorViewModel.class);

        configurarRecyclerViewComentarios();
        configurarRecyclerViewActores();
        recuperarNombreUsuario();
        observarActoresFav();

        if (sviewModel.serieSeleccionada.getValue() != null) {
            mostrarDetalleSerie();
        } else if (mviewModel.movieSeleccionada.getValue() != null) {
            mostrarDetallePelicula();
        } else {
            mostrarDetalleMedia();
        }
        configurarBotonEnviar();
    }

    private void configurarRecyclerViewComentarios() {
        comentarioAdapter = new ComentarioAdapter();
        binding.recyclerViewComentarios.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewComentarios.setAdapter(comentarioAdapter);
    }

    private void configurarRecyclerViewActores() {
        actorAdapter = new ActorAdapter(new ActorAdapter.OnActorClickListener() {
            @Override
            public void onFavToggle(Actor actor, boolean fav) {
                if (authViewModel.getCurrentUser() == null) return;
                String uid = authViewModel.getCurrentUser().getUid();

                if (fav) {
                    db.collection("usuarios").document(uid)
                            .collection("actores_favoritos").document(actor.getId()).delete();

                    new AlertDialog.Builder(requireContext())
                            .setTitle("Favoritos")
                            .setMessage(actor.getNombre() + " eliminado de favoritos.")
                            .setPositiveButton("Aceptar", null)
                            .show();
                } else {
                    db.collection("usuarios").document(uid)
                            .collection("actores_favoritos").document(actor.getId()).set(actor);

                    new AlertDialog.Builder(requireContext())
                            .setTitle("Favoritos")
                            .setMessage(actor.getNombre() + " añadido a favoritos.")
                            .setPositiveButton("Aceptar", null)
                            .show();
                }
            }

            @Override
            public void onActorClick(Actor actor) {
                Bundle b = new Bundle();
                b.putSerializable("actor", actor);
                Navigation.findNavController(requireView()).navigate(R.id.detalleActorFragment, b);
            }
        });

        binding.recyclerViewActores.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.recyclerViewActores.setAdapter(actorAdapter);
    }

    private void observarActoresFav() {
        if (authViewModel.getCurrentUser() == null) return;
        String uid = authViewModel.getCurrentUser().getUid();

        db.collection("usuarios").document(uid).collection("actores_favoritos")
                .addSnapshotListener((value, error) -> {
                    if (value != null) {
                        List<String> favsIds = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : value) {
                            favsIds.add(doc.getId());
                        }
                        actorAdapter.establecerFavoritos(favsIds);
                    }
                });
    }

    private void cargarReparto(int idMedia, boolean esSerie) {
        String uid = authViewModel.getCurrentUser() != null ? authViewModel.getCurrentUser().getUid() : "";
        SharedPreferences prefs = requireActivity().getSharedPreferences("Ajustes_" + uid, Context.MODE_PRIVATE);
        String idiomaGuardado = prefs.getString("idioma_pref_codigo", "es-ES");

        if (idiomaGuardado == null || idiomaGuardado.trim().isEmpty()) {
            idiomaGuardado = "es-ES";
        }

        LiveData<List<Actor>> creditosLiveData = esSerie
                ? actorViewModel.getRepartoSerie(idMedia, idiomaGuardado)
                : actorViewModel.getRepartoMovie(idMedia, idiomaGuardado);

        creditosLiveData.observe(getViewLifecycleOwner(), reparto -> {
            if (reparto != null) {
                if (reparto.size() > 12) {
                    reparto = reparto.subList(0, 12);
                }
                actorAdapter.establecerActores(reparto);
            }
        });
    }

    private void cargarSeccionComentarios(String idMedia) {
        this.idMediaActual = idMedia;
        meviewModel.obtenerComentarios(idMedia).observe(getViewLifecycleOwner(), listaComentarios -> {
            if (listaComentarios != null) {
                comentarioAdapter.establecerComentarios(listaComentarios);
            }
        });
    }

    private void recuperarNombreUsuario() {
        if (authViewModel.getCurrentUser() != null) {
            authViewModel.obtenerNombreUsuario(authViewModel.getCurrentUser().getUid())
                    .observe(getViewLifecycleOwner(), nombre -> {
                        if (nombre != null && !nombre.trim().isEmpty()) {
                            nombreUsuarioActual = nombre;
                        }
                    });
        }
    }

    private void configurarBotonEnviar() {
        binding.btnEnviarComentario.setOnClickListener(v -> {
            String textoMensaje = binding.etNuevoComentario.getText().toString().trim();
            if (!textoMensaje.isEmpty() && !idMediaActual.isEmpty()) {
                meviewModel.enviarComentario(idMediaActual, nombreUsuarioActual, textoMensaje);
                binding.etNuevoComentario.setText("");

                new AlertDialog.Builder(requireContext())
                        .setTitle("Comentario Enviado")
                        .setMessage("Tu comentario se ha publicado correctamente.")
                        .setPositiveButton("Aceptar", null)
                        .show();
            } else {
                new AlertDialog.Builder(requireContext())
                        .setTitle("Error")
                        .setMessage("El comentario no puede estar vacío.")
                        .setPositiveButton("Aceptar", null)
                        .show();
            }
        });
    }

    private void mostrarDetalleSerie() {
        sviewModel.serieSeleccionada.observe(getViewLifecycleOwner(), serie -> {
            if (serie != null) {
                binding.TituloDetalle.setText(serie.getName());
                binding.DescripcionDetalle.setText(serie.getOverwiew());
                binding.Estreno.setText("Estreno: " + serie.estreno());

                if (debeAhorrarDatos()) {
                    Glide.with(this).load(R.drawable.television).into(binding.ImagenDetalle);
                } else {
                    Glide.with(this).load(serie.getPoster_path()).into(binding.ImagenDetalle);
                }

                cargarSeccionComentarios("serie_" + serie.getId());
                cargarReparto(serie.getId(), true);

                binding.btnTrailer.setOnClickListener(v -> {
                    if (serie.getVideo_key() != null && !serie.getVideo_key().trim().isEmpty()) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(serie.getVideo_key())));
                    } else {
                        new AlertDialog.Builder(requireContext())
                                .setTitle("Tráiler no disponible")
                                .setMessage("Esta serie no tiene un tráiler registrado en la nube.")
                                .setPositiveButton("Aceptar", null)
                                .show();
                    }
                });
            }
        });
    }

    private void mostrarDetallePelicula() {
        mviewModel.movieSeleccionada.observe(getViewLifecycleOwner(), movie -> {
            if (movie != null) {
                binding.TituloDetalle.setText(movie.getTitulo());
                binding.DescripcionDetalle.setText(movie.getOverwiew());
                binding.Estreno.setText("Estreno: " + movie.getFecha());

                if (debeAhorrarDatos()) {
                    Glide.with(this).load(R.drawable.television).into(binding.ImagenDetalle);
                } else {
                    Glide.with(this).load(movie.getPoster_path()).into(binding.ImagenDetalle);
                }

                cargarSeccionComentarios("movie_" + movie.getId());
                cargarReparto(movie.getId(), false);

                binding.btnTrailer.setOnClickListener(v -> {
                    if (movie.getVideo_key() != null && !movie.getVideo_key().trim().isEmpty()) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(movie.getVideo_key())));
                    } else {
                        new AlertDialog.Builder(requireContext())
                                .setTitle("Tráiler no disponible")
                                .setMessage("Esta película no tiene un tráiler registrado en la nube.")
                                .setPositiveButton("Aceptar", null)
                                .show();
                    }
                });
            }
        });
    }

    private void mostrarDetalleMedia() {
        meviewModel.mediaSeleccionada.observe(getViewLifecycleOwner(), media -> {
            if (media != null) {
                binding.TituloDetalle.setText(media.getTitle());
                binding.DescripcionDetalle.setText(media.getOverview());
                binding.Estreno.setText("Estreno: " + media.getReleaseDate());

                if (debeAhorrarDatos()) {
                    Glide.with(this).load(R.drawable.television).into(binding.ImagenDetalle);
                } else {
                    Glide.with(this).load(media.getPoster()).into(binding.ImagenDetalle);
                }

                cargarSeccionComentarios("media_" + media.getId());
                cargarReparto(media.getId(), false);

                binding.btnTrailer.setVisibility(View.VISIBLE);
                binding.btnTrailer.setOnClickListener(v -> {
                    if (media.getVideoKey() != null && !media.getVideoKey().trim().isEmpty()) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(media.getVideoKey())));
                    } else {
                        new AlertDialog.Builder(requireContext())
                                .setTitle("Tráiler no disponible")
                                .setMessage("Este elemento no tiene un tráiler registrado en la nube.")
                                .setPositiveButton("Aceptar", null)
                                .show();
                    }
                });
            }
        });
    }

    private boolean debeAhorrarDatos() {
        String uid = "";
        if (authViewModel != null && authViewModel.getCurrentUser() != null) {
            uid = authViewModel.getCurrentUser().getUid();
        }
        SharedPreferences prefs = requireActivity().getSharedPreferences("Ajustes_" + uid, Context.MODE_PRIVATE);
        return prefs.getBoolean("solo_wifi", false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (sviewModel != null) sviewModel.cleanSeleccion();
        if (mviewModel != null) mviewModel.cleanSeleccion();
    }
}