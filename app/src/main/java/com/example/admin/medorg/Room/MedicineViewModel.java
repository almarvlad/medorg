package com.example.admin.medorg.Room;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import java.util.ArrayList;
import java.util.List;

public class MedicineViewModel extends AndroidViewModel {

    private MedRepo mRepository;

    private LiveData<List<UserMedicine>> mAllMeds;

    public List<UserMedicine> getMedsAL() {
        return medsAL;
    }

    private List<UserMedicine> medsAL;
    private String[] medsCount;

    public MedicineViewModel (Application app) {
        super(app);
        mRepository = new MedRepo(app);
        mAllMeds = mRepository.getAllMeds();
        medsCount = mRepository.mDBDao.medsCount();
        medsAL = mRepository.mDBDao.getAllMedsAL();
    }

    public LiveData<List<UserMedicine>> getAllMeds() { return mAllMeds; }

    public String[] getMedsCount() {
        return medsCount;
    }

    public void insert(UserMedicine userMed, long[] noncompat) { mRepository.insert(userMed, noncompat); }

    public long getLastId () {
        return mRepository.res;
    }
}
