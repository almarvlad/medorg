package ru.markova.admin.medorg;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import ru.markova.admin.medorg.Fragments.FragmentMeds;
import ru.markova.admin.medorg.Fragments.FragmentReport;
import ru.markova.admin.medorg.Fragments.FragmentTimetable;
import ru.markova.admin.medorg.Fragments.FragmentSettings;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    // РАЗДЕЛЫ ПРИЛОЖЕНИЯ (ФРАГМЕНТЫ)
    FragmentTimetable ftime;
    FragmentMeds fmeds;
    FragmentReport freport;
    FragmentSettings fsettings;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("График приёма");
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ftime = new FragmentTimetable();
        fmeds = new FragmentMeds();
        freport = new FragmentReport();
        fsettings = new FragmentSettings();

        /*
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("day_begin", 360);
        editor.putInt("day_end", 1320);
        editor.putString("meal_count", "3");
        editor.commit();
        */

        // чтобы первоначально отображался экран "График приёма"
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.container, ftime).commit();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        FragmentTransaction fTrans = getSupportFragmentManager().beginTransaction();
        switch (item.getItemId()) {
            case R.id.menu_timetable:
                fTrans.replace(R.id.container, ftime);
                toolbar.setTitle("График приёма");
                break;
            case R.id.menu_meds:
                fTrans.replace(R.id.container, fmeds);
                toolbar.setTitle("Лекарства");
                break;
            case R.id.menu_report:
                //fTrans.replace(R.id.container, freport);
                break;
            case R.id.menu_settings:
                fTrans.replace(R.id.container, fsettings);
                toolbar.setTitle("Настройки");
                break;
        }
        fTrans.commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START); //после выбора пункта меню шторка закрывается
        return true;
    }
}
