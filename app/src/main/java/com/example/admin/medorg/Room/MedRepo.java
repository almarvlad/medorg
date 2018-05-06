package com.example.admin.medorg.Room;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;

public class MedRepo {

    private DBDao mDBDao;
    private LiveData<List<UserMedicine>> mAllMeds;

    MedRepo(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        mDBDao = db.Dao();
        mAllMeds = mDBDao.getAllMeds();
    }

    LiveData<List<UserMedicine>> getAllMeds() {
        return mAllMeds;
    }

    public void insert (UserMedicine userMed) {
        new insertAsyncTask(mDBDao).execute(userMed);
    }

    private static class insertAsyncTask extends AsyncTask<UserMedicine, Void, Void> {

        private DBDao mAsyncTaskDao;

        insertAsyncTask(DBDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final UserMedicine... params) {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }
}
