package com.example.filmoteca.Model;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface MediaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertarMedia(Media media);
    @Update
    void actualizar(Media media);

    @Delete
    void eliminar(Media media);

    @Query("SELECT * FROM media ")
    LiveData<List<Media>> obtenerTodos();
    @Query("SELECT * FROM media WHERE title = :nombre ")
    LiveData<Media> buscarPorNombre(String nombre);

}
