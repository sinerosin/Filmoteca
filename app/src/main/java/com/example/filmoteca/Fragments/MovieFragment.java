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
import com.example.filmoteca.ViewModel.AuthViewModel;
import com.example.filmoteca.ViewModel.MediaViewModel;
import com.example.filmoteca.ViewModel.MovieViewModel;
import com.example.filmoteca.ViewModel.SerieViewModel;
import com.example.filmoteca.databinding.FragmentMovieBinding;
import com.example.filmoteca.databinding.FragmentSeriesBinding;


public class MovieFragment extends Fragment {

    private FragmentMovieBinding binding;
    private MoviesAdapter adapter;
    private MovieViewModel viewModel;
    private MediaViewModel mediaViewModel;
    private AuthViewModel authViewModel;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentMovieBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);
        viewModel = new ViewModelProvider(requireActivity()).get(MovieViewModel.class);
        mediaViewModel=new ViewModelProvider(requireActivity()).get(MediaViewModel.class);


        configurarRecyclerView();
        observarMovies();
        configurarPaginacion();


        viewModel.cargarMovies();
    }

    private void configurarRecyclerView() {
        String user = "";
        if (authViewModel.getCurrentUser() != null) {
            user = authViewModel.getCurrentUser().getUid();
        }
        adapter = new MoviesAdapter(requireContext(),viewModel,mediaViewModel,user);
        binding.recyclerViewMovie.setAdapter(adapter);
        binding.recyclerViewMovie.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void observarMovies() {
        viewModel.movies.observe(getViewLifecycleOwner(), resource -> {
            if (resource == null) return;

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
                    binding.mError.setText(resource.message);
                    break;
            }
        });
    }private void configurarPaginacion() {

        binding.recyclerViewMovie.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);


                if (!recyclerView.canScrollVertically(1)) {

                    viewModel.cargarMovies();
                }
            }
        });
    }
}