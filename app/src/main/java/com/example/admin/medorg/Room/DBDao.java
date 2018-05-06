package com.example.admin.medorg.Room;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Dao;

import java.util.List;

@Dao
public interface DBDao {
    @Query("SELECT * FROM medicals")
    LiveData<List<UserMedicine>> getAllMeds();

    @Insert
    void insert(UserMedicine userMedicine);
}
