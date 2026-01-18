package com.example.filmoteca.Fragments;

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
import com.example.filmoteca.Adapter.SeriesAdapter;
import com.example.filmoteca.R;
import com.example.filmoteca.ViewModel.MovieViewModel;
import com.example.filmoteca.ViewModel.SerieViewModel;
import com.example.filmoteca.databinding.FragmentMovieBinding;
import com.example.filmoteca.databinding.FragmentSeriesBinding;


public class MovieFragment extends Fragment {

    private FragmentMovieBinding binding;
    private MoviesAdapter adapter;
    private MovieViewModel viewModel;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentMovieBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(MovieViewModel.class);


        configurarRecyclerView();
        observarMovies();
        configurarPaginacion();


        viewModel.cargarMovies();
    }

    private void configurarRecyclerView() {
        adapter = new MoviesAdapter(requireContext());
        binding.recyclerViewMovie.setAdapter(adapter);
        binding.recyclerViewMovie.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void observarMovies() {
        viewModel.Movies.observe(getViewLifecycleOwner(), resource -> {
            if (resource == null) return;

            // Gestionamos los diferentes estados
            switch (resource.status) {
                case LOADING:
                    binding.progressLoading.setVisibility(View.VISIBLE);
                    binding.layoutError.setVisibility(View.GONE);
                    binding.recyclerViewMovie.setVisibility(View.VISIBLE);
                    break;

                case SUCCESS:
                    binding.progressLoading.setVisibility(View.GONE);
                    binding.layoutError.setVisibility(View.GONE);
                    binding.recyclerViewMovie.setVisibility(View.VISIBLE);


                    adapter.addMovieList(resource.data);
                    break;

                case ERROR:
                    binding.progressLoading.setVisibility(View.GONE);
                    binding.recyclerViewMovie.setVisibility(View.GONE);
                    binding.layoutError.setVisibility(View.VISIBLE);
                    binding.sError.setText(resource.message);
                    break;
            }
        });
    }private void configurarPaginacion() {
        // Añadimos un listener al RecyclerView para detectar el scroll
        binding.recyclerViewMovie.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                // Comprobamos si el usuario ha llegado al final del RecyclerView.
                // canScrollVertically(1) devuelve false cuando NO se puede seguir bajando.
                if (!recyclerView.canScrollVertically(1)) {

                    // Si estamos en el final, pedimos al ViewModel que cargue la siguiente página
                    viewModel.cargarMovies();
                }
            }
        });
    }
}