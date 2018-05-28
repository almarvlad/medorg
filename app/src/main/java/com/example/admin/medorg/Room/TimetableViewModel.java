package com.example.admin.medorg.Room;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;

public class TimetableViewModel extends AndroidViewModel {
    private MedRepo mRepository;
    private LiveData<List<Timetable>> timetableList;

    public TimetableViewModel(@NonNull Application application) {
        super(application);
        mRepository = new MedRepo(application);
        timetableList = mRepository.getTimetableList();
    }

    LiveData<List<Timetable>> getTimetableList() { return timetableList; }

    //public void insert(Timetable timetable) { mRepository.insertTimetable(timetable); }
}
