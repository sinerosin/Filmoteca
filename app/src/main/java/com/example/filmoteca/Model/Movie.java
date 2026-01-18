package com.example.filmoteca.Model;

import com.google.gson.annotations.SerializedName;

public class Movie {
    private int id;

    private String title;
    @SerializedName("overview")
    private String overwiew;
    private String poster_path;
    private String release_date;
    private String video_key;


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
    public String getFecha() {
        return release_date;
    }
    public String getVideo_key() {
        return "https://www.youtube.com/watch?v="+video_key;
    }
}
