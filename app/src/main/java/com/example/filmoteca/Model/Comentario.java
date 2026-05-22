package com.example.filmoteca.Model;

import com.google.firebase.Timestamp;

public class Comentario {
    private String userName;
    private String mensaje;
    private Timestamp timestamp;

    public Comentario() {}

    public Comentario(String userName, String mensaje, Timestamp timestamp) {
        this.userName = userName;
        this.mensaje = mensaje;
        this.timestamp = timestamp;
    }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }

    public Timestamp getTimestamp() { return timestamp; }
    public void setTimestamp(Timestamp timestamp) { this.timestamp = timestamp; }
}