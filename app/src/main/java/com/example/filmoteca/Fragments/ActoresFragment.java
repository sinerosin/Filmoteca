package com.example.filmoteca.Fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.filmoteca.Adapter.ActorAdapter;
import com.example.filmoteca.Model.Actor;
import com.example.filmoteca.R;
import com.example.filmoteca.ViewModel.AuthViewModel;
import com.example.filmoteca.databinding.FragmentActoresBinding;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ActoresFragment extends Fragment {

    private FragmentActoresBinding binding;
    private ActorAdapter adapter;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private AuthViewModel authViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentActoresBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);

        configurarRecyclerView();
        cargarFav();
    }

    private void configurarRecyclerView() {
        adapter = new ActorAdapter(new ActorAdapter.OnActorClickListener() {
            @Override
            public void onFavToggle(Actor actor, boolean esFavorito) {
                if (authViewModel.getCurrentUser() == null) return;
                String uid = authViewModel.getCurrentUser().getUid();

                db.collection("usuarios").document(uid)
                        .collection("actores_favoritos").document(actor.getId()).delete();
                new AlertDialog.Builder(requireContext())
                        .setTitle("Actor Eliminado")
                        .setMessage(actor.getNombre() + " ha sido eliminado de tus favoritos.")
                        .setPositiveButton("Aceptar", null)
                        .show();
            }

            @Override
            public void onActorClick(Actor actor) {
                Bundle b = new Bundle();
                b.putSerializable("actor", actor);
                Navigation.findNavController(requireView())
                        .navigate(R.id.detalleActorFragment, b);
            }
        });

        binding.rvActoresFavoritos.setLayoutManager(new GridLayoutManager(getContext(), 3));
        binding.rvActoresFavoritos.setAdapter(adapter);
    }

    private void cargarFav() {
        if (authViewModel.getCurrentUser() == null) return;
        String uid = authViewModel.getCurrentUser().getUid();

        db.collection("usuarios").document(uid).collection("actores_favoritos")
                .addSnapshotListener((value, error) -> {
                    if (error != null) return;

                    if (value != null) {
                        List<Actor> favoritos = new ArrayList<>();
                        List<String> ids = new ArrayList<>();

                        for (QueryDocumentSnapshot doc : value) {
                            Actor actor = doc.toObject(Actor.class);
                            favoritos.add(actor);
                            ids.add(actor.getId());
                        }

                        adapter.establecerActores(favoritos);
                        adapter.establecerFavoritos(ids);
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}