package com.example.admin.medorg.Fragments;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.admin.medorg.R;
import com.example.admin.medorg.Room.MedicineViewModel;
import com.example.admin.medorg.Room.UserMedicine;

import java.util.List;

public class MedicineListAdapter extends RecyclerView.Adapter<MedicineListAdapter.MedicineViewHolder> {

    class MedicineViewHolder extends RecyclerView.ViewHolder {
        private final TextView medItemView;

        private MedicineViewHolder(View itemView) {
            super(itemView);
            medItemView = itemView.findViewById(R.id.medname);
        }
    }

    private final LayoutInflater mInflater;
    private List<UserMedicine> mMeds; // Cached copy of meds

    MedicineListAdapter(Context context) { mInflater = LayoutInflater.from(context); }

    @Override
    public MedicineViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.med_row, parent, false);
        return new MedicineViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MedicineViewHolder holder, int position) {
        if (mMeds != null) {
            UserMedicine current = mMeds.get(position);
            holder.medItemView.setText(current.getName());
        } else {
            // Covers the case of data not being ready yet.
            holder.medItemView.setText("No Word");
        }
    }

    void setWords(List<UserMedicine> meds){
        mMeds = meds;
        notifyDataSetChanged();
    }

    // getItemCount() is called many times, and when it is first called,
    // mWords has not been updated (means initially, it's null, and we can't return null).
    @Override
    public int getItemCount() {
        if (mMeds != null)
            return mMeds.size();
        else return 0;
    }
}
