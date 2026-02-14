package com.example.filmoteca.Repository;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AuthRepository {
    private final FirebaseAuth auth;

    public AuthRepository() {
        auth = FirebaseAuth.getInstance();
    }

    /**
     * Callback para comunicar el resultado de una operación de autenticación (login/registro).
     * Se utiliza porque las llamadas a Firebase son asíncronas.
     */
    public interface AuthCallback {
        // Se ejecuta cuando la operación ha sido exitosa. Devuelve la información del usuario autenticado.
        void onSuccess(FirebaseUser user);
        // Se ejecuta cuando la operación falla.
        void onError(String message);
    }

    // Inicia sesión con email y contraseña.
    // Notifica el resultado mediante AuthCallback
    public void login(String email, String password, AuthCallback callback) {
        // Operación asíncrona: Firebase devuelve un Task y se resuelve en los listeners.
        auth.signInWithEmailAndPassword(email, password)
                // Si se autentica correctamente, devolvemos el usuario actual.
                .addOnSuccessListener(result -> callback.onSuccess(auth.getCurrentUser()))
                // Si falla, mapeamos la excepción a un mensaje y lo devolvemos.
                .addOnFailureListener(e -> callback.onError(mapError(e)));
    }

    // Registra un nuevo usuario con email y contraseña.
    // Notifica el resultado mediante AuthCallback
    public void register(String email, String password, AuthCallback callback) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(result -> callback.onSuccess(auth.getCurrentUser()))
                .addOnFailureListener(e -> callback.onError(mapError(e)));
    }

    // Devuelve el usuario actualmente autenticado (si hay alguno).
    public FirebaseUser getCurrentUser() {
        return auth.getCurrentUser();
    }

    // Cierra sesión del usuario actual.
    public void logout() {
        auth.signOut();
    }

    // Convierte una excepción en un mensaje de error para mostrar.
    private String mapError(Exception e) {
        // Si no hay excepción o no hay mensaje, devolvemos un texto genérico.
        if (e == null || e.getMessage() == null) return "Error desconocido.";
        // Si no, devolvemos el mensaje original de Firebase.
        return e.getMessage();
    }
}
