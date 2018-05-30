package com.example.admin.medorg.Room;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface TimetableDao {
    @Insert
    void insert(Timetable timetable);

    @Query("SELECT * from timetable ORDER BY weekday")
    LiveData<List<Timetable>> getTimetable();

    @Query("SELECT * from timetable WHERE weekday = :wday ORDER BY time")
    List<Timetable> getWeekdayTimetable(int wday);
}
