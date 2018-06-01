package com.example.admin.medorg;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;

import com.example.admin.medorg.Room.AppDatabase;
import com.example.admin.medorg.Room.MedicineDao;
import com.example.admin.medorg.Room.MedicineViewModel;
import com.example.admin.medorg.Room.Timetable;
import com.example.admin.medorg.Room.TimetableComplete;
import com.example.admin.medorg.Room.TimetableCompleteDao;
import com.example.admin.medorg.Room.TimetableDao;
import com.example.admin.medorg.Room.TimetableViewModel;
import com.example.admin.medorg.Room.UserMedicine;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import static java.lang.Math.abs;

public class TimetableMaker {
    final int mealInterval = 120; // = 2часа - минимальный интервал между приёмами пищи
    final int stInterval = 30;  // стандартный минимальный интервал между приёмами лекарств + возможен перед завтраком
                                // а так же минимальный интервал для приёма до и после еды
    final int dayFull = 1440;   // кол-во минут в сутках всего
    final int hour = 60;        // просто час
    int de, db, dayDuration, mealCount;

    AppDatabase adb;
    MedicineDao dao;
    TimetableDao ttDao;
    TimetableCompleteDao ttCompleteDao;
    List<UserMedicine> userMeds;
    List<Timetable> dayTimetable;
    List<MedSpec> priorityList; // здесь лекарства выстроены в порядке по приоритету,
                                // учитывающему и кол-во связей и сочетание с пищей

    List<MealAround> mealList;
    List<TimeMark> day = new ArrayList<>();
    //TreeMap<Integer, ArrayList<Integer>> day = new TreeMap<>();
    private static final String TAG = "SET_PRIORITY";
    private static final String MEALTIME = "MEALTIME";

    //private TimetableViewModel mTimetableViewModel;

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
            ttDao = adb.ttDao();
            ttCompleteDao = adb.ttCompleteDao();

            //mTimetableViewModel = ViewModelProviders.of(cntxt).get(TimetableViewModel.class);

