package com.example.filmoteca.Response;

import com.example.filmoteca.Model.Movie;
import com.example.filmoteca.Model.Serie;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MovieResponse {
    @SerializedName("results")
    private List<Movie> results;

    public List<Movie> getResults() {
        return results;
    }
}
