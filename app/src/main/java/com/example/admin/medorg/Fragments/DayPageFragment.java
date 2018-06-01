package com.example.admin.medorg.Fragments;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.admin.medorg.MedInfo;
import com.example.admin.medorg.R;
import com.example.admin.medorg.Room.AppDatabase;
import com.example.admin.medorg.Room.MedicineDao;
import com.example.admin.medorg.Room.TimetableComplete;
import com.example.admin.medorg.Room.TimetableCompleteDao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;

public class DayPageFragment extends Fragment {
    static final String ARGUMENT_PAGE_NUMBER = "arg_page_number";
    static final String SAVE_PAGE_NUMBER = "save_page_number";
    static final String DATE_IN_MILLIS = "save_page_number";

    private static final String TAG = "TT_VIEWPAGER";
    private static final String tt = "TT_RW";

    AppDatabase adb;
    TimetableCompleteDao ttCompleteDao;

    int pageNumber;

    static DayPageFragment newInstance(int page, Calendar d) {
        DayPageFragment DayPageFragment = new DayPageFragment();
        Bundle arguments = new Bundle();
        arguments.putInt(ARGUMENT_PAGE_NUMBER, page);
        arguments.putLong(DATE_IN_MILLIS, d.getTimeInMillis()); // записываем дату для данного фрагмента в аргументы
        DayPageFragment.setArguments(arguments);
        return DayPageFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageNumber = getArguments().getInt(ARGUMENT_PAGE_NUMBER);
        //Log.d(TAG, "onCreate: " + pageNumber);

        int savedPageNumber = -1;
        if (savedInstanceState != null) {
            savedPageNumber = savedInstanceState.getInt(SAVE_PAGE_NUMBER);
        }
        //Log.d(TAG, "savedPageNumber = " + savedPageNumber);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.timetable_day, null);

        TextView title = (TextView) view.findViewById(R.id.no_med_takes);

        View cardview = inflater.inflate(R.layout.timetable_element, null);

        adb = AppDatabase.getDatabase(getContext());
        ttCompleteDao = adb.ttCompleteDao();

        RecyclerView recyclerView = view.findViewById(R.id.timetable_page); // наш список cardview для графика приёма
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        Calendar date_one = Calendar.getInstance();
        date_one.setTimeInMillis(getArguments().getLong(DATE_IN_MILLIS));
        Calendar date_two = new GregorianCalendar(date_one.get(Calendar.YEAR), date_one.get(Calendar.MONTH), date_one.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        date_two.add(Calendar.DATE, 1);
        Date d1 = new Date(date_one.getTimeInMillis());
        Date d2 = new Date(date_two.getTimeInMillis());
        List<TimeMarkLong> listTime = new ArrayList<TimeMarkLong>();
        listTime = ttCompleteDao.getDistinctTimeList(date_one.getTimeInMillis(), date_two.getTimeInMillis());
        List<TimetableComplete> listMedsTime = ttCompleteDao.getTimetableByDate(date_one.getTimeInMillis(), date_two.getTimeInMillis());

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        int mealCount = Integer.parseInt(prefs.getString("meal_count", "3"));


        if (listTime.size() > mealCount) {
            title.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            final TimetableRVAdapter adapter = new TimetableRVAdapter(getContext(), listTime, listMedsTime);
            recyclerView.setAdapter(adapter);
        }
        return view;
    }

    public static class TimeMarkLong {
        private long date_time;
        private int id_med; // id лекарства

        public TimeMarkLong(long date_time, int id_med) {
            this.date_time = date_time;
            this.id_med = id_med;
        }

        public long getDate_time() {
            return date_time;
        }

        public void setDate_time(long date_time) {
            this.date_time = date_time;
        }

        public int getId_med() {
            return id_med;
        }

        public void setId_med(int id_med) {
            this.id_med = id_med;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SAVE_PAGE_NUMBER, pageNumber);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //Log.d(TAG, "onDestroy: " + pageNumber);
    }
}
