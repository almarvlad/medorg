package ru.markova.admin.medorg.Room;

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

    @Query("SELECT * from timetable WHERE weekday = :wday AND (time > :currtime OR mark = -1) ORDER BY time")
    List<Timetable> getWeekdayTimetableFromCurrTime(int wday, int currtime);

    @Query("DELETE FROM timetable WHERE weekday = :day")
    void deleteTimetable(byte day);

    @Query("DELETE FROM timetable WHERE mark = :id")
    void deleteMedFromTimetable(long id);
}
