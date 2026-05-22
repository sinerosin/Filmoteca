package com.example.filmoteca.Model;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Media.class}, version = 5)
public abstract class MediaDatabase extends RoomDatabase {
    public abstract MediaDao mediaDao();
    private static MediaDatabase instance;
    public static MediaDatabase getInstance(final Context context) {
        if (instance == null) {
            synchronized (MediaDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(context.getApplicationContext(),
                                    MediaDatabase.class, "media.db").fallbackToDestructiveMigration()
                            .build();
                }
            }
        }return instance;
    }

}
