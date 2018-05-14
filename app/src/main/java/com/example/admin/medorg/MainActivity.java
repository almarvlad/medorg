package com.example.admin.medorg;

import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.admin.medorg.Fragments.FragmentMeds;
import com.example.admin.medorg.Fragments.FragmentReport;
import com.example.admin.medorg.Fragments.FragmentSettings;
import com.example.admin.medorg.Fragments.FragmentTimetable;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    // РАЗДЕЛЫ ПРИЛОЖЕНИЯ (ФРАГМЕНТЫ)
    FragmentTimetable ftime;
    FragmentMeds fmeds;
    FragmentReport freport;
    FragmentSettings fsettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        FragmentTransaction fTrans = getSupportFragmentManager().beginTransaction();
        switch (item.getItemId()) {
            case R.id.menu_timetable:
                fTrans.replace(R.id.container, ftime);
                break;
            case R.id.menu_meds:
                fTrans.replace(R.id.container, fmeds);
                break;
            case R.id.menu_report:
                fTrans.replace(R.id.container, freport);
                break;
            case R.id.menu_settings:
                //fTrans.replace(R.id.container, fsettings);
                Intent intent = new Intent(this, MedInfo.class);
                startActivity(intent);
                break;
        }
        fTrans.commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START); //после выбора пункта меню шторка закрывается
        return true;
    }
}
