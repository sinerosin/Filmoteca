package com.example.filmoteca.Model;

import com.google.gson.annotations.SerializedName;

public class Movie {
    private int id;

    private String title;
    @SerializedName("overview")
    private String overwiew;
    private String poster_path;

    public int getId() {
        return id;
    }
    public String getTitulo() {
        return title;
    }

    public String getOverwiew() {
        return overwiew;
    }

    public String getPoster_path() {
        return "https://image.tmdb.org/t/p/w500"+poster_path;
    }
}
