package com.example.filmoteca.Repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AuthRepository {
    private final FirebaseAuth auth;

    private final FirebaseFirestore db;

    public AuthRepository() {
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    public interface AuthCallback {
        void onSuccess(FirebaseUser user);
        void onError(String message);
    }

    public void login(String email, String password, AuthCallback callback) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(result -> callback.onSuccess(auth.getCurrentUser()))
                .addOnFailureListener(e -> callback.onError(mapError(e)));
    }

    public void loginWithGoogle(String idToken, AuthCallback callback) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential)
                .addOnSuccessListener(result -> {
                    FirebaseUser user = auth.getCurrentUser();
                    if (user != null) {
                        guardarDatosUsuarioEnFirestore(user.getUid(), user.getDisplayName(), user.getEmail());
                    }
                    callback.onSuccess(user);
                })
                .addOnFailureListener(e -> callback.onError(mapError(e)));
    }
    public void register(String name, String email, String password, AuthCallback callback) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(result -> {
                    FirebaseUser user = auth.getCurrentUser();
                    if (user != null) {
                        // Guardamos el nombre en la colección "usuarios" antes de terminar
                        db.collection("usuarios")
                                .document(user.getUid())
                                .set(crearMapaUsuario(name, email))
                                .addOnSuccessListener(aVoid -> callback.onSuccess(user))
                                .addOnFailureListener(e -> callback.onError("Usuario creado, pero falló al guardar el nombre: " + mapError(e)));
                    } else {
                        callback.onSuccess(null);
                    }
                })
                .addOnFailureListener(e -> callback.onError(mapError(e)));
    }

    public FirebaseUser getCurrentUser() {
        return auth.getCurrentUser();
    }

    public void logout() {
        auth.signOut();
    }

    public LiveData<String> obtenerNombreUsuarioDesdeFirestore(String uid) {
        MutableLiveData<String> nombreLiveData = new MutableLiveData<>();

        db.collection("usuarios")
                .document(uid)
                .addSnapshotListener((documentSnapshot, error) -> {
                    if (error != null) {
                        nombreLiveData.setValue("");
                        return;
                    }
                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        String nombre = documentSnapshot.getString("name");
                        nombreLiveData.setValue(nombre != null ? nombre : "");
                    }
                });

        return nombreLiveData;
    }
    public void actualizarNombreEnFirestore(String uid, String nuevoNombre) {
        if (uid == null || nuevoNombre == null) return;

        db.collection("usuarios")
                .document(uid)
                .update("name", nuevoNombre.trim());
    }

    private Map<String, Object> crearMapaUsuario(String name, String email) {
        Map<String, Object> usuario = new HashMap<>();
        usuario.put("name", name);
        usuario.put("email", email);
        return usuario;
    }

    private void guardarDatosUsuarioEnFirestore(String uid, String name, String email) {
        Map<String, Object> usuario = crearMapaUsuario(name != null ? name : "Usuario de Google", email);
        db.collection("usuarios").document(uid).set(usuario, com.google.firebase.firestore.SetOptions.merge());
    }

    private String mapError(Exception e) {
        if (e == null || e.getMessage() == null) return "Error desconocido.";
        return e.getMessage();
    }
}