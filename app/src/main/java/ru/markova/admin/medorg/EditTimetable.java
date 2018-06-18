package ru.markova.admin.medorg;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import ru.markova.admin.medorg.Fragments.EditTimetableFragment;

public class EditTimetable extends AppCompatActivity {

    private static int NUM_PAGES = 7;
    ViewPager ttpager;
    PagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_timetable);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true); // отображение кнопки "назад"
        actionBar.setTitle("Редактировать расписание");

        ttpager = (ViewPager) findViewById(R.id.timetable_pager);
        pagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
        ttpager.setAdapter(pagerAdapter);
    }

    private class MyFragmentPagerAdapter extends FragmentPagerAdapter {

        public MyFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return EditTimetableFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
        @Override
        public CharSequence getPageTitle(int position) {
            String[] weekdays_list = getResources().getStringArray(R.array.weekdays); // образуем массив из строковых ресурсов
            return weekdays_list[position];
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }
}
