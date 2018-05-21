package com.example.admin.medorg.Fragments;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.preference.DialogPreference;
import android.util.AttributeSet;
import android.util.Log;

public class TimePreference extends DialogPreference {
    private static final String TAG = "TimePreference";
    public int hour = 0;
    public int minute = 0;


    public static int parseHour(String value) { //выделяем час дня из строки
        try {
            String[] time = value.split(":");
            return (Integer.parseInt(time[0]));
        }
        catch (Exception e) {
            return 0;
        }
    }

    public static int parseMinute(String value) { // выделяем минуты из строки
        try {
            String[] time = value.split(":");
            return (Integer.parseInt(time[1]));
        }
        catch (Exception e) {
            return 0;
        }
    }

    public static String timeToString(int h, int m) {
        return String.format("%02d", h) + ":" + String.format("%02d", m);
    }

    public TimePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        Log.d(TAG, "onGetDefaultValue in TimePreference");
        Log.d(TAG, "index in TypedArray: " + index);
        return a.getString(index);
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        Log.d(TAG, "onSetInitialValue in TimePreference");
        String value;
        if (restoreValue) {
            if (defaultValue == null)
                value = getPersistedString("00:00");
            else value = getPersistedString(defaultValue.toString());
        }
        else {
            value = defaultValue.toString();
        }
        hour = parseHour(value);
        minute = parseMinute(value);
        Log.d(TAG, hour + ":" + minute);
    }

    public void persistStringValue(String value) {
        Log.d(TAG, "persistStringValue in TimePreference");
        persistString(value);
    }
}
