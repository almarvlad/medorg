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

public class TimetableRVAdapter extends RecyclerView.Adapter<TimetableRVAdapter.TimetableViewHolder> {

    private final LayoutInflater mInflater;
    private List<Long> exampleStrings;
    static List<TimetableComplete> listMedsTake;
    private Context con;

    static final String TAG = "TT_VIEWPAGER";

    AppDatabase adb;
    static MedicineDao medDao;

    // конструктор адаптера
    TimetableRVAdapter(Context context, List<Long> ex, List<TimetableComplete> list) {
        mInflater = LayoutInflater.from(context);
        exampleStrings = ex;
        listMedsTake = list;
        con = context;
        adb = AppDatabase.getDatabase(context);
        medDao = adb.Dao();
    }

    @NonNull
    @Override // создание элемента в списке recyclerview
    public TimetableViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.timetable_element, parent, false);
        return new TimetableViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TimetableViewHolder holder, int position) {
        Long t = exampleStrings.get(position);
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(t);
        Log.d(TAG, "" + new Date(c.getTimeInMillis()));
        String hour = (c.get(Calendar.HOUR_OF_DAY) < 10) ? ("0" + c.get(Calendar.HOUR_OF_DAY)) : Integer.toString(c.get(Calendar.HOUR_OF_DAY));
        String minute = (c.get(Calendar.MINUTE) < 10) ? ("0" + c.get(Calendar.MINUTE)) : Integer.toString(c.get(Calendar.MINUTE));
        String time = hour + ":" + minute;
        holder.bindTime(time, t);
        holder.setMedsAtTime(mInflater);
    }

    @Override
    public int getItemCount() {
        return exampleStrings.size();
    }

    public static class TimetableViewHolder extends RecyclerView.ViewHolder{
        private long thisTime;
        private final TextView elem;
        private final LinearLayout llMeds;
        public TimetableViewHolder(View itemView) {
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
                    n.setText(med.getName());
                    llMeds.addView(view);
                }
                i++;
            }


            /*
            String[] meds = new String[] {"Кагоцел", "Парацетамол", "Волокардин"};
            for (int i = 0; i < 3; i++) {
                View view = li.inflate(R.layout.timetable_med_element, llMeds, false);
                TextView n = (TextView) view.findViewById(R.id.card_med_name);
                n.setText(meds[i]);
                llMeds.addView(view);
            }
            */

        }
    }
}
