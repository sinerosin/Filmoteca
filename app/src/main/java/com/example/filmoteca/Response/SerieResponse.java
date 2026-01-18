package com.example.filmoteca.Response;

import com.example.filmoteca.Model.Serie;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SerieResponse {
    @SerializedName("results")
    private List<Serie> results;

    public List<Serie> getResults() {
        return results;
    }

}