            setMealTime();
        }
    }

    public void setPriority() {
        userMeds = dao.getActiveMeds();
        priorityList = new ArrayList<>(userMeds.size());
        //Log.d(TAG, "кол-во активных лекарств: " + userMeds.size());

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

    public void sortAndSaveTimetable(){
        new ShellSort().sort(day);
        ttDao.deleteTimetable();
        for (byte i = 1; i <= 7; i++) {
            for (int j = 0; j < day.size(); j++)
                ttDao.insert(new Timetable(i, day.get(j).getTime(), day.get(j).getMark()));
        }

        Calendar c = Calendar.getInstance(); // текущая дата
        Calendar plusMonth = Calendar.getInstance();;
        plusMonth.add(Calendar.MONTH, 1); // дата через месяц
        int nowTime = c.get(Calendar.HOUR_OF_DAY)*60 + c.get(Calendar.MINUTE);

        ttCompleteDao.deleteCurrentTimetable(c.getTimeInMillis(), plusMonth.getTimeInMillis());
        //byte j = 0;
        while (!c.after(plusMonth)) {
            int dayNumber = (c.get(Calendar.DAY_OF_WEEK)-1 > 0) ? c.get(Calendar.DAY_OF_WEEK)-1 : 7; // находим день недели текущей даты
            dayTimetable = ttDao.getWeekdayTimetable(dayNumber); // получаем расписание для этого дня недели

            // давайте пока обойдёмся без текущего времени, хорошо?
            for (int i = 0; i < dayTimetable.size(); i++) {
                Calendar timeTakeMed = new GregorianCalendar(c.get(Calendar.YEAR), c.get(Calendar.MONTH),
                        c.get(Calendar.DAY_OF_MONTH), getHours(dayTimetable.get(i).getTime()), getMinutes(dayTimetable.get(i).getTime()));
                ttCompleteDao.insert(new TimetableComplete(timeTakeMed.getTimeInMillis(), dayTimetable.get(i).getMark()));
            }
            //текущая дата в цикле
            Calendar date_one = new GregorianCalendar(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), 0, 0, 0);

            // текущая дата в цикле + 1 день
            Calendar date_two = new GregorianCalendar(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
            date_two.add(Calendar.DATE, 1);

            // переходим к сл дню - увеличиваем переменную цикла
            c.add(Calendar.DATE, 1); // переходим к сл дню
        }
    }

    public void setMealTime(){
        int meald = dayDuration - stInterval; // отнимаем стандартное утреннее время перед едой
        int varMealInt = meald / mealCount;
        if (varMealInt > 4*hour) {
            meald -= 4*hour; // оставляем интервал в 4 часа между сном и последним приёмом пищи
            varMealInt = meald / (mealCount-1);
        }
        /*
        int i = 1;
        for (Map.Entry e : day.entrySet()) {
            int hours = (int)e.getKey() / 60;
            int minutes = (int)e.getKey() % 60;
            Log.d(MEALTIME, i + " приём пищи в " + hours + ":" + minutes);
            i++;
        }
        */
        mealList = new ArrayList<>(); // список "отметок" со временем приёма пищи
        mealList.add(new MealAround(db+30)); //
        for (int i = 1; i < mealCount; i++) {
            mealList.add(new MealAround(mealList.get(0).mealTime + i*varMealInt));
        }
    }

    public int getHours(int time){ return time / 60; }
    public int getMinutes(int time){ return time % 60; }


    public void setTimeAllMeds(){
        for (int i = 0; i < userMeds.size(); i++) {
            Log.d(MEALTIME, "вызван setTimeAllMeds");
            setTimeMed(userMeds.get(i));
        }
        for (int i = 0; i < mealList.size(); i++) {
            mealList.get(i).readDataMeal();
        }
    }

    public void setTimeMed(UserMedicine med) {
        if (med.isTimeType()){ // если интервалы (каждые н часов)
            int frequency = (int)(dayFull / (med.getTimePer() * hour)); // находим кол-во раз, которое будет умещаться в сутках
            switch (med.getInstruct()){
                case 3: // не важно
                    int tempTime;
                    for (int i = 0; i < frequency; i++) { // для каждого раза в день
                        if (med.getTimePer()*hour >= (dayFull-dayDuration)) // если больше периода сна, то расставляем в период бодрствования
                            tempTime = db + med.getTimePer()*hour*i;
                        else tempTime = med.getTimePer()*i*hour; // иначе расставляем по всем суткам
                        day.add(new TimeMark(tempTime, (int)med.getID()));
                    }
                    break;
                case 0:
                case 1:
                case 2:
                    if (frequency > mealCount)
                        Log.d(MEALTIME, "нужно увеличить кол-во приёмов пищи :с");
                    else if (frequency == mealCount)
                        for (int i = 0; i < mealCount; i++)
                            mealList.get(i).addMed((int)med.getID(), med.getInstruct());
                    else
                        setMedTake((int)(med.getTimePer() * hour), med.getID(), med.getInstruct(), frequency);
                    break;
                default: break;
            }
        } else { // если частота (н раз в день)
            int recomInterval = dayFull / (int)med.getTimePer(); // находим рекомендуемый интервал
            switch (med.getInstruct()){
                case 3: // н раз в день // не важно
                    int tempTime;
                    for (int i = 0; i < med.getTimePer(); i++) { // для каждого раза в день
                        if (recomInterval >= dayFull-dayDuration)
                            tempTime = db + recomInterval*i;
                        else tempTime = (recomInterval / 2) + recomInterval*i;
                        day.add(new TimeMark(tempTime, (int)med.getID()));
                    }
                    break;
                case 0:
                case 1:
                case 2:
                    if (med.getTimePer()>mealCount) // если приёмов пищи меньше
                        Log.d(MEALTIME, "нужно увеличить кол-во приёмов пищи :с");
                    else if (med.getTimePer()== mealCount) { // если приёмов пищи столько же, то расставляем рядом со всеми рассчитанными приёмами пищи
                        for (int i = 0; i < mealCount; i++)
                            mealList.get(i).addMed((int)med.getID(), med.getInstruct());
                    } else
                        setMedTake(recomInterval, med.getID(), med.getInstruct(), med.getTimePer()); // расставляем по времени приёма пищи
                    break;
                default: break;
            }
        }
    }
    public void setMedTake (int recomInterval, long id, byte instr, float timePer) {
        for (int i = 0; i < timePer; i++) { // для каждого раза в день
            int tempTime = (recomInterval / 2) + recomInterval*i; // находим рекомендуемое время приёма
            int minTime = dayFull;
            int mealNumber = 0;
            for (int j = 0; j < mealList.size(); j++) { // для каждого приёма пищи находим модуль времени между tempTime
                if (minTime > abs(mealList.get(j).mealTime-tempTime)) {
                    mealNumber = j; minTime = abs(mealList.get(j).mealTime-tempTime);
                }
            }
            mealList.get(mealNumber).addMed((int)id, instr);
        }
    }

//  ВНУТРЕННИЕ КЛАССЫ
    class TimeMark { // отметка во времени
        private int time;
        private int mark; // id лекарства

        public TimeMark(int time, int mark) {
            this.time = time;
            this.mark = mark;
        }

        public int getTime() { return time; }
        public void setTime(int time) { this.time = time; }
        public int getMark() { return mark; }
        public void setMark(int mark) { this.mark = mark; }
    }

/*******************************************************************/
    class MealAround {
        int mealTime;
        ArrayList<TimeMark> beforeMeal = new ArrayList<>();
        ArrayList<TimeMark> atMeal = new ArrayList<>();
        ArrayList<TimeMark> afterMeal = new ArrayList<>();

        public MealAround(int time) {
            mealTime = time;
        }

        public void addMed(int idMed, byte relation) {
            switch (relation){
                case 0:
                    beforeMeal.add(new TimeMark(mealTime-stInterval,idMed));
                    break;
                case 1:
                    atMeal.add(new TimeMark(mealTime, idMed));
                    break;
                case 2:
                    afterMeal.add(new TimeMark(mealTime+stInterval, idMed));
                    break;
                default: break;
            }
        }
        public void readDataMeal(){
            for (int i = 0; i < beforeMeal.size(); i++) { day.add(beforeMeal.get(i)); }   // лекарства до еды
            day.add(new TimeMark(mealTime, -1));                                    // -1 - значит приём пищи
            for (int i = 0; i < atMeal.size(); i++) { day.add(atMeal.get(i)); }           // лекарства во время еды
            for (int i = 0; i < afterMeal.size(); i++) { day.add(afterMeal.get(i)); }     // лекарства после еды
        }
    }

/***********************************************/
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

/************************************************************/
    public class ShellSort {
        public void sort (List<TimeMark> arr) {
            int increment = arr.size() / 2;
            while (increment >= 1) {
                for (int startIndex = 0; startIndex < increment; startIndex++)
                    insertionSort(arr, startIndex, increment);
                increment = increment / 2;
            }
        }
        private void insertionSort (List<TimeMark> arr, int startIndex, int increment) {
            for (int i = startIndex; i < arr.size() - 1; i = i + increment) {
                for (int j = Math.min(i + increment, arr.size() - 1); j - increment >= 0; j = j - increment) {
                    if (arr.get(j - increment).time > arr.get(j).time) {
                        Collections.swap(arr, j, j - increment);
                    } else break;
                }
            }
        }
    }


}
