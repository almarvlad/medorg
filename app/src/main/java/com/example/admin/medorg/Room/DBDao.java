package com.example.admin.medorg.Room;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Dao;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface DBDao {
    @Query("SELECT * FROM medicals")
    LiveData<List<UserMedicine>> getAllMeds();

    @Query("SELECT * FROM medicals WHERE id = :medId")
    UserMedicine getById(long medId);

    @Query("SELECT * FROM noncompatible WHERE id_one = :id OR id_two = :id")
    List<NonCompatMeds> getNoncompat(long id);

    @Query("SELECT * FROM medicals WHERE isActive=1")
    ArrayList<UserMedicine> getActiveMeds();

    @Insert
    long insert(UserMedicine userMedicine);

    @Query("SELECT med_name FROM medicals")
    String[] medsCount();

    @Insert
    void addNoncompat(NonCompatMeds noncompatMeds);

    @Query("DELETE from medicals WHERE id = :id")
    void deleteMed(long id);

    @Query("DELETE from noncompatible WHERE id_one = :id OR id_two = :id")
    void deleteNoncompatMed(long id);
}
