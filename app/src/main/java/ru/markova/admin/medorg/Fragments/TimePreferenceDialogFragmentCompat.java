package ru.markova.admin.medorg.Fragments;
import android.os.Bundle;
import android.support.v7.preference.DialogPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceDialogFragmentCompat;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.TimePicker;

import ru.markova.admin.medorg.R;
import ru.markova.admin.medorg.TimePreference;

import ru.markova.admin.medorg.TimePreference;

public class TimePreferenceDialogFragmentCompat extends PreferenceDialogFragmentCompat implements DialogPreference.TargetFragment {
    TimePicker mTimePicker = null;
    private static final String TAG = "TimePreference";

    public static TimePreferenceDialogFragmentCompat newInstance(String key) {
        final TimePreferenceDialogFragmentCompat fragment = new TimePreferenceDialogFragmentCompat();
        final Bundle b = new Bundle(1);
        b.putString(ARG_KEY, key);
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);

        mTimePicker = (TimePicker) view.findViewById(ru.markova.admin.medorg.R.id.edit);
        // если нет установщика времени
        if (mTimePicker == null) {
            throw new IllegalStateException("Dialog view must contain a TimePicker with id 'edit'");
        }
        // получаем время из текущей настройки
        Integer minutesAM = null;
        DialogPreference preference = getPreference();
        if (preference instanceof TimePreference) {
            minutesAM = ((TimePreference) preference).getTime();
        }
        // и ставим его в тайм пикер
        if (minutesAM != null) {
            int hours = minutesAM / 60;
            int minutes = minutesAM % 60;
            boolean is24hour = DateFormat.is24HourFormat(getContext());

            mTimePicker.setIs24HourView(is24hour);
            mTimePicker.setCurrentHour(hours);
            mTimePicker.setCurrentMinute(minutes);
        }
    }

    @Override
    public void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            int hours = mTimePicker.getCurrentHour();
            int minutes = mTimePicker.getCurrentMinute();
            int minutesAM = (hours * 60) + minutes;

            DialogPreference preference = getPreference();
            if (preference instanceof TimePreference) {
                TimePreference timePreference = ((TimePreference) preference);
                // This allows the client to ignore the user value.
                if (timePreference.callChangeListener(minutesAM)) {
                    timePreference.setTime(minutesAM);
                }
            }
        }
    }

    @Override
    public Preference findPreference(CharSequence key) {
        return null;
    }
}
