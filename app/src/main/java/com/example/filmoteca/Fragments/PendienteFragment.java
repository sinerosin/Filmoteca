package com.example.filmoteca.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.filmoteca.Adapter.MediaAdapter;
import com.example.filmoteca.Model.Media;
import com.example.filmoteca.R;
import com.example.filmoteca.ViewModel.AuthViewModel;
import com.example.filmoteca.ViewModel.MediaViewModel;
import com.example.filmoteca.databinding.FragmentPendienteBinding;
import com.google.firebase.auth.FirebaseUser;

public class PendienteFragment extends Fragment {

    FragmentPendienteBinding binding;
    private MediaViewModel viewModel;
    private MediaAdapter adapter;
    private AuthViewModel authViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
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
        String userUid = "";
        if (authViewModel.getCurrentUser() != null) {
            userUid = authViewModel.getCurrentUser().getUid();
        }

        adapter = new MediaAdapter(requireContext(), viewModel, userUid);
        binding.recyclerViewPendiente.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewPendiente.setAdapter(adapter);

        ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView rv, @NonNull RecyclerView.ViewHolder vh, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Media media = adapter.getMediaAt(position);

                viewModel.eliminarMedia(media);

                new AlertDialog.Builder(requireContext())
                        .setTitle("Eliminado de Pendientes")
                        .setMessage('"' + media.getTitle() + '"' + " se ha quitado de tu lista de pendientes.")
                        .setPositiveButton("Aceptar", null)
                        .show();
            }
        });
        helper.attachToRecyclerView(binding.recyclerViewPendiente);
    }

    private void observarMedia() {
        FirebaseUser user = authViewModel.getCurrentUser();
        if (user != null) {
            viewModel.obtenerMedia(user.getUid()).observe(getViewLifecycleOwner(), listaMedia -> {
                if (listaMedia != null) {
                    adapter.establecerLista(listaMedia);
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