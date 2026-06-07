package com.example.filmoteca.Model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Actor implements Serializable {

    @SerializedName("id")
    private String id;
    @SerializedName("name")
    private String nombre;
    @SerializedName("profile_path")
    private String fotoUrl;

    public Actor() {
    }

    public Actor(String id, String nombre, String fotoUrl) {
        this.id = id;
        this.nombre = nombre;
        this.fotoUrl = fotoUrl;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getFotoUrl() {
        if (fotoUrl == null || fotoUrl.isEmpty() || fotoUrl.equals("null")) {
            return "";
        }
        if (!fotoUrl.startsWith("http")) {
            return "https://image.tmdb.org/t/p/w200" + fotoUrl;
        }
        return fotoUrl;
    }

    public void setFotoUrl(String fotoUrl) {
        this.fotoUrl = fotoUrl;
    }
}