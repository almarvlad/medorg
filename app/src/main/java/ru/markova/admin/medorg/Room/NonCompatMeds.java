package ru.markova.admin.medorg.Room;

import android.arch.persistence.room.Entity;

@Entity(tableName = "noncompatible", primaryKeys = {"id_one","id_two"})
public class NonCompatMeds {
    public NonCompatMeds(long id_one, long id_two) {
        this.id_one = id_one;
        this.id_two = id_two;
    }

    public long id_one;
    public long id_two;

}
