package com.example.filmoteca.Model;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface SeguimientoDao {
    @Insert
    void insertarSeguimiento(Seguimiento seguimiento);

    @Query("SELECT * FROM seguimiento ORDER BY idInternal DESC")
    LiveData<List<Seguimiento>> obtenerSeguimientos();

    @Delete
    void eliminarSeguimiento(Seguimiento seguimiento);
}