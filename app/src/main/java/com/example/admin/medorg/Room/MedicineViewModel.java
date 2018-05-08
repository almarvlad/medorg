package com.example.admin.medorg.Room;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import java.util.List;

public class MedicineViewModel extends AndroidViewModel {

    private MedRepo mRepository;

    private LiveData<List<UserMedicine>> mAllMeds;
    private String[] medsCount;

    public MedicineViewModel (Application app) {
        super(app);
        mRepository = new MedRepo(app);
        mAllMeds = mRepository.getAllMeds();
        medsCount = mRepository.mDBDao.medsCount();
    }

    public LiveData<List<UserMedicine>> getAllMeds() { return mAllMeds; }

    public String[] getMedsCount() {
        return medsCount;
    }

    public void insert(UserMedicine userMed) { mRepository.insert(userMed); }
}
