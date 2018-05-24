package com.example.admin.medorg;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;

import com.example.admin.medorg.Room.AppDatabase;
import com.example.admin.medorg.Room.DBDao;
import com.example.admin.medorg.Room.UserMedicine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TimetableMaker {
    final int mealInterval = 120; // = 2часа - минимальный интервал между приёмами пищи
    final int stInterval = 30;  // стандартный минимальный интервал между приёмами лекарств + возможен перед завтраком
                                // а так же минимальный интервал для приёма до и после еды
    final int dayFull = 1440;   // кол-во минут в сутках всего
    final int hour = 60;        // просто час
    int de, db, dayDuration, mealCount;

    AppDatabase adb;
    DBDao dao;
    List<UserMedicine> userMeds;
    List<MedSpec> priorityList; // здесь лекарства выстроены в порядке по приоритету,
                                // учитывающему и кол-во связей и сочетание с пищей

    ArrayList<Object> day = new ArrayList<>();
    private static final String TAG = "SET_PRIORITY";
    private static final String MEALTIME = "MEALTIME";

    public TimetableMaker(Context cntxt) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(cntxt);
        mealCount = Integer.parseInt(prefs.getString("meal_count", "3"));
        db = prefs.getInt("day_begin", 360);
        de = prefs.getInt("day_end", 1320);
        int minDay = mealCount*mealInterval + stInterval; // минимальная длина дня = кол-во приёмов пищи * 2 часа + 30 минут утром
        if (de < db)
            dayDuration = (dayFull - db) + de;
        else dayDuration = de - db;
                                        // если минимальная длина дня меньше или равна фактической (установленной пользователем),
        if (minDay <= dayDuration) {    // то можно производиь расчёты дальше
            adb = AppDatabase.getDatabase(cntxt);
            dao = adb.Dao();

            setMealTime();
        }
    }

    public void setPriority() {
        userMeds = dao.getActiveMeds();
        priorityList = new ArrayList<>(userMeds.size());
        Log.d(TAG, "кол-во активных лекарств: " + userMeds.size());

        for (int i = 0; i < userMeds.size(); i++) { // формируем лист с характеристиками лекарств для его последующей сортировки
            long idmed = userMeds.get(i).getID();
            byte meal;
            switch (userMeds.get(i).getInstruct()) {
                case 0:
                case 2:
                    meal = 2;
                    break;
                case 1:
                    meal = 1;
                    break;
                default:
                    meal=0;
                    break;
            }
            priorityList.add(new MedSpec((byte)dao.getNoncompat(idmed).size(), meal, idmed));
        }
        Log.d(TAG, "PriorityList.size = " + priorityList.size());
        if (priorityList.size()!=0) {
            // new ShellSort().sort(priorityList);

            Collections.sort(priorityList, new Comparator<MedSpec>() {
                public int compare(MedSpec o1, MedSpec o2) {
                    int sComp;
                    byte x1 = o1.getRelationsCount(); // сначала сортировка по кол-ву связей несовмесимости с др лекарствами
                    byte x2 = o2.getRelationsCount();
                    sComp = (x1-x2 < 0) ? -1 : (x1-x2 > 0) ? 1 : x1-x2;

                    if (sComp != 0) {
                        return sComp;
                    } else {
                        byte x3 = o1.getMealDepend(); // затем сортируем по совмесимости с едой
                        byte x4 = o2.getMealDepend();
                        sComp = (x3-x4 < 0) ? -1 : (x3-x4 > 0) ? 1 : x3-x4;
                        return sComp;
                    }
                }});

            for (int i = 0; i < priorityList.size(); i++) {
                Log.d(TAG, "Имя лекарства: " + dao.getById(priorityList.get(i).id).getName());
            }
        }
    }

    // пусть останеся на всякий, вдруг понадобится ещё
    public class ShellSort {
        public void sort (List<MedSpec> arr) {
            int increment = arr.size() / 2;
            while (increment >= 1) {
                for (int startIndex = 0; startIndex < increment; startIndex++) {
                    insertionSort(arr, startIndex, increment);
                }
                increment = increment / 2;
            }
        }

        private void insertionSort (List<MedSpec> arr, int startIndex, int increment) {
            for (int i = startIndex; i < arr.size() - 1; i = i + increment) {
                for (int j = Math.min(i + increment, arr.size() - 1); j - increment >= 0; j = j - increment) {
                    if (arr.get(j - increment).getRelationsCount() > arr.get(j).getRelationsCount()) {
                        Collections.swap(arr, j, j - increment);
                    } else break;
                }
            }
        }
    }

    public class MedSpec{
        long id;
        byte relationsCount; // кол-во несовместимых лекарств с данным
        byte mealDepend; // 0 - не важно, 1 - во время еды, 2 - до/после еды

        public MedSpec(byte nc, byte meal, long id) {
            this.id = id;
            relationsCount = nc;
            mealDepend = meal;
        }

        public byte getRelationsCount() { return relationsCount; }
        public byte getMealDepend() { return mealDepend; }
    }

    public void setMealTime(){
        int meald = dayDuration - stInterval; // отнимаем стандартное утреннее время перед едой
        int varMealInt = meald / mealCount;
        if (varMealInt > 4*hour) {
            meald -= 4*hour; // оставляем интервал в 4 часа между сном и последним приёмом пищи
            Log.d(MEALTIME, "продолжительность дня: " + meald);
            varMealInt = meald / (mealCount-1);
            Log.d(MEALTIME, "интервалы между едой: " + varMealInt);
        }
        day.add(db + 30); // 1 приём пищи
        for (int i = 1; i < mealCount; i++) {
            day.add((int)day.get(0) + i*varMealInt);
        }
        for (int i = 0; i < mealCount; i++) {
            int hours = (int)day.get(i) / 60;
            int minutes = (int)day.get(i) % 60;
            Log.d(MEALTIME, (i+1) + " приём пищи в " + hours + ":" + minutes);
        }
    }

    public void setMealMeds() {

    }
}
