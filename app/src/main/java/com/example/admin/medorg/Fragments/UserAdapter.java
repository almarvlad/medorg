package com.example.admin.medorg.Fragments;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.admin.medorg.R;
import com.example.admin.medorg.Room.UserMedicine;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

class UserAdapter extends android.support.v7.widget.RecyclerView.Adapter<UserAdapter.ViewHolder> {
    List<UserMedicine> meds;
    public UserAdapter(List<UserMedicine> meds) {
        this.meds = meds;
    }

    @NonNull
    @Override
    public UserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.med_row, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.ViewHolder holder, int position) {
        holder.nameMed.setText(meds.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return meds.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView nameMed;

        public ViewHolder(View itemView) {
            super(itemView);
            nameMed = itemView.findViewById(R.id.medname);
        }
    }
}
