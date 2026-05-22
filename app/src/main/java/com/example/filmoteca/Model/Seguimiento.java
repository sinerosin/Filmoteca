package com.example.filmoteca.Model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.io.Serializable;

@Entity(tableName = "seguimiento")
public class Seguimiento implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int idInternal;
    private String user;
    private String titulo;
    private String tipo;
    private String fechaVisualizacion;
    private float puntuacion;
    private String imagenRecuerdo;
    private String posterPath;

    public Seguimiento(String titulo, String tipo, String fechaVisualizacion,
                       float puntuacion, String imagenRecuerdo,
                       String posterPath,String user ) {
        this.titulo = titulo;
        this.tipo = tipo;
        this.fechaVisualizacion = fechaVisualizacion;
        this.puntuacion = puntuacion;
        this.imagenRecuerdo = imagenRecuerdo;
        this.posterPath = posterPath;
        this.user=user;
    }

    public int getIdInternal() { return idInternal; }
    public void setIdInternal(int idInternal) { this.idInternal = idInternal; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getFechaVisualizacion() { return fechaVisualizacion; }
    public void setFechaVisualizacion(String fechaVisualizacion) { this.fechaVisualizacion = fechaVisualizacion; }

    public float getPuntuacion() { return puntuacion; }
    public void setPuntuacion(float puntuacion) { this.puntuacion = puntuacion; }

    public String getImagenRecuerdo() { return imagenRecuerdo; }
    public void setImagenRecuerdoUri(String imagenRecuerdo) { this.imagenRecuerdo = imagenRecuerdo; }

    public String getPosterPath() { return posterPath; }
    public void setPosterPath(String posterPath) { this.posterPath = posterPath; }
    public String getUser() {
        return user;
    }
    public void setUser(String user) {
        this.user = user;
    }

}