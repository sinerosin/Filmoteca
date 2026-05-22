package com.example.filmoteca.Model;

import android.provider.MediaStore;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Serie  {

    private int id;
    private String name;
    @SerializedName("overview")
    private String overwiew;
    private String poster_path;

    private String first_air_date ;
    private String serieVideo;
    public Serie(){}
    public Serie(int id,String name,String overwiew,String poster_path,String first_air_date){
        this.id=id;
        this.name=name;
        this.overwiew=overwiew;
        this.poster_path=poster_path;
        this.first_air_date=first_air_date;
    }
    public void setVideoKey(String serieVideo){
        this.serieVideo=serieVideo;

    }


    public Serie(String name){
        this.name=name;
    }
    public int getId() {
        return id;
    }
    public String getName() {
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
        return "https://www.youtube.com/watch?v="+serieVideo;
    }
    public  static class serieVideo{
        private String key;
        private String site;
        private String type;

        public String getKey() { return key; }
        public String getSite() { return site; }
        public String getType() { return type; }

    }
    public  static class VideoResponse {
        private List<serieVideo> results;
        public List<serieVideo> getResults() { return results; }
    }


}

