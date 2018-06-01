package com.example.admin.medorg.Room;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.TypeConverters;

import com.example.admin.medorg.Fragments.DayPageFragment;

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

    @Query("DELETE FROM TimetableComplete WHERE date_time BETWEEN :date_one AND :date_two")
    void deleteCurrentTimetable(long date_one, long date_two);

    @Query("SELECT date_time, id_med FROM TimetableComplete s1 " +
            "WHERE (date_time BETWEEN :date_one AND :date_two) " +
            "AND " +
            "id_med = (SELECT s2.id_med FROM TimetableComplete s2 " +
            "WHERE s1.date_time = s2.date_time AND id_med>-1 LIMIT 1) " +
            "UNION SELECT date_time, id_med FROM TimetableComplete " +
            "WHERE (date_time BETWEEN :date_one AND :date_two) AND id_med=-1 " +
            "ORDER BY date_time")
    List<DayPageFragment.TimeMarkLong> getDistinctTimeList(long date_one, long date_two);

/*
    SELECT time, mark
        FROM   timetable s1
        WHERE  mark=(SELECT TOP 1 s2.mark
            FROM timetable s2
            WHERE s1.time = s2.time AND mark>-1)
        UNION SELECT time, mark
        FROM   timetable
        WHERE  mark=-1
        ORDER BY time;
    */
}