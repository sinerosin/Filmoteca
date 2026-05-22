package com.example.filmoteca.Fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.filmoteca.Adapter.SeguimientoAdapter;
import com.example.filmoteca.Model.Seguimiento;
import com.example.filmoteca.R;
import com.example.filmoteca.ViewModel.AuthViewModel;
import com.example.filmoteca.ViewModel.MediaViewModel;
import com.example.filmoteca.databinding.FragmentSeguimientoBinding;
import com.google.firebase.auth.FirebaseUser;

public class SeguimientoFragment extends Fragment {
    private NavController navController;
    private FragmentSeguimientoBinding binding;
    private MediaViewModel viewModel;
    private AuthViewModel authViewModel;
    private SeguimientoAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return (binding = FragmentSeguimientoBinding.inflate(inflater, container, false)).getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);
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
        ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView rv, @NonNull RecyclerView.ViewHolder vh, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Seguimiento s = adapter.getSeguimientoAt(position);
                viewModel.eliminarSeguimiento(s);
                Toast.makeText(getContext(), "Eliminado: " + s.getTitulo(), Toast.LENGTH_SHORT).show();
            }
        });
        helper.attachToRecyclerView(binding.recyclerViewSeguimiento);
    }

    private void observarSeguimientos() {
        FirebaseUser user = authViewModel.getCurrentUser();
        viewModel.obtenerSeguimiento(user.getUid()).observe(getViewLifecycleOwner(), listaSeguimiento -> {
            if (listaSeguimiento != null && !listaSeguimiento.isEmpty()) {

                adapter.addSeguimientoList(listaSeguimiento);


                binding.recyclerViewSeguimiento.setVisibility(View.VISIBLE);
            } else {
                binding.recyclerViewSeguimiento.setVisibility(View.GONE);
            }
        });
    }

}