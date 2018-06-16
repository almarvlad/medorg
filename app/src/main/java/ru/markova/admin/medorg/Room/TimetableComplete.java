package ru.markova.admin.medorg.Room;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

@Entity
public class TimetableComplete {
    public TimetableComplete(long dateTime, @NonNull int idMed) {
        this.dateTime = dateTime;
        this.idMed = idMed;
        completion = -1;
    }

    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "date_time")
    private long dateTime; // дата и время приёма лекарства

    @NonNull
    @ColumnInfo(name = "id_med")
    private int idMed;

    @Nullable // -1 нет отметки; 0 не принято; 1 принято
    private int completion;

    @NonNull
    public int getId() { return id; }
    public void setId(@NonNull int id) { this.id = id; }

    public long getDateTime() { return dateTime; }
    public void setDateTime(long dateTime) { this.dateTime = dateTime; }

    @NonNull
    public int getIdMed() { return idMed; }
    public void setIdMed(@NonNull int idMed) { this.idMed = idMed; }

    @Nullable
    public int getCompletion() { return completion; }
    public void setCompletion(@NonNull int completion) { this.completion = completion; }
}
