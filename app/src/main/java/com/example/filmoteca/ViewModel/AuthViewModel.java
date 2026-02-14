package com.example.filmoteca.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.filmoteca.AuthState;
import com.example.filmoteca.Repository.AuthRepository;
import com.google.firebase.auth.FirebaseUser;

public class AuthViewModel extends AndroidViewModel {
    private final AuthRepository repo;

    // LiveData para los métodos asíncronos del repositorio
    private final MutableLiveData<AuthState> authState = new MutableLiveData<>();

    public AuthViewModel(@NonNull Application application) {
        super(application);
        repo = new AuthRepository();
    }

    // Getter público del LiveData
    public MutableLiveData<AuthState> getAuthState() {
        return authState;
    }

    // No es una petición asíncrona ni un cambio de estado, es una consulta directa y síncrona.
    // Podemos devolverlo sin necesidad de un LiveData
    public FirebaseUser getCurrentUser() {
        return repo.getCurrentUser();
    }

    // Método para realizar el logout
    public void logout() {
        repo.logout();
    }

    // Método para realizar el login
    public void login(String email, String password) {
        // Primera validación
        String error = validate(email, password);
        if (error != null) {
            // Si no pasa la validación, devolvemos mensaje de error
            authState.setValue(AuthState.error(error));
            return;
        }

        // Anunciamos que estamos lanzando la petición (puede tardar así que podemos
        // mostrar un progress bar)
        authState.setValue(AuthState.loading());

        // Lanzamos la petición a Firebase
        repo.login(email.trim(), password, new AuthRepository.AuthCallback() {
            @Override public void onSuccess(FirebaseUser user) {
                // Si va bien, devolvemos la información del usuario
                authState.postValue(AuthState.success(user));
            }
            @Override public void onError(String message) {
                // Si va mal, devolvemos el mensaje de error
                authState.postValue(AuthState.error(message));
            }
        });
    }

    // Método de registro, lógica similar al login
    public void register(String email, String password) {
        String error = validate(email, password);
        if (error != null) {
            authState.setValue(AuthState.error(error));
            return;
        }

        authState.setValue(AuthState.loading());
        repo.register(email.trim(), password, new AuthRepository.AuthCallback() {
            @Override public void onSuccess(FirebaseUser user) {
                authState.postValue(AuthState.success(user));
            }
            @Override public void onError(String message) {
                authState.postValue(AuthState.error(message));
            }
        });
    }


    // Validación básica de los campos que introduce el usuario
    // Recomendable antes de enviar petición a Firebase
    private String validate(String email, String password) {
        if (email == null || email.trim().isEmpty()) return "El correo es obligatorio.";
        if (password == null || password.isEmpty()) return "La contraseña es obligatoria.";
        return null;
    }
}
