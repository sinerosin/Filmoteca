package com.example.filmoteca.Model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "media")
public class Media {
    @PrimaryKey
    private int id;
    private String user;
    private String title;
    private String overview;
    private String releaseDate;
    private String poster;

    public Media() {
    }

    public Media(int id, String title, String overview, String releaseDate, String poster, String user) {
        this.id = id;
        this.title = title;
        this.overview = overview;
        this.releaseDate = releaseDate;
        this.poster = poster;
        this.user = user;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public String getTitulo() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setTitulo(String title) {
        this.title = title;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getPoster() {
        return poster;
    }
    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}