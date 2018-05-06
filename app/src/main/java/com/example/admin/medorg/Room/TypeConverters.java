package com.example.admin.medorg.Room;

import android.arch.persistence.room.TypeConverter;

import java.util.Date;

public class TypeConverters {
    @TypeConverter
    public Long dateToTimestamp(Date date) {
        if (date == null) {
            return null;
        } else {
            return date.getTime();
        }
    }
}
