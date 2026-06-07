package com.example.filmoteca.Response;

import com.example.filmoteca.Model.Actor;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ActorResponse {
    @SerializedName("cast")
    private List<Actor> results;

    public List<Actor> getResults() {
        return results;
    }
}
