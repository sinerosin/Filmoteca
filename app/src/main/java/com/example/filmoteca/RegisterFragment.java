package com.example.filmoteca;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.filmoteca.ViewModel.AuthViewModel;
import com.example.filmoteca.databinding.FragmentRegisterBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

public class RegisterFragment extends Fragment {

    private FragmentRegisterBinding binding;
    private AuthViewModel viewModel;
    private GoogleSignInClient googleClient;
    private ActivityResultLauncher<Intent> googleLauncher;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configurarGoogleSignIn();
        inicializarLauncherGoogle();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentRegisterBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);
        observarEstado();
        configurarBotones();
    }

    private void observarEstado() {
        viewModel.getAuthState().observe(getViewLifecycleOwner(), state -> {
            if (state == null) return;

            actualizarUI(state.loading);

            if (state.error != null) {
                new AlertDialog.Builder(requireContext())
                        .setTitle("Error de Registro")
                        .setMessage(state.error)
                        .setPositiveButton("Aceptar", null)
                        .show();
            }

            if (state.user != null) {
                ((LoginActivity) requireActivity()).goToMain();
            }
        });
    }

    private void configurarBotones() {
        binding.btnRegister.setOnClickListener(v -> {
            String name = binding.etName.getText().toString().trim();
            String email = binding.etEmail.getText().toString().trim();
            String pass = binding.etPassword.getText().toString().trim();
            String confirmPass = binding.etConfirm.getText().toString().trim();

            if (validarCampos(name, email, pass, confirmPass)) {
                viewModel.register(name, email, pass);
            }
        });

        binding.googleSignInButton.setOnClickListener(v -> {
            Intent intent = googleClient.getSignInIntent();
            googleLauncher.launch(intent);
        });

        binding.btnBack.setOnClickListener(v -> Navigation.findNavController(v).popBackStack());
        binding.tvLoginLink.setOnClickListener(v -> Navigation.findNavController(v).popBackStack());
    }
    private boolean validarCampos(String name, String email, String pass, String confirm) {
        if (name.isEmpty() || email.isEmpty() || pass.isEmpty()) {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Campos Incompletos")
                    .setMessage("Por favor, rellena todos los campos obligatorios.")
                    .setPositiveButton("Aceptar", null)
                    .show();
            return false;
        }

        String regexContrasena = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}$";

        if (!pass.matches(regexContrasena)) {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Contraseña No Válida")
                    .setMessage("La contraseña debe cumplir las siguientes políticas de seguridad:\n\n" +
                            "• Mínimo 8 caracteres de longitud.\n" +
                            "• Al menos una letra mayúscula.\n" +
                            "• Al menos una letra minúscula.\n" +
                            "• Al menos un dígito numérico.")
                    .setPositiveButton("Aceptar", null)
                    .show();
            return false;
        }

        if (!pass.equals(confirm)) {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Error de Validación")
                    .setMessage("Las contraseñas introducidas no coinciden.")
                    .setPositiveButton("Aceptar", null)
                    .show();
            return false;
        }
        return true;
    }

    private void actualizarUI(boolean cargando) {
        binding.btnRegister.setEnabled(!cargando);
        binding.googleSignInButton.setEnabled(!cargando);
    }

    private void configurarGoogleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(getString(R.string.default_web_client_id))
                .build();
        googleClient = GoogleSignIn.getClient(requireActivity(), gso);
    }

    private void inicializarLauncherGoogle() {
        googleLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                        try {
                            GoogleSignInAccount account = task.getResult(ApiException.class);
                            if (account != null) {
                                viewModel.loginWithGoogle(account.getIdToken());
                            }
                        } catch (ApiException e) {
                            new AlertDialog.Builder(requireContext())
                                    .setTitle("Error Google")
                                    .setMessage("No se pudo conectar con los servicios de Google: " + e.getMessage())
                                    .setPositiveButton("Aceptar", null)
                                    .show();
                        }
                    }
                }
        );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}