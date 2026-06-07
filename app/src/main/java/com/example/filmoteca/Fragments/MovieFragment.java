package com.example.filmoteca.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.filmoteca.Adapter.MoviesAdapter;
import com.example.filmoteca.ViewModel.AuthViewModel;
import com.example.filmoteca.ViewModel.MediaViewModel;
import com.example.filmoteca.ViewModel.MovieViewModel;
import com.example.filmoteca.databinding.FragmentMovieBinding;

import java.util.ArrayList;

public class MovieFragment extends Fragment {

    private FragmentMovieBinding binding;
    private MoviesAdapter adapter;
    private MovieViewModel viewModel;
    private MediaViewModel mediaViewModel;
    private AuthViewModel authViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMovieBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);
        viewModel = new ViewModelProvider(requireActivity()).get(MovieViewModel.class);
        mediaViewModel = new ViewModelProvider(requireActivity()).get(MediaViewModel.class);

        configurarRecyclerView();
        configurarPaginacion();

        String uid = authViewModel.getCurrentUser() != null ? authViewModel.getCurrentUser().getUid() : "";
        SharedPreferences prefs = requireActivity().getSharedPreferences("Ajustes_" + uid, Context.MODE_PRIVATE);
        String idiomaGuardado = prefs.getString("idioma_pref_codigo", "es-ES");

        if (viewModel.idiomaActual == null || viewModel.idiomaActual.isEmpty()) {
            viewModel.idiomaActual = idiomaGuardado;
            viewModel.cargarMovies(idiomaGuardado);
        } else if (!idiomaGuardado.equals(viewModel.idiomaActual)) {
            viewModel.reset(idiomaGuardado);
        }

        observarMovies();
    }

    private void configurarRecyclerView() {
        String user = "";
        if (authViewModel.getCurrentUser() != null) {
            user = authViewModel.getCurrentUser().getUid();
        }
        adapter = new MoviesAdapter(requireContext(), viewModel, mediaViewModel, user);
        binding.recyclerViewMovie.setAdapter(adapter);
        binding.recyclerViewMovie.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void observarMovies() {
        viewModel.movies.observe(getViewLifecycleOwner(), resource -> {
            if (resource == null) return;

            switch (resource.status) {
                case LOADING:
                    if (viewModel.cleanAdapter || adapter.getItemCount() == 0) {
                        binding.progressLoading.setVisibility(View.VISIBLE);
                        binding.recyclerViewMovie.setVisibility(View.GONE);
                    }
                    binding.layoutError.setVisibility(View.GONE);
                    break;

                case SUCCESS:
                    binding.progressLoading.setVisibility(View.GONE);
                    binding.layoutError.setVisibility(View.GONE);
                    binding.recyclerViewMovie.setVisibility(View.VISIBLE);

                    if (viewModel.cleanAdapter) {
                        adapter.establecerLista(new ArrayList<>());
                        viewModel.cleanAdapter = false;
                    }

                    if (resource.data != null) {
                        adapter.addMovieList(resource.data);
                    }
                    break;

                case ERROR:
                    binding.progressLoading.setVisibility(View.GONE);
                    binding.recyclerViewMovie.setVisibility(View.GONE);
                    binding.layoutError.setVisibility(View.VISIBLE);
                    binding.mError.setText(resource.message);
                    break;
            }
        });
    }

    private void configurarPaginacion() {
        binding.recyclerViewMovie.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (!recyclerView.canScrollVertically(1)) {
                    viewModel.cargarMovies(viewModel.idiomaActual);
                }
            }
        });
    }
}