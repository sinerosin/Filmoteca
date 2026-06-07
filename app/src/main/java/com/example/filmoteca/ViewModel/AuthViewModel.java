package com.example.filmoteca.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.filmoteca.AuthState;
import com.example.filmoteca.Repository.AuthRepository;
import com.google.firebase.auth.FirebaseUser;

public class AuthViewModel extends AndroidViewModel {
    private final AuthRepository repo;
    private final MutableLiveData<AuthState> authState = new MutableLiveData<>();

    public AuthViewModel(@NonNull Application application) {
        super(application);
        repo = new AuthRepository();
    }

    public MutableLiveData<AuthState> getAuthState() {
        return authState;
    }

    public FirebaseUser getCurrentUser() {
        return repo.getCurrentUser();
    }

    public void logout() {
        repo.logout();
    }

    public void login(String email, String password) {
        String error = validate(email, password);
        if (error != null) {
            authState.setValue(AuthState.error(error));
            return;
        }

        authState.setValue(AuthState.loading());

        repo.login(email.trim(), password, new AuthRepository.AuthCallback() {
            @Override public void onSuccess(FirebaseUser user) {
                authState.postValue(AuthState.success(user));
            }
            @Override public void onError(String message) {
                authState.postValue(AuthState.error(message));
            }
        });
    }
    public void register(String name, String email, String password) {
        String error = validate(email, password);
        if (error != null) {
            authState.setValue(AuthState.error(error));
            return;
        }
        if (name == null || name.trim().isEmpty()) {
            authState.setValue(AuthState.error("El nombre es obligatorio."));
            return;
        }

        authState.setValue(AuthState.loading());
        repo.register(name.trim(), email.trim(), password, new AuthRepository.AuthCallback() {
            @Override public void onSuccess(FirebaseUser user) {
                authState.postValue(AuthState.success(user));
            }
            @Override public void onError(String message) {
                authState.postValue(AuthState.error(message));
            }
        });
    }

    private String validate(String email, String password) {
        if (email == null || email.trim().isEmpty()) return "El correo es obligatorio.";
        if (password == null || password.isEmpty()) return "La contraseña es obligatoria.";
        return null;
    }

    public void loginWithGoogle(String idToken) {
        if (idToken == null || idToken.trim().isEmpty()) {
            authState.setValue(AuthState.error("No se pudo obtener el token de Google."));
            return;
        }

        authState.setValue(AuthState.loading());

        repo.loginWithGoogle(idToken, new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess(FirebaseUser user) {
                authState.postValue(AuthState.success(user));
            }

            @Override
            public void onError(String message) {
                authState.postValue(AuthState.error(message));
            }
        });
    }
    public LiveData<String> obtenerNombreUsuario(String uid) {
        return repo.obtenerNombreUsuarioDesdeFirestore(uid);
    }

    public void actualizarNombreUsuario(String uid, String nuevoNombre) {
        repo.actualizarNombreEnFirestore(uid, nuevoNombre);
    }
    public void recuperarContrasena(String email, AuthRepository.AuthCallback callback) {
        if (email == null || email.trim().isEmpty()) {
            callback.onError("Por favor, introduce tu correo electrónico.");
            return;
        }
        com.google.firebase.auth.FirebaseAuth.getInstance().sendPasswordResetEmail(email.trim())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callback.onSuccess(null);
                    } else {
                        callback.onError(task.getException() != null ? task.getException().getMessage() : "Error al enviar el correo.");
                    }
                });
    }
}