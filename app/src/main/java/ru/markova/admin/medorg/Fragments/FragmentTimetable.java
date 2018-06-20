package ru.markova.admin.medorg.Fragments;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import ru.markova.admin.medorg.EditTimetable;
import ru.markova.admin.medorg.MedAdd;
import ru.markova.admin.medorg.R;

import java.util.Calendar;
import java.util.Date;

import devs.mulham.horizontalcalendar.HorizontalCalendar;
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener;
import devs.mulham.horizontalcalendar.utils.Utils;
import ru.markova.admin.medorg.Room.AppDatabase;
import ru.markova.admin.medorg.Room.TimetableDao;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentTimetable.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentTimetable#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentTimetable extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    ViewGroup rootView;

    private static int NUM_PAGES;
    ViewPager pager;
    PagerAdapter pagerAdapter;

    private static final String TAG = "TT_VIEWPAGER";

    private OnFragmentInteractionListener mListener;
    Calendar startDate, endDate;

    public FragmentTimetable() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentTimetable.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentTimetable newInstance(String param1, String param2) {
        FragmentTimetable fragment = new FragmentTimetable();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        //Менять заголовок
        //((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("График приёма");
        AppDatabase adb = AppDatabase.getDatabase(getContext());
        TimetableDao ttDao = adb.ttDao();
        int t = ttDao.rowsTimetable();
        if (ttDao.rowsTimetable() > 0)
            setHasOptionsMenu(true);
        //
    }

    // "меню" нужно поменять
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(ru.markova.admin.medorg.R.menu.main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == ru.markova.admin.medorg.R.id.action_settings) {
            Log.d("MED_INFO", "Нажата кнопка рассчитать");
            Intent editTimetable = new Intent(getActivity(), EditTimetable.class);
            startActivity(editTimetable);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = (ViewGroup) inflater.inflate(ru.markova.admin.medorg.R.layout.fragment_timetable, container, false);

        /* starts before 1 month from now */
        startDate = Calendar.getInstance();
        startDate = Calendar.getInstance();
        startDate.add(Calendar.MONTH, -1);
        /* ends after 1 month from now */
        endDate = Calendar.getInstance();
        endDate.add(Calendar.MONTH, 1);

        final HorizontalCalendar horizontalCalendar = new HorizontalCalendar.Builder(rootView, ru.markova.admin.medorg.R.id.calendarView)
                .range(startDate, endDate)
                .datesNumberOnScreen(3)
                .configure()
                    .showBottomText(false)
                    .textColor(Color.parseColor("#727272"), Color.BLACK)    // default to (Color.LTGRAY, Color.WHITE).
                .end()
                .defaultSelectedDate(Calendar.getInstance())
                .build();

        horizontalCalendar.refresh();
        NUM_PAGES = Utils.daysBetween(startDate, endDate)+1;

        horizontalCalendar.setCalendarListener(new HorizontalCalendarListener() {
            @Override
            public void onDateSelected(Calendar date, int position) {
                pager.setCurrentItem(position-1);
            }
        });

        // позиции в календаре начинаются с 1
        // номера страниц начинаются с 0
        pager = (ViewPager) rootView.findViewById(ru.markova.admin.medorg.R.id.pager);

        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override // дает номер текущей отображенной страницы
            public void onPageSelected(int position) {
                horizontalCalendar.centerCalendarToPosition(position + 1);
            }

            @Override // дает нам представление о текущем значении скроллера при пролистывании
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }

            @Override // сообщает нам о состоянии, в котором находится скроллер
            // (SCROLL_STATE_IDLE – ничего не скролится,
            // SCROLL_STATE_DRAGGING – пользователь «тащит» страницу,
            // SCROLL_STATE_SETTLING – скроллер долистывает страницу до конца)
            public void onPageScrollStateChanged(int state) { }
        });

        pagerAdapter = new MyFragmentPagerAdapter(getChildFragmentManager(), horizontalCalendar);
        pager.setAdapter(pagerAdapter);
        pager.setCurrentItem(horizontalCalendar.positionOfDate(Calendar.getInstance())-1);

        //Log.d(TAG, "позиция календаря: " + horizontalCalendar.getSelectedDatePosition());
        return rootView;
    }

    private class MyFragmentPagerAdapter extends FragmentStatePagerAdapter {
        HorizontalCalendar horCal;
        public MyFragmentPagerAdapter(FragmentManager fm, HorizontalCalendar hc) {
            super(fm);
            horCal = hc;
        }

        @Override // по номеру страницы нам надо вернуть фрагмент, используем наш метод newInstance
        public Fragment getItem(int position) {
            return DayPageFragment.newInstance(position, horCal.getDateAt(position+1));
        }

        @Override // здесь мы должны возвращать кол-во страниц, используем константу
        public int getCount() {
            return NUM_PAGES;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "Title " + position;
        }

        @Override
        public Parcelable saveState() {
            // Do Nothing
            return null;
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    /*
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }
    */

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return new DayPageFragment();
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: Timetable");
    }

    @Override
    public  void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: Timetable");
    }

    @Override
    public  void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: Timetable");
    }

    @Override
    public  void onStop() {
        super.onStop();
        Log.d(TAG, "onStop : Timetable");
    }
}
