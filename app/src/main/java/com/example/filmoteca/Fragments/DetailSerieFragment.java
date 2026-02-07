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
import com.example.filmoteca.Model.Serie;
import com.example.filmoteca.R;
import com.example.filmoteca.ViewModel.MediaViewModel;
import com.example.filmoteca.ViewModel.MovieViewModel;
import com.example.filmoteca.ViewModel.SerieViewModel;
import com.example.filmoteca.databinding.FragmentDetailSerieBinding;


public class DetailSerieFragment extends Fragment {
    private SerieViewModel sviewModel;
    private MovieViewModel mviewModel;
    private MediaViewModel meviewModel;
    private FragmentDetailSerieBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentDetailSerieBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sviewModel = new ViewModelProvider(requireActivity()).get(SerieViewModel.class);
        mviewModel = new ViewModelProvider(requireActivity()).get(MovieViewModel.class);
        meviewModel = new ViewModelProvider(requireActivity()).get(MediaViewModel.class);


        if (sviewModel.serieSeleccionada.getValue() != null) {
            mostrarDetalleSerie();
        } else if (mviewModel.movieSeleccionada.getValue() != null) {
            mostrarDetallePelicula();
        } else {
            mostrarDetalleMedia();
        }
    }

    private void mostrarDetalleSerie() {
        sviewModel.serieSeleccionada.observe(getViewLifecycleOwner(), serie -> {
            if (serie != null) {
                binding.TituloDetalle.setText(serie.getName());
                binding.DescripcionDetalle.setText(serie.getOverwiew());
                binding.Estreno.setText("Estreno: " + serie.estreno());
                Glide.with(this).load(serie.getPoster_path()).into(binding.ImagenDetalle);

                binding.btnTrailer.setOnClickListener(v -> {
                    if (serie.getVideo_key() != null) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(serie.getVideo_key())));
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
                Glide.with(this).load(movie.getPoster_path()).into(binding.ImagenDetalle);

                binding.btnTrailer.setOnClickListener(v -> {
                    if (movie.getVideo_key() != null) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(movie.getVideo_key())));
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
                Glide.with(this).load(media.getPoster()).into(binding.ImagenDetalle);


                binding.btnTrailer.setVisibility(View.GONE);
            }
        });
    }
    @Override
    public void onDestroy() {
        super.onDestroy();

        if (sviewModel != null) sviewModel.limpiarSeleccion();
        if (mviewModel != null) mviewModel.limpiarSeleccion();

    }
    }

