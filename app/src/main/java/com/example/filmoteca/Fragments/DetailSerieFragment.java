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
import com.example.filmoteca.ViewModel.SerieViewModel;
import com.example.filmoteca.databinding.FragmentDetailSerieBinding;


public class DetailSerieFragment extends Fragment {
    private SerieViewModel viewModel;
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
        viewModel = new ViewModelProvider(requireActivity()).get(SerieViewModel.class);
        viewModel.serieSeleccionada.observe(getViewLifecycleOwner(),serie -> {
            if (serie != null) {
                binding.sTituloDetalle.setText(serie.getTitulo());
                binding.sDescripcionDetalle.setText(serie.getOverwiew());
                binding.sEstreno.setText("Estreno: " + serie.estreno());


                Glide.with(this).load(serie.getPoster_path()).into(binding.sImagenDetalle);


                binding.btnTrailer.setOnClickListener(v -> {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(serie.getVideo_key()));
                    startActivity(intent);
                });
            } else {

                Toast.makeText(requireContext(), "No se pudo cargar el detalle de la serie", Toast.LENGTH_SHORT).show();
                requireActivity().onBackPressed();
            }
        });
    }
}