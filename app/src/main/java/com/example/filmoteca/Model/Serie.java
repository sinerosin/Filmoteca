package com.example.filmoteca.Model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Serie  {
    private int id;
    private String name;
    @SerializedName("overview")
    private String overwiew;
    private String poster_path;

    private String first_air_date ;
    private String video_key;

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
    public String estreno() {
        return first_air_date;
    }
    public String getVideo_key() {
        return "https://www.youtube.com/watch?v="+video_key;
    }
}
