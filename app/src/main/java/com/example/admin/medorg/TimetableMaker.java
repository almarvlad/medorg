package com.example.admin.medorg;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;

import com.example.admin.medorg.Room.AppDatabase;
import com.example.admin.medorg.Room.DBDao;
import com.example.admin.medorg.Room.UserMedicine;

import java.util.ArrayList;

public class TimetableMaker {
    final int mealInterval = 120; // = 2часа - минимальный интервал между приёмами пищи
    final int stInterval = 30;  // стандартный минимальный интервал между приёмами лекарств + возможен перед завтраком
                                // а так же минимальный интервал для приёма до и после еды
    final int dayFull = 1440;   // кол-во минут в сутках всего
    int de, db, dayDuration;

    AppDatabase adb;
    DBDao dao;
    ArrayList<UserMedicine> userMeds;
    ArrayList<MedSpec> priorityList;

    public TimetableMaker(Context cntxt) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(cntxt);
        db = prefs.getInt("day_begin", 360);
        de = prefs.getInt("day_end", 1320);
        if (de < db)
            dayDuration = (dayFull - db) + de;
        else dayDuration = de - db;

        adb = AppDatabase.getDatabase(cntxt);
        dao = adb.Dao();
        userMeds = dao.getActiveMeds();
    }

    public void setPriority() {
        for (int i = 0; i < userMeds.size(); i++) { // формируем лист с характеристиками лекарств для его последующей сортировки
            long idmed = userMeds.get(i).getID();
            priorityList.add(new MedSpec((byte)dao.getNoncompat(idmed).size(), userMeds.get(i).getInstruct(), idmed));
        }
    }


    public class ShellSort {
        public void sort (int[] arr) {
            int increment = arr.length / 2;
            while (increment >= 1) {
                for (int startIndex = 0; startIndex < increment; startIndex++) {
                    insertionSort(arr, startIndex, increment);
                }
                increment = increment / 2;
            }
        }

        private void insertionSort (int[] arr, int startIndex, int increment) {
            for (int i = startIndex; i < arr.length - 1; i = i + increment) {
                for (int j = Math.min(i + increment, arr.length - 1); j - increment >= 0; j = j - increment) {
                    if (arr[j - increment] > arr[j]) {
                        int tmp = arr[j];
                        arr[j] = arr[j - increment];
                        arr[j - increment] = tmp;
                    } else break;
                }
            }
        }
    }

    public class MedSpec{
        long id;
        byte relationsCount;
        byte mealDepend; // 0 не важно, 1 - во время еды, 2 - до/после еды
        int priorSpec;

        public MedSpec(byte nc, byte meal, long id) {
            this.id = id;
            relationsCount = nc;
            mealDepend = meal;
            priorSpec = relationsCount + mealDepend;
        }
    }
}
