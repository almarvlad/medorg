package com.example.admin.medorg.Room;

import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Dao;

import java.util.List;

@Dao
public interface DBDao {
    @Query("SELECT * FROM medicals")
    List<UserMedicine> getAllMeds();

    @Insert
    void insertAll(UserMedicine userMedicine);
}
