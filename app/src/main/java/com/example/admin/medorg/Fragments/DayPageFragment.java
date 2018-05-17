package com.example.admin.medorg.Fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.admin.medorg.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DayPageFragment extends Fragment {
    static final String ARGUMENT_PAGE_NUMBER = "arg_page_number";
    static final String SAVE_PAGE_NUMBER = "save_page_number";

    private static final String TAG = "TT_VIEWPAGER";
    private static final String tt = "TT_RW";

    int pageNumber;

    static DayPageFragment newInstance(int page) {
        DayPageFragment DayPageFragment = new DayPageFragment();
        Bundle arguments = new Bundle();
        arguments.putInt(ARGUMENT_PAGE_NUMBER, page);
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.timetable_day, null);

        RecyclerView recyclerView = view.findViewById(R.id.timetable_page); // наш список cardview для графика приёма
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        ArrayList<String> exampleTime = new ArrayList<String>(3);
        exampleTime.add("12:30");
        exampleTime.add("13:30");
        exampleTime.add("18:30");

        final TimetableRVAdapter adapter = new TimetableRVAdapter(getContext(), exampleTime);
        recyclerView.setAdapter(adapter);


        return view;
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
