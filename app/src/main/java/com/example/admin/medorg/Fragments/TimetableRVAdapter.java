package com.example.admin.medorg.Fragments;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.admin.medorg.R;
import com.example.admin.medorg.Room.UserMedicine;

import java.util.ArrayList;
import java.util.List;

public class TimetableRVAdapter extends RecyclerView.Adapter<TimetableRVAdapter.TimetableViewHolder> {

    private final LayoutInflater mInflater;
    private List<String> exampleStrings;

    TimetableRVAdapter(Context context, ArrayList<String> ex) {
        mInflater = LayoutInflater.from(context);
        exampleStrings = ex;
    }

    @NonNull
    @Override
    public TimetableViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.timetable_element, parent, false);
        return new TimetableViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TimetableViewHolder holder, int position) {
        String t = exampleStrings.get(position);
        holder.bindTime(t);
    }

    @Override
    public int getItemCount() {
        return exampleStrings.size();
    }

    public static class TimetableViewHolder extends RecyclerView.ViewHolder{
        private final TextView elem;
        public TimetableViewHolder(View itemView) {
            super(itemView);
            elem = (TextView) itemView.findViewById(R.id.tt_card_time);
        }

        public void bindTime(String time) {
            elem.setText(time);
        }
    }
}
