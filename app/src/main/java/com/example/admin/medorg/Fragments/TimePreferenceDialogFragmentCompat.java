package com.example.admin.medorg.Fragments;
import android.content.Context;
import android.support.v7.preference.DialogPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceDialogFragmentCompat;
import android.util.Log;
import android.view.View;
import android.widget.TimePicker;

public class TimePreferenceDialogFragmentCompat extends PreferenceDialogFragmentCompat implements DialogPreference.TargetFragment {
    TimePicker timePicker = null;
    private static final String TAG = "TimePreference";

    @Override
    protected View onCreateDialogView(Context context) {
        timePicker = new TimePicker(context);
        return (timePicker);
    }

    @Override
    protected void onBindDialogView(View v) {
        Log.d(TAG, "onBindDialogView in TimePreferenceDialogFragmentCompat");
        super.onBindDialogView(v);
        timePicker.setIs24HourView(true);
        TimePreference pref = (TimePreference) getPreference(); // получаем уже существующее значение времени
        timePicker.setCurrentHour(pref.hour); // и выставляем его в тайм пикере
        timePicker.setCurrentMinute(pref.minute);
    }

    @Override
    public void onDialogClosed(boolean positiveResult) {
        Log.d(TAG, "onDialogClosed in TimePreferenceDialogFragmentCompat");
        if (positiveResult) {
            TimePreference pref = (TimePreference) getPreference();
            pref.hour = timePicker.getCurrentHour();
            pref.minute = timePicker.getCurrentMinute();

            String value = TimePreference.timeToString(pref.hour, pref.minute);
            if (pref.callChangeListener(value)) pref.persistStringValue(value);
        }
    }

    @Override
    public Preference findPreference(CharSequence charSequence) {
        Log.d(TAG, "findPreference in TimePreferenceDialogFragmentCompat");
        return getPreference();
    }
}
