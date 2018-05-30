package com.example.admin.medorg.Room;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.TypeConverters;
import java.util.List;
import java.util.Date;

@Dao
public interface TimetableCompleteDao {
    @Insert
    void insert(TimetableComplete ttComplete);

    @Query("SELECT * from TimetableComplete WHERE date_time BETWEEN :date_one AND :date_two ORDER BY date_time")
    List<TimetableComplete> getTimetableByDate(long date_one, long date_two);

    @Query("SELECT DISTINCT date_time from TimetableComplete WHERE date_time BETWEEN :date_one AND :date_two ORDER BY date_time")
    List<Long> getTimeList(long date_one, long date_two);

    /*
    @Query("SELECT DISTINCT date_time, id_med FROM TimetableComplete s1 " +
            "WHERE (date_time BETWEEN :date_one AND :date_two) " +
            "AND " +
            "id_med = (SELECT DISTINCT date_time, id_med FROM TimetableComplete s1 )")
    List<Long> getTimeList(long date_one, long date_two);
    */
}
