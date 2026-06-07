package com.example.filmoteca.Response;

import com.example.filmoteca.Model.Movie;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class MovieCreditResponse {
    @SerializedName("cast")
    private List<Movie> results;

    public List<Movie> getResults() { return results; }
}