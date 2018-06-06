package com.example.admin.medorg.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.admin.medorg.MainActivity;
import com.example.admin.medorg.R;
import com.example.admin.medorg.Room.AppDatabase;
import com.example.admin.medorg.Room.MedicineDao;
import com.example.admin.medorg.Room.TimetableCompleteDao;
import com.example.admin.medorg.Room.UserMedicine;

import java.util.Calendar;

import fr.tvbarthel.lib.blurdialogfragment.SupportBlurDialogFragment;

public class TakeMedDialogFragment extends SupportBlurDialogFragment {
    private static Calendar time = Calendar.getInstance();
    private static String timeStr;
    private static UserMedicine umed;
    private static int position;

    Button skip, take;

    static AppDatabase adb;
    static TimetableCompleteDao ttCompleteDao;

    static RecyclerView recyclerView;

    private Listener mListener;

    public void setListener(Listener listener) {
        mListener = listener;
    }

    static interface Listener {
        void returnData(int result);
    }

    @Override
    public void onActivityCreated (Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.returnData(1);
                    ttCompleteDao.updateOneatTime(0, time.getTimeInMillis(), umed.getID());
                }
                dismiss();
            }
        });

        take.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.returnData(1);
                    ttCompleteDao.updateOneatTime(1, time.getTimeInMillis(), umed.getID());
                }
                dismiss();
            }
        });
    }

    public static TakeMedDialogFragment newInstance(UserMedicine med, long datetime, String t) {
        TakeMedDialogFragment f = new TakeMedDialogFragment();
        // Supply num input as an argument.
        /*
        Bundle args = new Bundle();
        args.put("num", num);
        f.setArguments(args);
        */
        time.setTimeInMillis(datetime);
        timeStr = t;
        umed = med;
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adb = AppDatabase.getDatabase(getActivity());
        ttCompleteDao = adb.ttCompleteDao();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.take_med, container, false);
        skip = (Button) v.findViewById(R.id.btn_skip);
        take = (Button) v.findViewById(R.id.btn_take);

        TextView timeTV = (TextView) v.findViewById(R.id.time);
        timeTV.setText(timeStr);
        TextView nameTV = (TextView) v.findViewById(R.id.med_name);
        nameTV.setText(umed.getName());
        TextView instrTV = (TextView) v.findViewById(R.id.med_instructions);
        String s = umed.getDose() + " " + umed.getDoseForm();
        switch (umed.getInstruct()) {
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
        instrTV.setText(s);
        return v;
    }

    @Override
    protected float getDownScaleFactor() {
        // Allow to customize the down scale factor.
        return 9;
    }

    @Override
    protected int getBlurRadius() {
        // Allow to customize the blur radius factor.
        return 8;
    }

}
