package com.example.admin.medorg.Room;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

@Database(entities = {UserMedicine.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract DBDao Dao();
}
