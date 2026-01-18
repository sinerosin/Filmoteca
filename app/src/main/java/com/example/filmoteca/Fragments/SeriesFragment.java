package com.example.filmoteca.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.filmoteca.Adapter.SeriesAdapter;
import com.example.filmoteca.Model.Serie;
import com.example.filmoteca.R;
import com.example.filmoteca.Repository.SeriesRepository;
import com.example.filmoteca.ViewModel.SerieViewModel;
import com.example.filmoteca.databinding.FragmentSeriesBinding;

import java.util.ArrayList;
import java.util.List;


public class SeriesFragment extends Fragment {
    private FragmentSeriesBinding binding;
    private SeriesAdapter adapter;
    private SerieViewModel viewModel;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentSeriesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(SerieViewModel.class);


        configurarRecyclerView();
        observarSeries();
        configurarPaginacion();


        viewModel.cargarSeries();
    }

    private void configurarRecyclerView() {
        adapter = new SeriesAdapter(requireContext());
        binding.recyclerViewSerie.setAdapter(adapter);
        binding.recyclerViewSerie.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void observarSeries() {
        viewModel.informacionSerie.observe(getViewLifecycleOwner(), resource -> {
            if (resource == null) return;

            // Gestionamos los diferentes estados
            switch (resource.status) {
                case LOADING:
                    binding.progressLoading.setVisibility(View.VISIBLE);
                    binding.layoutError.setVisibility(View.GONE);
                    binding.recyclerViewSerie.setVisibility(View.VISIBLE);
                    break;

                case SUCCESS:
                    binding.progressLoading.setVisibility(View.GONE);
                    binding.layoutError.setVisibility(View.GONE);
                    binding.recyclerViewSerie.setVisibility(View.VISIBLE);


                    adapter.addSerieList(resource.data);
                    break;

                case ERROR:
                    binding.progressLoading.setVisibility(View.GONE);
                    binding.recyclerViewSerie.setVisibility(View.GONE);
                    binding.layoutError.setVisibility(View.VISIBLE);
                    binding.sError.setText(resource.message);
                    break;
            }
        });
    }private void configurarPaginacion() {
        // Añadimos un listener al RecyclerView para detectar el scroll
        binding.recyclerViewSerie.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                // Comprobamos si el usuario ha llegado al final del RecyclerView.
                // canScrollVertically(1) devuelve false cuando NO se puede seguir bajando.
                if (!recyclerView.canScrollVertically(1)) {

                    // Si estamos en el final, pedimos al ViewModel que cargue la siguiente página
                    viewModel.cargarSeries();
                }
            }
        });
    }
}