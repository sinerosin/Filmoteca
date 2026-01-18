package com.example.filmoteca.Response;

import com.example.filmoteca.Model.Serie;

import java.util.List;

public class SerieResponse {
    private List<SerieEntry> results;

    public List<SerieEntry> getResults() {
        return results;
    }
    public class SerieEntry {
        private int id;
        private String name;
        private String overview;
        private String poster_path;
        public int getId() {
            return id;
        }
        public String getName() {
            return name;
        }
        public String getOverview() {
            return overview;
        }
        public String getPoster_path() {
            return poster_path;
        }
    }




}
