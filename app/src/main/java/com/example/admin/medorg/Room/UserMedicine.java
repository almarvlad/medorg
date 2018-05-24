package com.example.admin.medorg.Room;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Date;

@Entity(tableName = "medicals")
public class UserMedicine {
    public UserMedicine(String name, float timePer, long courseStart,
                        boolean timeType, String weekdays, float dose, String doseForm,
                        byte instruct, String addInstruct, int duration, boolean active) {
        this.name = name;
        this.timePer = timePer;
        this.courseStart = courseStart;
        this.timeType = timeType;
        this.weekdays = weekdays;
        this.dose = dose;
        this.doseForm = doseForm;
        this.instruct = instruct;
        this.addInstruct = addInstruct;
        this.duration = duration;
        this.isActive = active;
    }

    public void setID(@NonNull int ID) {
        this.ID = ID;
    }

    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "id")
    private int ID;

    public int getID(){
        return this.ID;
    }

    @ColumnInfo(name = "med_name")
    @NonNull
    private String name;

    public float getTimePer() {
        return timePer;
    }

    public void setTimePer(float timePer) {
        this.timePer = timePer;
    }

    @ColumnInfo(name = "time_per")
    private float timePer;

    @ColumnInfo(name = "course_start")
    private long courseStart;

    public long getCourseStart() {
        return courseStart;
    }

    public void setCourseStart(long courseStart) {
        this.courseStart = courseStart;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getWeekdays() {
        return weekdays;
    }

    public void setWeekdays(String weekdays) {
        this.weekdays = weekdays;
    }

    public float getDose() {
        return dose;
    }

    public void setDose(float dose) {
        this.dose = dose;
    }

    public String getDoseForm() {
        return doseForm;
    }

    public void setDoseForm(String doseForm) {
        this.doseForm = doseForm;
    }

    public byte getInstruct() {
        return instruct;
    }

    public void setInstruct(byte instruct) {
        this.instruct = instruct;
    }

    public String getAddInstruct() {
        return addInstruct;
    }

    public void setAddInstruct(String addInstruct) {
        this.addInstruct = addInstruct;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    private int duration;
    private String weekdays;
    private float dose;
    private String doseForm;
    private byte instruct;

    @Nullable
    private String addInstruct;
    @Nullable
    private boolean isActive;

    //0 - частота (Н раз в день), 1 - интервалы (каждые Н часов)
    public boolean isTimeType() {
        return timeType;
    }

    public void setTimeType(boolean timeType) {
        this.timeType = timeType;
    }

    private boolean timeType;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
