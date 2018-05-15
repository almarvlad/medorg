package com.example.admin.medorg.Room;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;
import android.util.Log;

import java.util.List;

public class MedRepo {
    public DBDao mDBDao;
    private LiveData<List<UserMedicine>> mAllMeds;
    public static Long res;

    MedRepo(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        mDBDao = db.Dao();
        mAllMeds = mDBDao.getAllMeds();
    }

    LiveData<List<UserMedicine>> getAllMeds() {
        return mAllMeds;
    }

    public void insert (UserMedicine userMed, long[] noncompat) {
        new insertAsyncTask(mDBDao, userMed, noncompat).execute();
    }

    public void deleteMed(long id) {
        Log.d("MED_INFO", "вызван AsyncTask для удаления");
        new deleteMedAsyncTask(mDBDao, id).execute();
    }

    // добавление в словарь
    private static class insertAsyncTask extends AsyncTask<UserMedicine, Void, Long> {
        private final DBDao mAsyncTaskDao;
        private UserMedicine umed;
        private long[] nc;

        insertAsyncTask(DBDao dao, UserMedicine umed, long[] nonc) {
            mAsyncTaskDao = dao;
            this.umed = umed;
            this.nc = nonc;
        }

        @Override
        protected Long doInBackground(final UserMedicine... params) {
            Long r = mAsyncTaskDao.insert(umed);
            res = r;
            Log.d("SAVE_MED", "last id: "+r);

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
            Log.d("SAVE_MED", "res: "+result);
        }
    }

    // удаление из словаря
    private static class deleteMedAsyncTask extends AsyncTask<Void, Void, String> {
        private final DBDao mAsyncTaskDao;
        private long id;

        deleteMedAsyncTask(DBDao dao, long id) {
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
