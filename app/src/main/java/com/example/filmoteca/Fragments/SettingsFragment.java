package com.example.filmoteca.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.filmoteca.LoginActivity;
import com.example.filmoteca.R;
import com.example.filmoteca.ViewModel.AuthViewModel;
import com.example.filmoteca.databinding.FragmentSettingsBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class SettingsFragment extends Fragment {
    private FragmentSettingsBinding binding;
    private SharedPreferences sharedPreferences;
    private GoogleSignInClient googleClient;
    private AuthViewModel viewModel;
    private FirebaseUser currentUser;

    private static final String[] idiomas = {"Español (España)", "English", "Français", "Deutsch"};

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleClient = GoogleSignIn.getClient(requireActivity(), gso);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);
        currentUser = viewModel.getCurrentUser();

        if (currentUser != null) {
            String prefsName = "Ajustes_" + currentUser.getUid();
            sharedPreferences = requireActivity().getSharedPreferences(prefsName, Context.MODE_PRIVATE);

            configurarSpinnerIdioma();
            cargarPreferencias();

            binding.toggleGroupTema.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
                if (isChecked) {
                    group.check(checkedId);
                }
            });

            binding.btnGuardarAjustes.setOnClickListener(v -> guardarPreferencias());
            binding.btnResetear.setOnClickListener(v -> resetearPreferencias());
            binding.btnLogout.setOnClickListener(v -> cerrarSesion());
            binding.btnBackSettings.setOnClickListener(v -> Navigation.findNavController(v).popBackStack());
        }
    }

    private void configurarSpinnerIdioma() {
        binding.spinnerIdioma.setSimpleItems(idiomas);
    }

    private void cargarPreferencias() {
        viewModel.obtenerNombreUsuario(currentUser.getUid()).observe(getViewLifecycleOwner(), nombreRemoto -> {
            if (binding != null && nombreRemoto != null) {
                binding.etUsername.setText(nombreRemoto);
            }
        });

        binding.spinnerIdioma.setText(sharedPreferences.getString("idioma_pref_nombre", "Español (España)"), false);
        binding.switchWifi.setChecked(sharedPreferences.getBoolean("solo_wifi", false));

        boolean esOscuro = sharedPreferences.getBoolean("tema_oscuro", false);
        if (esOscuro) {
            binding.toggleGroupTema.check(R.id.btnTemaOscuro);
        } else {
            binding.toggleGroupTema.check(R.id.btnTemaClaro);
        }
    }

    private void guardarPreferencias() {
        String nombre = binding.etUsername.getText().toString().trim();
        if (nombre.isEmpty()) {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Campo obligatorio")
                    .setMessage("El nombre de usuario no puede estar vacío.")
                    .setPositiveButton("Aceptar", null)
                    .show();
            return;
        }

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(nombre)
                .build();

        currentUser.updateProfile(profileUpdates).addOnCompleteListener(task -> {
            if (task.isSuccessful() && binding != null) {

                viewModel.actualizarNombreUsuario(currentUser.getUid(), nombre);

                SharedPreferences.Editor editor = sharedPreferences.edit();
                String idiomaSeleccionado = binding.spinnerIdioma.getText().toString();

                String codigoIdioma = "es-ES";
                if (idiomaSeleccionado.equals("English")) codigoIdioma = "en-US";
                else if (idiomaSeleccionado.equals("Français")) codigoIdioma = "fr-FR";
                else if (idiomaSeleccionado.equals("Deutsch")) codigoIdioma = "de-DE";

                editor.putString("idioma_pref_nombre", idiomaSeleccionado);
                editor.putString("idioma_pref_codigo", codigoIdioma);
                editor.putBoolean("solo_wifi", binding.switchWifi.isChecked());

                boolean temaOscuro = binding.toggleGroupTema.getCheckedButtonId() == R.id.btnTemaOscuro;
                editor.putBoolean("tema_oscuro", temaOscuro);
                editor.commit();

                AppCompatDelegate.setDefaultNightMode(temaOscuro ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);

                new AlertDialog.Builder(requireContext())
                        .setTitle("Ajustes Guardados")
                        .setMessage("El nombre de usuario y tus preferencias se han sincronizado en Firebase correctamente.")
                        .setPositiveButton("Aceptar", null)
                        .show();
            } else if (binding != null) {
                new AlertDialog.Builder(requireContext())
                        .setTitle("Error")
                        .setMessage("No se pudo actualizar el nombre en el servidor de autenticación.")
                        .setPositiveButton("Aceptar", null)
                        .show();
            }
        });
    }

    private void resetearPreferencias() {
        sharedPreferences.edit().clear().commit();
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        cargarPreferencias();

        new AlertDialog.Builder(requireContext())
                .setTitle("Restablecer Ajustes")
                .setMessage("Las preferencias locales se han restablecido a los valores por defecto.")
                .setPositiveButton("Aceptar", null)
                .show();
    }

    private void cerrarSesion() {
        viewModel.logout();
        googleClient.signOut().addOnCompleteListener(task -> goToLogin());
    }

    private void goToLogin() {
        Intent intent = new Intent(requireActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}