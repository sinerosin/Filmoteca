package com.example.filmoteca.Response;

import com.example.filmoteca.Model.Serie;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class SerieCreditResponse {
    @SerializedName("cast")
    private List<Serie> results;

    public List<Serie> getResults() { return results; }
}