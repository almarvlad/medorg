package com.example.admin.medorg.Fragments;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.admin.medorg.R;
import com.example.admin.medorg.Room.UserMedicine;

import java.util.ArrayList;
import java.util.List;

public class TimetableRVAdapter extends RecyclerView.Adapter<TimetableRVAdapter.TimetableViewHolder> {

    private final LayoutInflater mInflater;
    private List<String> exampleStrings;
    private Context con;

    // конструктор адаптера
    TimetableRVAdapter(Context context, ArrayList<String> ex) {
        mInflater = LayoutInflater.from(context);
        exampleStrings = ex;
        con = context;
    }

    @NonNull
    @Override // создание элемента в списке recyclerview
    public TimetableViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.timetable_element, parent, false);
        return new TimetableViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TimetableViewHolder holder, int position) {
        String t = exampleStrings.get(position);
        holder.bindTime(t);
        holder.setMedsAtTime(mInflater);
    }

    @Override
    public int getItemCount() {
        return exampleStrings.size();
    }

    public static class TimetableViewHolder extends RecyclerView.ViewHolder{
        private final TextView elem;
        private final LinearLayout llMeds;
        public TimetableViewHolder(View itemView) {
            super(itemView);
            elem = (TextView) itemView.findViewById(R.id.tt_card_time);
            llMeds = (LinearLayout) itemView.findViewById(R.id.ll_meds);
        }

        public void bindTime(String time) {
            elem.setText(time);
        }

        public void setMedsAtTime(LayoutInflater li) {
            String[] meds = new String[] {"Кагоцел", "Парацетамол", "Волокардин"};
            for (int i = 0; i < 3; i++) {
                View view = li.inflate(R.layout.timetable_med_element, llMeds, false);
                TextView n = (TextView) view.findViewById(R.id.card_med_name);
                n.setText(meds[i]);
                llMeds.addView(view);
            }

        }
    }
}
