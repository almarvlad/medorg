package com.example.admin.medorg.Room;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

@Entity(tableName = "timetable")
public class Timetable {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "id")
    private int id;

    @NonNull // номер дня недели
    private byte weekday;
    @NonNull
    private int time;
    @NonNull // либо ID лекарства, либо -1 - приём пищи
    private int mark;

    @NonNull
    public int getTime() { return time; }
    public void setTime(@NonNull int time) { this.time = time; }

    @NonNull
    public int getMark() { return mark; }
    public void setMark(@NonNull int mark) { this.mark = mark; }

    @NonNull
    public byte getWeekday() { return weekday; }
    public void setWeekday(@NonNull byte weekday) { this.weekday = weekday; }

    @NonNull
    public int getId() { return id; }
}
