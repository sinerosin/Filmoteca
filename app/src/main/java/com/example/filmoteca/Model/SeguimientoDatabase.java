package com.example.filmoteca.Model;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Seguimiento.class}, version = 2)
public abstract class SeguimientoDatabase extends RoomDatabase {
    public abstract SeguimientoDao seguimientoDao();
    private static SeguimientoDatabase instance;

    public static SeguimientoDatabase getInstance(final Context context) {
        if (instance == null) {
            synchronized (SeguimientoDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(context.getApplicationContext(),
                                    SeguimientoDatabase.class, "seguimiento.db").fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return instance;
    }
}
