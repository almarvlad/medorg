package ru.markova.admin.medorg;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import ru.markova.admin.medorg.Room.AppDatabase;
import ru.markova.admin.medorg.Room.MedicineDao;
import ru.markova.admin.medorg.Room.MedicineViewModel;
import ru.markova.admin.medorg.Room.NonCompatMeds;
import ru.markova.admin.medorg.Room.TimetableCompleteDao;
import ru.markova.admin.medorg.Room.UserMedicine;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import ru.markova.admin.medorg.Room.AppDatabase;
import ru.markova.admin.medorg.Room.MedicineDao;
import ru.markova.admin.medorg.Room.MedicineViewModel;
import ru.markova.admin.medorg.Room.NonCompatMeds;

public class MedInfo extends AppCompatActivity implements TextToSpeech.OnInitListener{
    private long medID;
    private UserMedicine med;
    private List<NonCompatMeds> nc;
    public String[] weekdays_list;

    AppDatabase adb;
    MedicineDao dao;
    TimetableCompleteDao ttCompleteDao;


    private TextToSpeech mTTS;
    private FloatingActionButton fab;
    private Button editMed;
    private Button stopMed;

    private MedicineViewModel mMedicineViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_med_info);
        mTTS = new TextToSpeech(this, this);
        weekdays_list = getResources().getStringArray(R.array.weekdays);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("");

        Intent intent = getIntent();
        medID = intent.getLongExtra("id", -1);
        Log.d("MED_INFO", "Activity got an ID: " + Long.toString(medID));

        adb = AppDatabase.getDatabase(MedInfo.this);
        dao = adb.Dao();
        ttCompleteDao = adb.ttCompleteDao();
        med = dao.getById(medID);
        nc = dao.getNoncompat(medID);

        final AppBarLayout header = (AppBarLayout) findViewById(R.id.app_bar);
        stopMed = (Button) findViewById(R.id.stopmed);
        if (!med.isActive()) {
            header.setBackgroundColor(getResources().getColor(R.color.colorStoppedMed));
            stopMed.setText("Возобновить");
        }

        mMedicineViewModel = ViewModelProviders.of(this).get(MedicineViewModel.class);

        mMedicineViewModel.getCurrentMed((int)medID).observe(this, new Observer<UserMedicine>() {
            @Override
            public void onChanged(@Nullable final UserMedicine med) {
                //adapter.setWords(words); // обновить кэш-копию слов в репозитории
                if (med != null)
                    setMedInfo(med, nc);
            }
        });

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = med.getName();
                mTTS.speak(text, TextToSpeech.QUEUE_FLUSH, null);
            }
        });

        editMed = (Button) findViewById(R.id.editmed);
        editMed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editMedActivity = new Intent(getApplication(), MedAdd.class);
                editMedActivity.putExtra("id", medID);
                startActivity(editMedActivity);
            }
        });


        stopMed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (med.isActive()) {
                    header.setBackgroundColor(getResources().getColor(R.color.colorStoppedMed));
                    stopMed.setText("Возобновить");
                    med.setActive(false);
                    dao.setStoppedMed(medID);
                    ttCompleteDao.stopMed(medID, Calendar.getInstance().getTimeInMillis());
                } else {
                    header.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    stopMed.setText("Остановить");
                    dao.setActiveMed(medID);
                    med.setActive(true);
                    new MedAdd.updateMedAsyncTask(med, TimetableMaker.getInstance(getBaseContext())).execute();
                    //ttCompleteDao.stopMed(medID, Calendar.getInstance().getTimeInMillis());
                }

                //header.setContentScrimColor(getResources().getColor(R.color.colorStoppedMed));
                //header.setContentScrim(new ColorDrawable(getResources().getColor(R.color.colorStoppedMed)));
                //actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorStoppedMed)));
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_med_info, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_delete:
                Log.d("MED_INFO", "Нажата кнопка delete");
                medDelete(medID);
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        finish();
    }

    public void setMedInfo(UserMedicine med, List<NonCompatMeds> nc) {
        TextView textView;
        // название лекарства
        textView = (TextView) findViewById(R.id.info_med_title);
        textView.setText(med.getName());
        // кол-во приёмов в сутки
        textView = (TextView) findViewById(R.id.info_frequency);
        textView.setText(setIntervalInfo(med.isTimeType(), med.getTimePer()));
        // дни приёма
        textView = (TextView) findViewById(R.id.info_days);
        textView.setText(setDaysInfo(med.getWeekdays()));
        // дозировка
        textView = (TextView) findViewById(R.id.info_dose);
        textView.setText(med.getDose() + " " + med.getDoseForm());

        View i = findViewById(R.id.info_block_instruct);
        if (med.getInstruct()==3 && med.getAddInstruct().equals("")) // если не важна сочетаемость с пищей и нет доп инструкций
            i.setVisibility(View.GONE);                         // тогда убираем блок с инструкциями
        else {
            TextView textViewFood = (TextView) findViewById(R.id.info_food_instruct);
            // если есть сочетаемость с пищей
            if (med.getInstruct()<3) {
                textViewFood.setText(setFoodInstructInfo(med.getInstruct()));
            }
            else textViewFood.setVisibility(View.GONE);
            // если есть доп инструкции
            textView = (TextView) findViewById(R.id.info_add_insctruct);
            if (med.getAddInstruct().equals(""))
                textView.setVisibility(View.GONE);
            else {
                textView.setText(med.getAddInstruct());
            }
        }

        Log.d("MED_INFO", "кол-во несовместимых лекарств: " + nc.size());
        if (nc.size() == 0) // если нет несовмесимых лекарств (а мы запросили этот список ранее в onCreate)
            findViewById(R.id.info_block_noncompat).setVisibility(View.GONE);
        else {
            textView = (TextView) findViewById(R.id.info_nc_meds);
            textView.setText(setInfoNoncompat(nc));
        }
    }

    // false - частота (Н раз(а) в день)
    // true - интервалы (каждые H часов)
    public String setIntervalInfo (boolean type, int per) {
        String res;
        if (!type) {
            if ((per > 1)&&(per < 5))
                res = per + " раза в день";
            else res = per + " раз в день";
        } else
            if (per == 1)
                res = "каждый час";
            else if ((per > 1)&&(per < 5))
                    res = "каждые " + per + " часа";
                else res = "каждые " + per + " часов";
        return res;
    }

    public String setDaysInfo(String weekdays) {
        String res;
        String every = "1234567";
        if (weekdays.equals(every))
            res = "каждый день";
        else {
            res = "дни приёма: ";
            for (int i = 1; i <= every.length(); i++) {
                if (weekdays.contains(Integer.toString(i))) {
                    res += weekdays_list[i-1] + ", ";
                }
            }
            res = res.substring(0,res.length()-2);
        }
        return res;
    }

    public String setFoodInstructInfo(byte instr){
        Log.d("MED_INFO", "setFoodInstructInfo " + instr);
        String res;
        switch (instr) {
            case 0:
                res = "до еды";
                break;
            case 1:
                res = "во время еды";
                break;
            case 2:
                res = "после еды";
                break;
            default:
                res = "не важно";
                break;
        }
        return res;
    }

    public String setInfoNoncompat(List<NonCompatMeds> nc) {
        String res = "";
        long ncID;
        for (int i = 0; i < nc.size(); i++) {
            // получаем id несовмесимого лекарства
            if (nc.get(i).id_one == medID)
                ncID = nc.get(i).id_two;
            else ncID = nc.get(i).id_one;
            Log.d("MED_INFO", "id " + i + "-ого лекарства: " + ncID + " " + dao.getById(ncID).getName());
            res += dao.getById(ncID).getName();
            if (i < nc.size()-1) res += ", ";
        }
        return res;
    }

    public void medDelete(long id) {
        Log.d("MED_INFO", "Вызван метод в ViewModel");
        mMedicineViewModel.deletemed(id);
        finish();
    }

    @Override
    public void onInit(int status) {
        // TODO Auto-generated method stub
        if (status == TextToSpeech.SUCCESS) {

            Locale locale = new Locale("ru");

            int result = mTTS.setLanguage(locale);
            //int result = mTTS.setLanguage(Locale.getDefault());

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "Извините, этот язык не поддерживается");
            } else {
                fab.setEnabled(true);
            }

        } else {
            Log.e("TTS", "Ошибка!");
        }
    }
}
