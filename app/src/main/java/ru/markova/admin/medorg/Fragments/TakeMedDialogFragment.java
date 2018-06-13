package ru.markova.admin.medorg.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import ru.markova.admin.medorg.MainActivity;
import ru.markova.admin.medorg.R;
import ru.markova.admin.medorg.Room.AppDatabase;
import ru.markova.admin.medorg.Room.MedicineDao;
import ru.markova.admin.medorg.Room.TimetableCompleteDao;
import ru.markova.admin.medorg.Room.UserMedicine;

import java.util.Calendar;
import java.util.Locale;

import fr.tvbarthel.lib.blurdialogfragment.SupportBlurDialogFragment;
import ru.markova.admin.medorg.Room.AppDatabase;

public class TakeMedDialogFragment extends SupportBlurDialogFragment implements TextToSpeech.OnInitListener {
    private static Calendar time = Calendar.getInstance();
    private static String timeStr;
    private static UserMedicine umed;

    private TextToSpeech mTTS;
    private ImageButton play;

    Button skip, take;

    static AppDatabase adb;
    static TimetableCompleteDao ttCompleteDao;

    private Listener mListener;

    public void setListener(Listener listener) {
        mListener = listener;
    }

    @Override
    public void onInit(int status) {
        // TODO Auto-generated method stub
        if (status == TextToSpeech.SUCCESS) {
            Locale locale = new Locale("ru");
            int result = mTTS.setLanguage(locale);
            //int result = mTTS.setLanguage(Locale.getDefault());
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "Извините, этот язык не поддерживается");
            } else { play.setEnabled(true); }
        } else { Log.e("TTS", "Ошибка!"); }
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
        mTTS = new TextToSpeech(getActivity(), this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(ru.markova.admin.medorg.R.layout.take_med, container, false);
        skip = (Button) v.findViewById(ru.markova.admin.medorg.R.id.btn_skip);
        take = (Button) v.findViewById(ru.markova.admin.medorg.R.id.btn_take);
        play = (ImageButton) v.findViewById(ru.markova.admin.medorg.R.id.play_audio);

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = umed.getName();
                mTTS.speak(text, TextToSpeech.QUEUE_FLUSH, null);
            }
        });

        TextView timeTV = (TextView) v.findViewById(ru.markova.admin.medorg.R.id.time);
        timeTV.setText(timeStr);
        TextView nameTV = (TextView) v.findViewById(ru.markova.admin.medorg.R.id.med_name);
        nameTV.setText(umed.getName());
        TextView instrTV = (TextView) v.findViewById(ru.markova.admin.medorg.R.id.med_instructions);
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
