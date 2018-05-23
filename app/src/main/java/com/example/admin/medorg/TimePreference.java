package com.example.admin.medorg;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.preference.DialogPreference;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.util.Log;

import com.example.admin.medorg.R;

import java.util.Calendar;
import java.util.Date;

public class TimePreference extends DialogPreference {
    private int mTime;
    private int mDialogLayoutResId = R.layout.pref_dialog_time;
    private static final String TAG = "TimePreference";

    public TimePreference(Context context) {
        this(context, null);
    }
    public TimePreference(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.dialogPreferenceStyle);
    }
    public TimePreference(Context context, AttributeSet attrs,
                          int defStyleAttr) {
        this(context, attrs, defStyleAttr, defStyleAttr);
    }
    public TimePreference(Context context, AttributeSet attrs,
                          int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public int getTime() {
        Log.d(TAG, "getTime in TimePreference");
        return mTime;
    }
    public void setTime(int time) {
        Log.d(TAG, "setTime in TimePreference");
        mTime = time;
        // Save to Shared Preferences
        persistInt(time);
        String hour = (Integer.toString(time / 60).length()< 2) ? "0" + Integer.toString(time / 60) : Integer.toString(time / 60);
        String min = (Integer.toString(time % 60).length()< 2) ? "0" + Integer.toString(time % 60) : Integer.toString(time % 60);
        Log.d(TAG, hour + ":" + min);
        setSummary(hour + ":" + min);
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        Log.d(TAG, "onGetDefaultValue in TimePreference");
        // Default value from attribute. Fallback value is set to 0.
        return a.getInt(index, 0);
    }
    @Override
    protected void onSetInitialValue(boolean restorePersistedValue,
                                     Object defaultValue) {
        Log.d(TAG, "onSetInitialValue in TimePreference");
        setTime(restorePersistedValue ? getPersistedInt(mTime) : (int) defaultValue);
    }

    @Override
    public int getDialogLayoutResource() {
        Log.d(TAG, " getDialogLayoutResource in TimePreference");
        return mDialogLayoutResId;
    }
}
