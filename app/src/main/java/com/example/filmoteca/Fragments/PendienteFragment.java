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
import com.example.filmoteca.ViewModel.AuthViewModel;
import com.example.filmoteca.ViewModel.MediaViewModel;
import com.example.filmoteca.ViewModel.MovieViewModel;
import com.example.filmoteca.databinding.FragmentPendienteBinding;
import com.google.firebase.auth.FirebaseUser;


public class PendienteFragment extends Fragment {

    FragmentPendienteBinding binding;
    private MediaViewModel viewModel;
    private MediaAdapter adapter;
    private AuthViewModel authViewModel;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return (binding = FragmentPendienteBinding.inflate(inflater, container, false)).getRoot();
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);
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
        FirebaseUser user = authViewModel.getCurrentUser();
        if (user != null) {
            viewModel.obtenerMedia(user.getUid()).observe(getViewLifecycleOwner(), listaMedia -> {
                if (listaMedia != null) {
                    adapter.addMediaList(listaMedia);
                }
            });
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}