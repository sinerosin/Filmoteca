package com.example.filmoteca.Fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.filmoteca.R;
import com.example.filmoteca.ViewModel.MovieViewModel;
import com.example.filmoteca.ViewModel.SerieViewModel;
import com.example.filmoteca.databinding.FragmentDetailMovieBinding;
import com.example.filmoteca.databinding.FragmentDetailSerieBinding;


public class DetailMovieFragment extends Fragment {
    private MovieViewModel viewModel;
    private FragmentDetailMovieBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentDetailMovieBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(MovieViewModel.class);
        viewModel.movieSeleccionada.observe(getViewLifecycleOwner(),movie -> {
            if (movie != null) {
                binding.mTituloDetalle.setText(movie.getTitulo());
                binding.mDescripcionDetalle.setText(movie.getOverwiew());
                binding.mEstreno.setText("Estreno: " + movie.getFecha());

                // Cargar la imagen
                Glide.with(this).load(movie.getPoster_path()).into(binding.mImagenDetalle);

                // Configurar el link a YouTube
                binding.mbtnTrailer.setOnClickListener(v -> {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(movie.getVideo_key()));
                    startActivity(intent);
                });
            } else {

                Toast.makeText(requireContext(), "No se pudo cargar el detalle de la pelicula", Toast.LENGTH_SHORT).show();
                requireActivity().onBackPressed();
            }
        });
    }
}