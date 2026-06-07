package com.example.filmoteca.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.filmoteca.R;
import com.example.filmoteca.ViewModel.MediaViewModel;
import com.example.filmoteca.databinding.FragmentDetalleSeguimientoBinding;

public class DetalleSeguimientoFragment extends Fragment {
    private FragmentDetalleSeguimientoBinding binding;
    private MediaViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentDetalleSeguimientoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(MediaViewModel.class);
        mostrarDetalle();
    }

    private void mostrarDetalle() {
        viewModel.seguimientoSeleccionado.observe(getViewLifecycleOwner(), seguimiento -> {
            if (seguimiento != null && binding != null) {
                binding.tvTituloDetalle.setText(seguimiento.getTitulo());
                binding.tvFechaDetalle.setText("Visualizado el: " + seguimiento.getFechaVisualizacion());
                binding.ratingBarDetalle.setRating(seguimiento.getPuntuacion());

                String prefsName = "Ajustes_" + seguimiento.getUser();
                SharedPreferences prefs = requireActivity().getSharedPreferences(prefsName, Context.MODE_PRIVATE);
                boolean soloWifi = prefs.getBoolean("solo_wifi", false);

                if (soloWifi) {
                    Glide.with(this).load(R.drawable.television).into(binding.imgPosterDetalle);
                } else {
                    if (seguimiento.getPosterPath() != null && !seguimiento.getPosterPath().isEmpty()) {
                        Glide.with(this).load(seguimiento.getPosterPath()).into(binding.imgPosterDetalle);
                    } else {
                        Glide.with(this).load(R.drawable.television).into(binding.imgPosterDetalle);
                    }
                }
                if (seguimiento.getImagenRecuerdo() != null && !seguimiento.getImagenRecuerdo().isEmpty()) {
                    binding.labelRecuerdo.setVisibility(View.VISIBLE);
                    binding.imgRecuerdoDetalle.setVisibility(View.VISIBLE);
                    Glide.with(this).load(seguimiento.getImagenRecuerdo()).into(binding.imgRecuerdoDetalle);
                } else {
                    binding.labelRecuerdo.setVisibility(View.GONE);
                    binding.imgRecuerdoDetalle.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}