package com.example.filmoteca.Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;


public class Movie {
    private int id;

    private String title;
    @SerializedName("overview")
    private String overwiew;
    private String poster_path;
    private String release_date;
    private String video_key;

    public void setVideoKey(String video_key){
        this.video_key=video_key;

    }
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
    public static class MovieVideo{
        private String key;
        private String site;
        private String type;

        public String getKey() { return key; }
        public String getSite() { return site; }
        public String getType() { return type; }

    }
    public  static class VideoResponse {
        private List<Movie.MovieVideo> results;
        public List<Movie.MovieVideo> getResults() { return results; }
    }
}
