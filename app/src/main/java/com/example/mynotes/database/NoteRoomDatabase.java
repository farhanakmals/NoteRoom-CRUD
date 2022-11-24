package com.example.mynotes.database;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Note.class}, version = 1, exportSchema = false)
public abstract class NoteRoomDatabase extends RoomDatabase {

    public abstract NoteDao notedao();

    public static volatile  NoteRoomDatabase INSTANCE;

    public static NoteRoomDatabase getDatabase(final Context context){
        if (INSTANCE == null) {
            synchronized (NoteRoomDatabase.class){
                INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                        NoteRoomDatabase.class, "note_database").build();
            }
        }
        return INSTANCE;
    }
}
