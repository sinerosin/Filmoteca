package com.example.filmoteca;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.filmoteca.Repository.AuthRepository;
import com.example.filmoteca.ViewModel.AuthViewModel;
import com.example.filmoteca.databinding.FragmentLoginBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

public class LoginFragment extends Fragment {

    private FragmentLoginBinding binding;
    private AuthViewModel viewModel;
    private GoogleSignInClient googleClient;
    private ActivityResultLauncher<Intent> googleLauncher;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configurarGoogleSignIn();
        inicializarLauncherGoogleSignIn();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);

        observeAuthState();

        binding.btnLogin.setOnClickListener(v -> {
            String email = binding.etEmail.getText().toString();
            String pass = binding.etPassword.getText().toString();
            viewModel.login(email, pass);
        });

        binding.googleSignInButton.setOnClickListener(v -> {
            Intent signInIntent = googleClient.getSignInIntent();
            googleLauncher.launch(signInIntent);
        });

        binding.tvRegister.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.registerFragment));

        binding.tvForgotPassword.setOnClickListener(v -> mostrarDialogoRecuperacion());
    }

    private void mostrarDialogoRecuperacion() {
        EditText inputEmail = new EditText(requireContext());
        inputEmail.setHint("correo@ejemplo.com");

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        inputEmail.setLayoutParams(lp);

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setTitle("Restablecer Contraseña")
                .setMessage("Introduce el correo electrónico asociado a tu cuenta para recibir un enlace de recuperación.")
                .setView(inputEmail)
                .setPositiveButton("Enviar", (dialogInterface, i) -> {
                    String email = inputEmail.getText().toString().trim();
                    viewModel.recuperarContrasena(email, new AuthRepository.AuthCallback() {
                        @Override
                        public void onSuccess(com.google.firebase.auth.FirebaseUser user) {
                            new AlertDialog.Builder(requireContext())
                                    .setTitle("Correo Enviado")
                                    .setMessage("Se ha enviado un enlace de recuperación a tu bandeja de entrada.")
                                    .setPositiveButton("Aceptar", null)
                                    .show();
                        }

                        @Override
                        public void onError(String message) {
                            new AlertDialog.Builder(requireContext())
                                    .setTitle("Error")
                                    .setMessage(message)
                                    .setPositiveButton("Aceptar", null)
                                    .show();
                        }
                    });
                })
                .setNegativeButton("Cancelar", null)
                .create();

        dialog.show();
    }

    private void configurarGoogleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(getString(R.string.default_web_client_id))
                .build();
        googleClient = GoogleSignIn.getClient(requireActivity(), gso);
    }

    private void inicializarLauncherGoogleSignIn() {
        googleLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                        gestionarResultadoSignIn(task);
                    }
                }
        );
    }

    private void gestionarResultadoSignIn(Task<GoogleSignInAccount> task) {
        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
            if (account != null && account.getIdToken() != null) {
                viewModel.loginWithGoogle(account.getIdToken());
            }
        } catch (ApiException e) {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Error de Autenticación")
                    .setMessage("No se pudo conectar con los servicios de Google: " + e.getMessage())
                    .setPositiveButton("Aceptar", null)
                    .show();
        }
    }

    private void observeAuthState() {
        viewModel.getAuthState().observe(getViewLifecycleOwner(), state -> {
            if (state == null) return;
            binding.btnLogin.setEnabled(!state.loading);
            binding.googleSignInButton.setEnabled(!state.loading);
            if (state.user != null) ((LoginActivity) requireActivity()).goToMain();
            if (state.error != null) {
                new AlertDialog.Builder(requireContext())
                        .setTitle("Error de Acceso")
                        .setMessage(state.error)
                        .setPositiveButton("Aceptar", null)
                        .show();
            }
        });
    }
}