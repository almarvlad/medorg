package com.example.admin.medorg.Room;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;
import android.util.Log;

import com.example.admin.medorg.TimetableMaker;

import java.util.List;

public class MedRepo {
    public MedicineDao mMedicineDao;
    public TimetableDao mTimetableDao;
    private LiveData<List<UserMedicine>> mAllMeds;

    private LiveData<List<Timetable>> timetableList;
    public static Long res;
    TimetableMaker ttmaker;
    private static final String TAG = "SET_PRIORITY";


    MedRepo(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        mMedicineDao = db.Dao();
        mAllMeds = mMedicineDao.getAllMeds();

        mTimetableDao = db.ttDao();
        timetableList = mTimetableDao.getTimetable();

        ttmaker = new TimetableMaker(application);
    }

    LiveData<List<UserMedicine>> getAllMeds() {
        return mAllMeds;
    }
    LiveData<List<Timetable>> getTimetableList() { return timetableList; }

    public void insert (UserMedicine userMed, long[] noncompat) {
        new insertAsyncTask(mMedicineDao, userMed, noncompat, ttmaker).execute();
    }

    /*
    public void insertTimetable (Timetable timetable) {
        new insertAsyncTask(mMedicineDao, userMed, noncompat, ttmaker).execute();
    }*/

    public void deleteMed(long id) {
        new deleteMedAsyncTask(mMedicineDao, id).execute();
    }

    // добавление в словарь
    private static class insertAsyncTask extends AsyncTask<UserMedicine, Void, Long> {
        private final MedicineDao mAsyncTaskDao;
        private UserMedicine umed;
        private long[] nc;
        private TimetableMaker ttMaker;

        insertAsyncTask(MedicineDao dao, UserMedicine umed, long[] nonc, TimetableMaker tm) {
            mAsyncTaskDao = dao;
            this.umed = umed;
            this.nc = nonc;
            ttMaker = tm;
        }

        @Override
        protected Long doInBackground(final UserMedicine... params) {
            Long r = mAsyncTaskDao.insert(umed);
            res = r;
            Log.d("SAVE_MED", "last id: "+r);
            Log.d(TAG, "Saving med info in background ...");

            if (nc != null) { //если есть список несовместимых лекарств
                if (nc.length>0){ //точно, что этот список есть и его длина не равна 0
                    for (int i = 0; i < nc.length; i++) {
                        mAsyncTaskDao.addNoncompat(new NonCompatMeds(r,nc[i])); // добавляем в таблицу несовместимых лекарств связи
                        Log.d("SAVE_MED","NC: "+r+ "; "+nc[i]); // и выводим их в лог, для проверки
                    }
                }
            }
            return r;
        }

        protected void onPostExecute(Long result) {
            res = result;
            Log.d(TAG, "Call SetPriority method ...");
            ttMaker.setPriority();
            ttMaker.setTimeAllMeds();
            ttMaker.sortAndSaveTimetable();
            Log.d("SAVE_MED", "res: "+result);
        }
    }

    // удаление из словаря
    private static class deleteMedAsyncTask extends AsyncTask<Void, Void, String> {
        private final MedicineDao mAsyncTaskDao;
        private long id;

        deleteMedAsyncTask(MedicineDao dao, long id) {
            mAsyncTaskDao = dao;
            this.id = id;
        }

        @Override
        protected String doInBackground(Void... params) {
            String r = mAsyncTaskDao.getById(id).getName();
            Log.d("MED_INFO", "Фоновый процесс по удалению из бд");
            mAsyncTaskDao.deleteMed(id);
            mAsyncTaskDao.deleteNoncompatMed(id);
            Log.d("MED_INFO", "Лекарство с id " + id + " удалено");
            return r;
        }
    }
}
