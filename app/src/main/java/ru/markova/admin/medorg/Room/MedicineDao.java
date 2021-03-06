package ru.markova.admin.medorg.Room;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Update;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface MedicineDao {
    @Query("SELECT * FROM medicals")
    LiveData<List<UserMedicine>> getAllMeds();

    @Query("SELECT * FROM medicals WHERE id = :medId")
    UserMedicine getById(long medId);

    @Query("UPDATE medicals SET isActive = 0 WHERE id = :medId")
    void setStoppedMed(long medId);

    @Query("UPDATE medicals SET isActive = 1 WHERE id = :medId")
    void setActiveMed(long medId);

    @Query("SELECT * FROM medicals WHERE id = :medId")
    LiveData<UserMedicine> getByIdLiveData(long medId);

    @Query("SELECT * FROM noncompatible WHERE id_one = :id OR id_two = :id")
    List<NonCompatMeds> getNoncompat(long id);

    @Query("SELECT * FROM medicals WHERE isActive=1 AND weekdays LIKE :day")
    List<UserMedicine> getActiveMeds(String day);

    @Query("SELECT * FROM medicals")
    List<UserMedicine> getAllMedsAL();

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

    @Update
    void update(UserMedicine umed);
}
