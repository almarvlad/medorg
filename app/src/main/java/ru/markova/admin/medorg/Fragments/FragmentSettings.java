package ru.markova.admin.medorg.Fragments;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.markova.admin.medorg.R;
import ru.markova.admin.medorg.TimePreference;

import ru.markova.admin.medorg.TimePreference;

public class FragmentSettings extends PreferenceFragmentCompat {
    private static final String TAG = "TimePreference";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Менять заголовок
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Настройки");
        setHasOptionsMenu(false);
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
}
