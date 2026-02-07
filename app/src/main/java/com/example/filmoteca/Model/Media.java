package com.example.filmoteca.Model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "media")
public class Media {
    @PrimaryKey
    private int id;
    private String title;
    private String overview;
    private String releaseDate;
    private String poster;
    public Media(int id,String title, String overview, String releaseDate, String poster){
        this.id=id;
        this.title=title;
        this.overview=overview;
        this.releaseDate=releaseDate;
        this.poster=poster;
    }
    public int getId() {
        return id;
    }
    public String getTitulo() {
        return title;
    }
    public String getOverview() {
        return overview;
    }
    public String getTitle() {
        return title;
    }
    public String getReleaseDate() {
        return releaseDate;
    }
    public String getPoster() {
        return poster;
    }
    public void setId(int id) {
        this.id = id;
    }
    public void setTitulo(String title) {
        this.title = title;
    }
    public void setOverwiew(String overview) {
        this.overview = overview;
    }
    public void setEstreno(String releaseDate) {
        this.releaseDate = releaseDate;
    }
    public void setPoster_path(String poster) {
        this.poster = poster;
    }

}
