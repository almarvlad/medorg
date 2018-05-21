package com.example.admin.medorg.Fragments;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.admin.medorg.R;

public class FragmentSettings extends PreferenceFragmentCompat {
    private static final String TAG = "TimePreference";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        /*
        if(v != null) { // сетаем паддинг у листа настроек
            getListView().setPadding(0, 0, 0, 0);
        }
        */
        return v;
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings, rootKey); // создаём вид настроек из файла с preference screen
    }

    @Override
    public void onDisplayPreferenceDialog(Preference preference) { // вызывается при создании диалогового фрагмента
        // Try if the preference is one of our custom Preferences
        DialogFragment dialogFragment = null;
        if (preference instanceof TimePreference) {
            // Create a new instance of TimePreferenceDialogFragment with the key of the related
            // Preference
            dialogFragment = TimePreferenceDialogFragmentCompat
                    .newInstance(preference.getKey());
        }

        // If it was one of our cutom Preferences, show its dialog
        if (dialogFragment != null) {
            dialogFragment.setTargetFragment(this, 0);
            dialogFragment.show(this.getFragmentManager(),
                    "android.support.v7.preference" +
                            ".PreferenceFragment.DIALOG");
        }
        // Could not be handled here. Try with the super method.
        else {
            super.onDisplayPreferenceDialog(preference);
        }
    }
}
