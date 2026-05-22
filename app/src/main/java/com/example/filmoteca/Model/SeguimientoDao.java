package com.example.filmoteca.Model;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface SeguimientoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertarSeguimiento(Seguimiento seguimiento);

    @Query("SELECT * FROM seguimiento WHERE user = :user ORDER BY idInternal DESC")
    LiveData<List<Seguimiento>> obtenerSeguimientos(String user);

    @Delete
    void eliminarSeguimiento(Seguimiento seguimiento);
}