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

import com.example.filmoteca.Adapter.SeriesAdapter;
import com.example.filmoteca.ViewModel.AuthViewModel;
import com.example.filmoteca.ViewModel.MediaViewModel;
import com.example.filmoteca.ViewModel.SerieViewModel;
import com.example.filmoteca.databinding.FragmentSeriesBinding;

import java.util.ArrayList;

public class SeriesFragment extends Fragment {
    private FragmentSeriesBinding binding;
    private SeriesAdapter adapter;
    private SerieViewModel viewModel;
    private MediaViewModel mediaViewModel;
    private AuthViewModel authViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSeriesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);
        viewModel = new ViewModelProvider(requireActivity()).get(SerieViewModel.class);
        mediaViewModel = new ViewModelProvider(requireActivity()).get(MediaViewModel.class);

        configurarRecyclerView();
        configurarPaginacion();

        String uid = authViewModel.getCurrentUser() != null ? authViewModel.getCurrentUser().getUid() : "";
        SharedPreferences prefs = requireActivity().getSharedPreferences("Ajustes_" + uid, Context.MODE_PRIVATE);
        String idiomaGuardado = prefs.getString("idioma_pref_codigo", "es-ES");

        if (viewModel.idiomaActual == null || viewModel.idiomaActual.isEmpty()) {
            viewModel.idiomaActual = idiomaGuardado;
            viewModel.cargarSeries(idiomaGuardado);
        } else if (!idiomaGuardado.equals(viewModel.idiomaActual)) {
            viewModel.reset(idiomaGuardado);
        }
        observarSeries();
    }

    private void configurarRecyclerView() {
        String user = "";
        if (authViewModel.getCurrentUser() != null) {
            user = authViewModel.getCurrentUser().getUid();
        }
        adapter = new SeriesAdapter(requireContext(), viewModel, mediaViewModel, user);
        binding.recyclerViewSerie.setAdapter(adapter);
        binding.recyclerViewSerie.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void observarSeries() {
        viewModel.series.observe(getViewLifecycleOwner(), resource -> {
            if (resource == null) return;

            switch (resource.status) {
                case LOADING:
                    if (viewModel.cleanAdapter || adapter.getItemCount() == 0) {
                        binding.progressLoading.setVisibility(View.VISIBLE);
                        binding.recyclerViewSerie.setVisibility(View.GONE);
                    }
                    binding.layoutError.setVisibility(View.GONE);
                    break;

                case SUCCESS:
                    binding.progressLoading.setVisibility(View.GONE);
                    binding.layoutError.setVisibility(View.GONE);
                    binding.recyclerViewSerie.setVisibility(View.VISIBLE);

                    if (viewModel.cleanAdapter) {
                        adapter.establecerLista(new ArrayList<>());
                        viewModel.cleanAdapter = false;
                    }

                    if (resource.data != null) {
                        adapter.addSerieList(resource.data);
                    }
                    break;

                case ERROR:
                    binding.progressLoading.setVisibility(View.GONE);
                    binding.recyclerViewSerie.setVisibility(View.GONE);
                    binding.layoutError.setVisibility(View.VISIBLE);
                    binding.sError.setText(resource.message);
                    break;
            }
        });
    }

    private void configurarPaginacion() {
        binding.recyclerViewSerie.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (!recyclerView.canScrollVertically(1)) {
                    viewModel.cargarSeries(viewModel.idiomaActual);
                }
            }
        });
    }
}