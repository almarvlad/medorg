package com.example.admin.medorg.Fragments;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.admin.medorg.R;
import com.example.admin.medorg.Room.AppDatabase;
import com.example.admin.medorg.Room.MedicineDao;
import com.example.admin.medorg.Room.TimetableComplete;
import com.example.admin.medorg.Room.TimetableCompleteDao;
import com.example.admin.medorg.Room.UserMedicine;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TimetableRVAdapter extends RecyclerView.Adapter {

    private final LayoutInflater mInflater;
    private List<DayPageFragment.TimeMarkLong> exampleStrings;
    static List<TimetableComplete> listMedsTake;
    private Context con;

    static final String TAG = "TT_VIEWPAGER";

    AppDatabase adb;
    static MedicineDao medDao;

    // конструктор адаптера
    TimetableRVAdapter(Context context, List<DayPageFragment.TimeMarkLong> ex, List<TimetableComplete> list) {
        mInflater = LayoutInflater.from(context);
        exampleStrings = ex;
        listMedsTake = list;
        con = context;
        adb = AppDatabase.getDatabase(context);
        medDao = adb.Dao();
    }

    @Override
    public int getItemViewType(int position) {
        if (exampleStrings.get(position).getId_med() == -1)
            return 0;
        else return 1;
    }

    @Override // создание элемента в списке recyclerview
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View itemView;
        if (viewType == 1) {
            itemView = mInflater.inflate(R.layout.timetable_element, parent, false);
            vh = new TimetableMedsHolder(itemView);
        } else {
            itemView = mInflater.inflate(R.layout.timetable_meal_element, parent, false);
            vh = new TimetableMealHolder(itemView);
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        Long t = exampleStrings.get(position).getDate_time();
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(t);
        Log.d(TAG, "" + new Date(c.getTimeInMillis()));
        String hour = (c.get(Calendar.HOUR_OF_DAY) < 10) ? ("0" + c.get(Calendar.HOUR_OF_DAY)) : Integer.toString(c.get(Calendar.HOUR_OF_DAY));
        String minute = (c.get(Calendar.MINUTE) < 10) ? ("0" + c.get(Calendar.MINUTE)) : Integer.toString(c.get(Calendar.MINUTE));
        String time = hour + ":" + minute;

        switch (this.getItemViewType(position)) {
            case 1:
                TimetableMedsHolder medHolder = (TimetableMedsHolder) holder;
                medHolder.bindTime(time, t);
                medHolder.setMedsAtTime(mInflater);
                break;
            case 0:
                TimetableMealHolder mealHolder = (TimetableMealHolder) holder;
                mealHolder.bindTime(time);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return exampleStrings.size();
    }

    public static class TimetableMedsHolder extends RecyclerView.ViewHolder{
        private long thisTime;
        private final TextView elem;
        private final LinearLayout llMeds;

        public TimetableMedsHolder(View itemView) {
            super(itemView);
            elem = (TextView) itemView.findViewById(R.id.tt_card_time);
            llMeds = (LinearLayout) itemView.findViewById(R.id.ll_meds);
        }

        public void bindTime(String time, long timeLong) {
            elem.setText(time);
            thisTime = timeLong;
        }

        public void setMedsAtTime(LayoutInflater li) {
            int i = 0;
            //Log.d(TAG, "listMedsTake.size() " + listMedsTake.size());
            while (i < listMedsTake.size() && listMedsTake.get(i).getDateTime() <= thisTime) {
                if (listMedsTake.get(i).getIdMed() > -1 && listMedsTake.get(i).getDateTime()==thisTime){ // если не лекарство
                    UserMedicine med = medDao.getById(listMedsTake.get(i).getIdMed()); //достаём это лекарство из бд
                    Log.d(TAG, new Date(thisTime) + "; название лекарства " + med.getName());
                    View view = li.inflate(R.layout.timetable_med_element, llMeds, false);
                    TextView n = (TextView) view.findViewById(R.id.card_med_name);
                    TextView instr = (TextView) view.findViewById(R.id.card_med_instructions);
                    String s = med.getDose() + " " + med.getDoseForm();
                    switch (med.getInstruct()) {
                        case 0:
                            s += ", до еды";
                            break;
                        case 1:
                            s += ", во время еды";
                            break;
                        case 2:
                            s += ", после еды";
                            break;
                        default: break;
                    }
                    n.setText(med.getName());
                    instr.setText(s);
                    llMeds.addView(view);
                }
                i++;
            }
        }
    }


    public static class TimetableMealHolder extends RecyclerView.ViewHolder {
        private final TextView mealTime;
        public TimetableMealHolder(View itemView) {
            super(itemView);
            mealTime = (TextView) itemView.findViewById(R.id.meal_time);
        }

        public void bindTime(String time) {
            String res = time + " приём пищи";
            mealTime.setText(res);
        }
    }
}
