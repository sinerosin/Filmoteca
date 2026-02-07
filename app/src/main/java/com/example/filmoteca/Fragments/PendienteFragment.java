package com.example.filmoteca.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.filmoteca.Adapter.MediaAdapter;
import com.example.filmoteca.R;
import com.example.filmoteca.ViewModel.MediaViewModel;
import com.example.filmoteca.ViewModel.MovieViewModel;
import com.example.filmoteca.databinding.FragmentPendienteBinding;


public class PendienteFragment extends Fragment {

    FragmentPendienteBinding binding;
    private MediaViewModel viewModel;
    private MediaAdapter adapter;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return (binding = FragmentPendienteBinding.inflate(inflater, container, false)).getRoot();
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(MediaViewModel.class);
        configurarRecyclerView();
        observarMedia();
    }
    private void configurarRecyclerView() {
        adapter = new MediaAdapter(requireContext(),viewModel);


        binding.recyclerViewPendiente.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewPendiente.setAdapter(adapter);

    }

    private void observarMedia() {

        viewModel.obtenerMedia().observe(getViewLifecycleOwner(), listaMedia -> {
            if (listaMedia != null && !listaMedia.isEmpty()) {
                adapter.addMediaList(listaMedia);

            }
        });
    }



}