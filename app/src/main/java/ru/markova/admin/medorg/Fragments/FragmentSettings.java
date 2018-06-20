package ru.markova.admin.medorg.Fragments;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.markova.admin.medorg.R;
import ru.markova.admin.medorg.Room.MedicineDao;
import ru.markova.admin.medorg.Room.TimetableCompleteDao;
import ru.markova.admin.medorg.Room.TimetableDao;
import ru.markova.admin.medorg.TimePreference;
import ru.markova.admin.medorg.TimetableMaker;

public class FragmentSettings extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String TAG = "TimePreference";
    SharedPreferences prefs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Менять заголовок
        //((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Настройки");
        setHasOptionsMenu(false);


        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        prefs.registerOnSharedPreferenceChangeListener(this);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        return v;
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(ru.markova.admin.medorg.R.xml.settings, rootKey); // создаём вид настроек из файла с preference screen
    }

    @Override
    public void onDisplayPreferenceDialog(Preference preference) { // вызывается при создании диалогового фрагмента
        DialogFragment dialogFragment = null;
        if (preference instanceof TimePreference) {
            dialogFragment = TimePreferenceDialogFragmentCompat.newInstance(preference.getKey());
        }
        if (dialogFragment != null) {
            dialogFragment.setTargetFragment(this, 0);
            dialogFragment.show(this.getFragmentManager(), "android.support.v7.preference.PreferenceFragment.DIALOG");
        }
        else {
            super.onDisplayPreferenceDialog(preference);
        }
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        TimetableMaker ttMaker = TimetableMaker.getInstance(getContext());
        if (key.equals("day_begin")) {
            ttMaker.setDayBegin(prefs.getInt("day_begin", 360));
        };
        if (key.equals("day_end")) {
            ttMaker.setDayEnd(prefs.getInt("day_end", 1320));
        };
        if (key.equals("meal_count")) {
            ttMaker.setMealCount(Integer.parseInt(prefs.getString("meal_count", "3")));
        };
        new updateTimetableAsyncTask(ttMaker).execute();
    }

    private static class updateTimetableAsyncTask extends AsyncTask<Void, Void, Void> {
        private final TimetableMaker ttMaker;

        updateTimetableAsyncTask(TimetableMaker ttm) {
            ttMaker = ttm;
        }
        @Override
        protected Void doInBackground(Void... params) {
            for (int i = 1; i <= 7; i++) {
                ttMaker.setPriority(Character.forDigit(i, 10));
                ttMaker.setTimeAllMeds();
                ttMaker.sortAndSaveTimetable();
                ttMaker.clearDayTimetable();
            }
            ttMaker.createHistoryTable();
            ttMaker.createNextAlarm();
            return null;
        }
    }
}
