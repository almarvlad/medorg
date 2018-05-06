package com.example.admin.medorg.Room;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "medicals")
public class UserMedicine {
    public UserMedicine(String name, float interval) {
        this.name = name;
        this.interval = interval;
    }

    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "id")
    private long ID;

    public long getID(){
        return this.ID;
    }

    @ColumnInfo(name = "med_name")
    private String name;

    @ColumnInfo(name = "interval")
    private float interval;
    /*
    public long courseStart;
    public int duration;
    public String weekdays;
    public float dose;
    public byte doseForm;
    public byte instruct;
    public String addInstruct;
    public boolean isActive;
    */

    public void setID(@NonNull long ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getInterval() {
        return interval;
    }

    public void setInterval(float interval) {
        this.interval = interval;
    }
}
