package ru.markova.admin.medorg;

import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import ru.markova.admin.medorg.Fragments.EditTimetableFragment;
import ru.markova.admin.medorg.Room.AppDatabase;
import ru.markova.admin.medorg.Room.MedicineDao;
import ru.markova.admin.medorg.Room.TimetableCompleteDao;
import ru.markova.admin.medorg.Room.TimetableDao;

public class EditTimetable extends AppCompatActivity {

    private static int NUM_PAGES = 7;
    ViewPager ttpager;
    PagerAdapter pagerAdapter;

    TimetableDao ttDao = AppDatabase.getDatabase(this).ttDao();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_timetable);

        PagerTabStrip pagerTabStrip = (PagerTabStrip) findViewById(R.id.pagerTitleStrip);
        pagerTabStrip.setDrawFullUnderline(false);
        pagerTabStrip.setTabIndicatorColor(getResources().getColor(R.color.colorAccent));

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

    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_timetable_edit, menu);
        return true;
    }
    */

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
//            case R.id.action_save:
//                TimetableMaker ttmaker = TimetableMaker.getInstance(this);
//                new saveNewTimetable(ttmaker, ttDao, ttpager).execute();
//                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    /*
    private static class saveNewTimetable extends AsyncTask<Void, Void, Void> {
        private final MedicineDao mAsyncTaskDao;
        private final TimetableDao ttDao;
        private final TimetableCompleteDao ttCompleteDao;
        private long id;
        private TimetableMaker ttMaker;
        ViewPager ttPager;

        saveNewTimetable(TimetableMaker tm, TimetableDao ttDao, ViewPager pager) {
            ttMaker = tm;
            this.ttDao = ttDao;
            ttPager = pager;
        }
        @Override
        protected Void doInBackground(Void... params) {
            ttDao.deleteWholeTimetable();
            Fragment fragment = ((MyFragmentPagerAdapter) ttPager.getAdapter()).getItem(0);
            EditTimetableFragment exactFragment = (EditTimetableFragment) fragment;
            exactFragment.getI


            String r = mAsyncTaskDao.getById(id).getName();
            mAsyncTaskDao.deleteMed(id);
            mAsyncTaskDao.deleteNoncompatMed(id);
            ttDao.deleteMedFromTimetable(id);
            ttCompleteDao.deleteMedFromTimetableComplete(id);
            return null;
        }
    }
    */
}
