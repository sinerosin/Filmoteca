package com.example.filmoteca.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.filmoteca.Adapter.MovieFilmoGraphAdapter;
import com.example.filmoteca.Adapter.SerieFilmoGraphAdapter;
import com.example.filmoteca.Model.Actor;
import com.example.filmoteca.R;
import com.example.filmoteca.ViewModel.ActorViewModel;
import com.example.filmoteca.ViewModel.AuthViewModel;
import com.example.filmoteca.ViewModel.MovieViewModel;
import com.example.filmoteca.ViewModel.SerieViewModel;
import com.example.filmoteca.databinding.FragmentDetalleActorBinding;

public class DetalleActorFragment extends Fragment {

    private FragmentDetalleActorBinding binding;
    private MovieFilmoGraphAdapter movieAdapter;
    private SerieFilmoGraphAdapter serieAdapter;

    private ActorViewModel actorViewModel;
    private MovieViewModel mviewModel;
    private SerieViewModel sviewModel;
    private AuthViewModel authViewModel;

    public DetalleActorFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDetalleActorBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        actorViewModel = new ViewModelProvider(requireActivity()).get(ActorViewModel.class);
        mviewModel = new ViewModelProvider(requireActivity()).get(MovieViewModel.class);
        sviewModel = new ViewModelProvider(requireActivity()).get(SerieViewModel.class);
        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);

        configurarRecyclerViews();

        if (getArguments() != null) {
            Actor actor = (Actor) getArguments().getSerializable("actor");
            if (actor != null) {
                binding.tvNombreActorDetalle.setText(actor.getNombre());
                Glide.with(this)
                        .load(actor.getFotoUrl())
                        .placeholder(android.R.drawable.ic_menu_gallery)
                        .error(android.R.drawable.ic_menu_report_image)
                        .into(binding.ivActorGrande);

                try {
                    int actorId = Integer.parseInt(actor.getId());
                    cargarDatos(actorId);
                } catch (NumberFormatException e) {
                    new AlertDialog.Builder(requireContext())
                            .setTitle("Error de Identificación")
                            .setMessage("El ID del actor proporcionado no es válido.")
                            .setPositiveButton("Aceptar", null)
                            .show();
                }
            }
        }
    }

    private void configurarRecyclerViews() {
        movieAdapter = new MovieFilmoGraphAdapter(movie -> {
            sviewModel.cleanSeleccion();
            mviewModel.cleanSeleccion();
            mviewModel.movieSeleccionada.setValue(movie);
            Navigation.findNavController(requireView()).navigate(R.id.detailSerieFragment);
        });
        binding.rvPeliculasActor.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvPeliculasActor.setAdapter(movieAdapter);

        serieAdapter = new SerieFilmoGraphAdapter(serie -> {
            sviewModel.cleanSeleccion();
            mviewModel.cleanSeleccion();
            sviewModel.serieSeleccionada.setValue(serie);
            Navigation.findNavController(requireView()).navigate(R.id.detailSerieFragment);
        });
        binding.rvSeriesActor.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvSeriesActor.setAdapter(serieAdapter);
    }

    private void cargarDatos(int actorId) {
        String uid = authViewModel.getCurrentUser() != null ? authViewModel.getCurrentUser().getUid() : "";
        SharedPreferences prefs = requireActivity().getSharedPreferences("Ajustes_" + uid, Context.MODE_PRIVATE);

        String idiomaGuardado = prefs.getString("idioma_pref_codigo", "es-ES");
        if (idiomaGuardado == null || idiomaGuardado.trim().isEmpty()) {
            idiomaGuardado = "es-ES";
        }

        actorViewModel.getMoviesActor(actorId, idiomaGuardado).observe(getViewLifecycleOwner(), peliculas -> {
            if (peliculas != null) {
                movieAdapter.establecerMovies(peliculas);
            }
        });

        actorViewModel.getSeriesActor(actorId, idiomaGuardado).observe(getViewLifecycleOwner(), series -> {
            if (series != null) {
                serieAdapter.establecerSerie(series);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}