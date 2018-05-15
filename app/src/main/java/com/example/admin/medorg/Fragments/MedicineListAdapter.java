package com.example.admin.medorg.Fragments;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.admin.medorg.R;
import com.example.admin.medorg.Room.UserMedicine;
import com.example.admin.medorg.MedInfo;

import java.util.List;

public class MedicineListAdapter extends RecyclerView.Adapter<MedicineListAdapter.MedicineViewHolder> {

    public static class MedicineViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener  {
        private final TextView medItemView; // элемент-строка в recyclerview

        public MedicineViewHolder(View v) {
            super(v);
            medItemView = (TextView) v.findViewById(R.id.medname);
            medItemView.setOnClickListener(this);   // вешаем обработчик клика на элемент списка
                                                    // чтобы вызывалась страница с информацией о данной лекарстве
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition(); // позиция элемента в списке
            if (position != RecyclerView.NO_POSITION) {
                Context context = v.getContext(); // среда, в которой находится recyclerview - наш фрагмент
                Intent infActivity = new Intent(context, MedInfo.class);
                infActivity.putExtra("id", (long)medItemView.getTag());
                Log.d("TAG", "id вызываемого лекарства" + Long.toString((long)medItemView.getTag()));
                context.startActivity(infActivity);
            }
        }
    }

    private final LayoutInflater mInflater;
    private List<UserMedicine> mMeds; // Cached copy of meds

    MedicineListAdapter(Context context) { mInflater = LayoutInflater.from(context); }

    @Override
    public MedicineViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // получаем элемент списка и вешаем на него холдер
        View itemView = mInflater.inflate(R.layout.med_row, parent, false);
        return new MedicineViewHolder(itemView);
    }

//    This method internally calls onBindViewHolder(ViewHolder, int) to update the RecyclerView.
//    ViewHolder contents with the item at the given position and also sets up some private fields to be used by RecyclerView.
    @Override
    public void onBindViewHolder(MedicineViewHolder holder, int position) {
        if (mMeds != null) {
            UserMedicine current = mMeds.get(position);
            holder.medItemView.setText(current.getName());
            holder.medItemView.setTag(current.getID());
        } else {
            // Covers the case of data not being ready yet.
            holder.medItemView.setText("No Word");
        }
    }

    void setWords(List<UserMedicine> meds){
        mMeds = meds;
        notifyDataSetChanged(); // уведомлять слушателей об изменении данных
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
