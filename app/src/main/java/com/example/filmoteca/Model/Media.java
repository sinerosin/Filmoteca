package com.example.filmoteca.Model;

public class Media {

    private int id;
    private String user;
    private String title;
    private String overview;
    private String releaseDate;
    private String poster;
    private String videoKey;

    public Media() {
    }

    public Media(int id, String title, String overview, String releaseDate, String poster, String user, String videoKey) {
        this.id = id;
        this.title = title;
        this.overview = overview;
        this.releaseDate = releaseDate;
        this.poster = poster;
        this.user = user;
        this.videoKey = videoKey;
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

    public String getVideoKey() {
        return videoKey;
    }

    public void setVideoKey(String videoKey) {
        this.videoKey = videoKey;
    }
}