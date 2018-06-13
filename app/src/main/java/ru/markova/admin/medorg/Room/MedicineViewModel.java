package ru.markova.admin.medorg.Room;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.util.Log;

import java.util.List;

public class MedicineViewModel extends AndroidViewModel {

    private MedRepo mRepository;

    private LiveData<List<UserMedicine>> mAllMeds;  // LiveData
    private List<UserMedicine> medsAL;              // лист самих записей о лекарствах
    private String[] medsCount;                     // массив названий лекарств

    public MedicineViewModel (Application app) {
        super(app);
        mRepository = new MedRepo(app);
        mAllMeds = mRepository.getAllMeds();
        medsCount = mRepository.mMedicineDao.medsCount();
        medsAL = mRepository.mMedicineDao.getAllMedsAL();
    }

    public LiveData<List<UserMedicine>> getAllMeds() { return mAllMeds; }

    public String[] getMedsCount() {
        return medsCount;
    }

    public void insert(UserMedicine userMed, long[] noncompat) { mRepository.insert(userMed, noncompat); }

    public long getLastId () {
        return mRepository.res;
    }

    public List<UserMedicine> getMedsAL() {
        return medsAL;
    }

    public void deletemed(long id) {
        Log.d("MED_INFO", "вызван метод в репозитории");
        mRepository.deleteMed(id);
    }
}
