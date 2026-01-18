package com.example.filmoteca.Model;

import java.io.Serializable;

public class Serie  {
    private int id;
    private String name;
    private String overwiew;
    private String poster_path;

    public Serie(String name){
        this.name=name;
    }
    public int getId() {
        return id;
    }
    public String getTitulo() {
        return name;
    }

    public String getOverwiew() {
        return overwiew;
    }

    public String getPoster_path() {
        return "https://image.tmdb.org/t/p/w500"+poster_path;
    }
}
