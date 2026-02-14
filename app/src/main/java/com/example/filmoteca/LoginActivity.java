package com.example.filmoteca;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.filmoteca.ViewModel.AuthViewModel;
import com.example.filmoteca.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

    private AuthViewModel viewModel;
    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((binding = ActivityLoginBinding.inflate(getLayoutInflater())).getRoot());

        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        // Si ya hay usuario autenticado, saltamos directamente a la pantalla principal
        if (viewModel.getCurrentUser() != null) {
            goToMain();
            return;
        }

        // Inicialización de componentes
        observeAuthState();
        inicializarBotones();
    }

    private void observeAuthState() {
        viewModel.getAuthState().observe(this, state -> {
            if (state == null) return;

            // Deshabilita botones y muestra la progress bar si la petición está en proceso
            habilitarInterfaz(state.loading);

            // Si hay un error, mostramos el mensaje
            if (state.error != null) {
                Toast.makeText(this, state.error, Toast.LENGTH_LONG).show();
                return;
            }

            // Si todo va bien, pasamos a la pantalla principal
            if (state.user != null) {
                goToMain();
            }
        });
    }

    private void habilitarInterfaz(boolean cargando) {
        // Si está cargando, deshabilitamos todos los botones y mostramos la barra de progreso
        if (cargando) {
            binding.registerButton.setEnabled(false);
            binding.loginButton.setEnabled(false);
            binding.emailEditText.setEnabled(false);
            binding.passwordEditText.setEnabled(false);
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.registerButton.setEnabled(true);
            binding.loginButton.setEnabled(true);
            binding.emailEditText.setEnabled(true);
            binding.passwordEditText.setEnabled(true);
            binding.progressBar.setVisibility(View.GONE);
        }
    }

    private void inicializarBotones() {
        binding.registerButton.setOnClickListener(v -> {
            // Recuperamos mail y contraseña
            String email = binding.emailEditText.getText().toString();
            String pass = binding.passwordEditText.getText().toString();
            // Iniciamos el proceso de registro
            viewModel.register(email, pass);
        });

        binding.loginButton.setOnClickListener(v -> {
            // Recuperamos mail y contraseña
            String email = binding.emailEditText.getText().toString();
            String pass = binding.passwordEditText.getText().toString();
            // Iniciamos el proceso de login
            viewModel.login(email, pass);
        });
    }

    // Método que navega a la pantalla principal
    private void goToMain() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}