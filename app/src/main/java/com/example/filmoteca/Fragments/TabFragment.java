package com.example.filmoteca.Fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.filmoteca.Model.Media;
import com.example.filmoteca.R;
import com.example.filmoteca.ViewModel.AuthViewModel;
import com.example.filmoteca.ViewModel.MediaViewModel;
import com.example.filmoteca.databinding.FragmentTabBinding;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseUser;

import java.util.Random;

public class TabFragment extends Fragment {
    private FragmentTabBinding binding;
    private MediaViewModel mediaViewModel;
    private AuthViewModel authViewModel;

    private static boolean mostrada = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentTabBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mediaViewModel = new ViewModelProvider(requireActivity()).get(MediaViewModel.class);
        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);

        establecerAdaptadorViewPager();
        vincularTabLayoutConViewPager();
        bienvenida();
    }

    private void establecerAdaptadorViewPager() {
        binding.viewPager.setAdapter(new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                switch (position) {
                    default:
                    case 0: return new MovieFragment();
                    case 1: return new SeriesFragment();
                }
            }

            @Override
            public int getItemCount() {
                return 2;
            }
        });
    }

    private void vincularTabLayoutConViewPager() {
        new TabLayoutMediator(binding.tabLayout, binding.viewPager,
                (tab, position) -> {
                    switch (position) {
                        case 0:
                            tab.setText("Peliculas");
                            break;
                        case 1:
                            tab.setText("Series");
                            break;
                    }
                }).attach();
    }

    private void bienvenida() {
        if (mostrada) return;
        FirebaseUser user = authViewModel.getCurrentUser();
        if (user == null) return;

        mediaViewModel.obtenerMedia(user.getUid()).observe(getViewLifecycleOwner(), pendientes -> {
            if (mostrada) return;
            SharedPreferences prefs = requireActivity().getSharedPreferences("MisAjustes", Context.MODE_PRIVATE);
            String nombre = prefs.getString("nombre_usuario", "");
            if (nombre.isEmpty() && user.getDisplayName() != null) {
                nombre = user.getDisplayName();
            }
            if (nombre.isEmpty()) {
                nombre = "Usuario";
            }
            if (pendientes == null || pendientes.isEmpty()) {
                mostrada = true;
                mostrarDialogoSinPendiente(nombre);
            } else {
                mostrada = true;
                Media aleatorio = pendientes.get(new Random().nextInt(pendientes.size()));
                mostrarDialogoConPendientes(nombre, aleatorio);
            }
        });
    }

    private void mostrarDialogoSinPendiente(String nombre) {
        new AlertDialog.Builder(requireContext())
                .setTitle("¡Hola! " + nombre)
                .setMessage("Para personalizar tu experiencia, puedes configurar tu nombre en los ajustes. ¿Quieres hacerlo ahora?")
                .setPositiveButton("Ir a Ajustes", (dialog, which) -> {
                    Navigation.findNavController(requireView()).navigate(R.id.settingsFragment);
                })
                .setNegativeButton("Más tarde", null)
                .show();
    }

    private void mostrarDialogoConPendientes(String nombre, Media pendiente) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Hola, " + nombre)
                .setMessage("¿Has visto ya tu pendiente " + pendiente.getTitle() + "?")
                .setPositiveButton("Ir a Seguimiento", (dialog, which) -> {
                    Navigation.findNavController(requireView()).navigate(R.id.formFragment);
                })
                .setNegativeButton("Aún no la he visto", null)
                .show();
    }
}