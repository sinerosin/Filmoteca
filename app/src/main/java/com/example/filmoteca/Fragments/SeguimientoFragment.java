package com.example.filmoteca.Fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.filmoteca.Adapter.SeguimientoAdapter;
import com.example.filmoteca.R;
import com.example.filmoteca.ViewModel.MediaViewModel;
import com.example.filmoteca.databinding.FragmentSeguimientoBinding;

public class SeguimientoFragment extends Fragment {
    private NavController navController;
    private FragmentSeguimientoBinding binding;
    private MediaViewModel viewModel;
    private SeguimientoAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return (binding = FragmentSeguimientoBinding.inflate(inflater, container, false)).getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(MediaViewModel.class);
        navController = Navigation.findNavController(view);

        configurarRecyclerView();

        observarSeguimientos();

        binding.aAdirSeguimiento.setOnClickListener(v -> navController.navigate(R.id.formFragment));
    }

    private void configurarRecyclerView() {

        adapter = new SeguimientoAdapter(requireContext(),viewModel);

        binding.recyclerViewSeguimiento.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewSeguimiento.setAdapter(adapter);
    }

    private void observarSeguimientos() {

        viewModel.obtenerSeguimiento().observe(getViewLifecycleOwner(), listaSeguimiento -> {
            if (listaSeguimiento != null && !listaSeguimiento.isEmpty()) {

                adapter.addSeguimientoList(listaSeguimiento);


                binding.recyclerViewSeguimiento.setVisibility(View.VISIBLE);
            } else {
                binding.recyclerViewSeguimiento.setVisibility(View.GONE);
            }
        });
    }

}