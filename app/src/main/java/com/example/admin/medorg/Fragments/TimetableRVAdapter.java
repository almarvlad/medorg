package com.example.admin.medorg.Fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.admin.medorg.MainActivity;
import com.example.admin.medorg.MedEdit;
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

import devs.mulham.horizontalcalendar.utils.Utils;

import static java.lang.Math.abs;

public class TimetableRVAdapter extends RecyclerView.Adapter implements TakeMedDialogFragment.Listener {

    private final LayoutInflater mInflater;
    private List<DayPageFragment.TimeMarkLong> exampleStrings;
    private static Context con;
    private Calendar d1, d2;

    static final String TAG = "TT_VIEWPAGER";

    AppDatabase adb;
    static MedicineDao medDao;
    static TimetableCompleteDao ttCompleteDao;

    // конструктор адаптера
    TimetableRVAdapter(Context context, List<DayPageFragment.TimeMarkLong> ex, Calendar date1, Calendar date2) {
        mInflater = LayoutInflater.from(context);
        exampleStrings = ex;
        d1 = date1;
        d2 = date2;
        con = context;
        adb = AppDatabase.getDatabase(context);
        medDao = adb.Dao();
        ttCompleteDao = adb.ttCompleteDao();
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

    @Override
    public void returnData(int result) {
        if (result == 1) {
            notifyDataSetChanged();
        }
    }

    public class TimetableMedsHolder extends RecyclerView.ViewHolder{
        private long thisTime;
        private String time;
        private final TextView elem;
        private final LinearLayout llMeds;
        private final ImageButton cancelAll;
        private final ImageButton doneAll;

        public TimetableMedsHolder(View itemView) {
            super(itemView);
            elem = (TextView) itemView.findViewById(R.id.tt_card_time);
            llMeds = (LinearLayout) itemView.findViewById(R.id.ll_meds);
            cancelAll = (ImageButton) itemView.findViewById(R.id.cancel_all);
            doneAll = (ImageButton) itemView.findViewById(R.id.done_all);

        }

        public void bindTime(String time, long timeLong) {
            this.time = time;
            elem.setText(time);
            thisTime = timeLong;

            if (diffDays()) {
                cancelAll.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ttCompleteDao.updateAllatTime(0, thisTime);
                        notifyItemChanged(getAdapterPosition());
                    }
                });
                doneAll.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ttCompleteDao.updateAllatTime(1, thisTime);
                        notifyItemChanged(getAdapterPosition());
                    }
                });
            } else {
                cancelAll.setVisibility(View.INVISIBLE);
                doneAll.setVisibility(View.INVISIBLE);
            }
        }

        public boolean diffDays() {
            Calendar day1 = Calendar.getInstance();
            day1.setTimeInMillis(thisTime);
            Calendar day2 = Calendar.getInstance();
            if (abs(Utils.daysBetween(day1, day2)) <= 1)
                return true;
            else return false;
        }

        public void setMedsAtTime(final LayoutInflater li) {
            llMeds.removeAllViews();
            List<TimetableComplete> listMedsTake = new ArrayList<TimetableComplete>();
            listMedsTake = ttCompleteDao.selectMedsAtTime(thisTime);
            for (int i = 0; i < listMedsTake.size(); i++) {
                final UserMedicine med = medDao.getById(listMedsTake.get(i).getIdMed()); //достаём это лекарство из бд
                View view = li.inflate(R.layout.timetable_med_element, llMeds, false); // получаем лэйаут, который будем всавлять в список

                switch (listMedsTake.get(i).getCompletion()) {
                    case 0:
                        view.setBackgroundColor(Color.parseColor("#d5d5d5"));
                        break;
                    case 1:
                        view.setBackgroundColor(Color.parseColor("#c5e6ad"));
                        break;
                    default:
                        break;
                }

                TextView n = (TextView) view.findViewById(R.id.card_med_name);
                n.setText(med.getName());

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
                    default:
                        break;
                }
                instr.setText(s);

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (diffDays()) {
                            MainActivity myActivity = (MainActivity) con;
                            TakeMedDialogFragment takeMedDialog = TakeMedDialogFragment.newInstance(med, thisTime, time);
                            //takeMedDialog.show(myActivity.getSupportFragmentManager(), "tag");
                            takeMedDialog.setListener(TimetableRVAdapter.this);
                            takeMedDialog.show(myActivity.getSupportFragmentManager(), "tag");
                        }
                    }
                });
                llMeds.addView(view);
            }
        }
    }

    public interface OnClickTakeMedDialogListener{
        void onClick();
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
