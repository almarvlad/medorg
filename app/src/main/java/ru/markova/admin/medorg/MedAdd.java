package ru.markova.admin.medorg;

import android.app.DatePickerDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import ru.markova.admin.medorg.Fragments.DatePickerfragment;
import ru.markova.admin.medorg.Room.AppDatabase;
import ru.markova.admin.medorg.Room.MedRepo;
import ru.markova.admin.medorg.Room.MedicineDao;
import ru.markova.admin.medorg.Room.MedicineViewModel;
import ru.markova.admin.medorg.Room.NonCompatMeds;
import ru.markova.admin.medorg.Room.UserMedicine;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MedAdd extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{

    private static final String TAG = "MedAdd";
    private static final String SM = "SAVE_MED";
    TextView beginCoursePicker;
    String[] weekdays_list;
    private long medID;

    boolean[] checkedItems;
    boolean[] checkedMeds;
    ArrayList<Integer> mSelectedItems = new ArrayList();  // отмеченные дни недели
    ArrayList<Integer> mSelectedMeds = new ArrayList();  // отмеченные несовместимые лекарства

    RadioButton weekdays;
    RadioButton noncompat;
    RadioButton fixedDays;

    // для сохранения в бд
    EditText editMedName;
    EditText editTimeFreq;
    EditText editTimeInt;
    EditText editDose;
    EditText add_instr;
    Spinner spinFormDose;

    private MedicineViewModel mMedicineViewModel;
    long[] noncompatID; // массив для хранения id лекарств, которые несовместимы с создаваемым лекарством
    private List<NonCompatMeds> nc;

    Calendar c = Calendar.getInstance();
    UserMedicine umed;
    TimetableMaker ttmaker;
    String num = ""; // для формирования строки с номерами дней недели // по умолчанию - все дни
    //int daysCount = 0; // для хранения количества дней для фиксированного курса

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_med_edit);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true); // отображение кнопки "назад"

        AppDatabase adb = AppDatabase.getDatabase(MedAdd.this);
        final MedicineDao dao = adb.Dao();

        editMedName = (EditText) findViewById(R.id.editMedName);
        editTimeFreq = (EditText) findViewById(R.id.editTimeFreq);
        editTimeInt = (EditText) findViewById(R.id.editTimeInt);
        final RadioGroup rdg_days = (RadioGroup) findViewById(R.id.days_rdg);
        final RadioGroup rdg = (RadioGroup) findViewById(R.id.duration_rdg);
        editDose = (EditText) findViewById(R.id.editDose);
        add_instr = (EditText) findViewById(R.id.add_instr);
        spinFormDose = (Spinner) findViewById(R.id.spinDose);
        final RadioGroup rdg_instr = (RadioGroup) findViewById(R.id.instruct_rdg);
        View compat = (View) findViewById(R.id.compatibility);
        final Button savebtn = (Button) findViewById(R.id.save_btn);

        Intent intent = getIntent();
        medID = intent.getLongExtra("id", -1);
        if (medID > -1) { // если передали ид лекарства, то выгружаем инфу о нём и потом заполнем соответствующие поля
            actionBar.setTitle("Изменить лекарство");
            ttmaker = new TimetableMaker(getBaseContext());

            umed = dao.getById(medID);
            nc = dao.getNoncompat(medID);

            editMedName.setText(umed.getName());
            editDose.setText(umed.getDose());
            String[] doseform = getResources().getStringArray(R.array.dose_list);
            int df = 0;
            while (!doseform[df].equals(umed.getDoseForm())){ df++; }
            spinFormDose.setSelection(df);
            add_instr.setText(umed.getAddInstruct());
            rdg_instr.check(rdg_instr.getChildAt(umed.getInstruct()).getId());

            //setMedInfo(med, nc);
        } else {
            actionBar.setTitle("Новое лекарство");
            umed = new UserMedicine("", 0, true);
        }

        // ПРОДОЛЖИТЕЛЬНОСТЬ КУРСА
        // если выбрано фиксированное кол-во дней, то создать новое диалоговое окно с вводом
        fixedDays = (RadioButton) findViewById(R.id.radio_numdays);
        if (umed.getDuration() > 0) {
            rdg.check(R.id.radio_numdays);
            fixedDays.setText("количество дней: " + umed.getDuration());
        }
        fixedDays.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater li = LayoutInflater.from(getApplicationContext());
                View promptsView = li.inflate(R.layout.fixed_days_course, null); // получаем лэйаут диалогового окна
                //Создаем AlertDialog
                AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(MedAdd.this);
                //Настраиваем fixed_days_course.xml для нашего AlertDialog:
                mDialogBuilder.setView(promptsView);

                // получаем объект Edittext из диалог. окна
                final EditText userInput = (EditText) promptsView.findViewById(R.id.fixeddayscourse);
                if (umed.getDuration() != 0) {
                    String s = Integer.toString(umed.getDuration());
                    userInput.setText(s);
                }
                //Настраиваем сообщение в диалоговом окне:
                mDialogBuilder
                        .setTitle("Количество дней:")
                        .setCancelable(false)
                        .setPositiveButton(getString(R.string.ok),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        //Вводим текст и отображаем в строке ввода на основном экране:
                                        if (!userInput.getText().toString().equals("")) {
                                            umed.setDuration(Integer.parseInt(userInput.getText().toString()));
                                            String s = getString(R.string.numDays) + ": " + umed.getDuration();
                                            fixedDays.setText(s);
                                        } else {
                                            rdg.check(R.id.radio_continuous);
                                            fixedDays.setText("количество дней");
                                            umed.setDuration(0);
                                        }

                                    }
                                })
                        .setNegativeButton(getString(R.string.cancel),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        dialog.cancel();
                                        if (umed.getDuration() == 0) {
                                            rdg.check(R.id.radio_continuous);
                                            fixedDays.setText("количество дней");
                                        }
                                    }
                                });
                AlertDialog alertDialog = mDialogBuilder.create();  // Создаем AlertDialog
                alertDialog.show();                                 // и отображаем его:
                alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(getBaseContext(), R.color.colorPrimaryDark));
                alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(getBaseContext(), R.color.colorPrimaryDark));
            }
        });

        // ВЫБОР ДНЕЙ ПРИЁМА
        weekdays_list = getResources().getStringArray(R.array.weekdays); // образуем массив из строковых ресурсов
        checkedItems = new boolean[weekdays_list.length]; // для хранения состояний чекбоксов
        if (umed.getWeekdays().length() < 7 && umed.getWeekdays().length() > 0) {
            rdg_days.check(R.id.radio_weekdays);
            for (int i = 0; i < umed.getWeekdays().length(); i++) {
                String s = "" + umed.getWeekdays().charAt(i); // получаем каждый день недели в виде строки
                int n = Integer.parseInt(s) - 1;
                checkedItems[n] = true;
                mSelectedItems.add(n);
            }
        }

        weekdays = (RadioButton) findViewById(R.id.radio_weekdays);
        weekdays.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mDialogDays = new AlertDialog.Builder(MedAdd.this);
                mDialogDays.setTitle("Выберите дни:")
                        .setMultiChoiceItems(weekdays_list, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                if (isChecked) {
                                    mSelectedItems.add(which);
                                } else if (mSelectedItems.contains(which)) {
                                    mSelectedItems.remove(Integer.valueOf(which));
                                }
                            }
                        })
                        .setCancelable(false)
                        .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String item = "";
                                for (int i = 0; i < mSelectedItems.size(); i++) {
                                    item += weekdays_list[mSelectedItems.get(i)];
                                    Log.d("SM",""+(mSelectedItems.get(i)+1));
                                    num += (mSelectedItems.get(i)+1);
                                    if (i != mSelectedItems.size()-1)
                                        item += ", ";
                                }
                                if ((item == "")||(mSelectedItems.size()==7)) {
                                    rdg_days.check(R.id.radio_everyday);
                                    weekdays.setText("определённые дни");
                                } else weekdays.setText("определённые дни: " + item);
                            }
                        })
                        .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                if (mSelectedItems.size() == 0) {
                                    rdg_days.check(R.id.radio_everyday);
                                    weekdays.setText("определённые дни");
                                }
                            }
                        });
                AlertDialog mDialog = mDialogDays.create();
                mDialog.show();
                mDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(getBaseContext(), R.color.colorPrimaryDark));
                mDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(getBaseContext(), R.color.colorPrimaryDark));
            }
        });

        // выставляем текущую дату и вешаем на него обработчик нажатия
        String currDate;
        if (medID > -1)
            currDate = DateFormat.getDateInstance().format(new Date(umed.getCourseStart()));
        else currDate = DateFormat.getDateInstance().format(c.getTime());
        beginCoursePicker = (TextView) findViewById(R.id.date);
        beginCoursePicker.setText(currDate);
        beginCoursePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new DatePickerfragment();
                datePicker.show(getSupportFragmentManager(), "date_picker");
//                alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(getBaseContext(), R.color.colorPrimaryDark));
//                alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(getBaseContext(), R.color.colorPrimaryDark));
            }
        });

        // настройка видимости для полей ввода времени приёма
        RadioGroup rdg_time = (RadioGroup) findViewById(R.id.time_rdg);
        final LinearLayout llFreq = (LinearLayout) findViewById(R.id.time_frequency);
        final LinearLayout llInt = (LinearLayout) findViewById(R.id.time_intervals);
        rdg_time.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radio_frequency:
                        llInt.setVisibility(View.GONE);
                        llFreq.setVisibility(View.VISIBLE);
                        break;
                    case R.id.radio_interval:
                        llFreq.setVisibility(View.GONE);
                        llInt.setVisibility(View.VISIBLE);
                        break;
                    default:
                        break;
                }
            }
        });
        if (medID > -1) {
            if (umed.isTimeType()) {
                llFreq.setVisibility(View.GONE);
                llInt.setVisibility(View.VISIBLE);
                rdg_time.check(R.id.radio_interval);
                editTimeInt.setText(Integer.toString(umed.getTimePer()));
            } else {
                editTimeFreq.setText(Integer.toString(umed.getTimePer()));
            }
        }

        mMedicineViewModel = ViewModelProviders.of(this).get(MedicineViewModel.class);
        if (mMedicineViewModel.getMedsCount().length>0) {
            compat.setVisibility(View.VISIBLE); // если у пользователя уже есть лекарства, то можно настроить для них совместимость

            //ААААААА ЧТО-ТО ЗДЕСЬ

            final String[] medsarray = mMedicineViewModel.getMedsCount(); // получаем названия всех лекарств
            checkedMeds = new boolean[medsarray.length];

            noncompat = (RadioButton) findViewById(R.id.radio_noncompat_yes);
            noncompat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder mDialogMeds = new AlertDialog.Builder(MedAdd.this);
                    mDialogMeds.setTitle("Выберите лекарства:");
                    mDialogMeds.setMultiChoiceItems(medsarray, checkedMeds, new DialogInterface.OnMultiChoiceClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                            if (isChecked) {
                                mSelectedMeds.add(which);
                            } else if (mSelectedMeds.contains(which)) {
                                mSelectedMeds.remove(Integer.valueOf(which));
                            }
                        }
                    });
                    mDialogMeds.setCancelable(false);
                    mDialogMeds.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (mSelectedMeds.size()==0){
                                RadioGroup rdg_compat = (RadioGroup) findViewById(R.id.compat_rdg);
                                rdg_compat.check(R.id.radio_noncompat_no);
                            } else {
                                noncompatID = new long[mSelectedMeds.size()];
                                List<UserMedicine> medsDB = mMedicineViewModel.getMedsAL();
                                for (int i = 0; i < mSelectedMeds.size(); i++) {
                                    noncompatID[i] = medsDB.get(mSelectedMeds.get(i)).getID();
                                }
                            }
                        }
                    });
                    mDialogMeds.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            RadioGroup rdg_compat = (RadioGroup) findViewById(R.id.compat_rdg);
                            rdg_compat.check(R.id.radio_noncompat_no);
                            mSelectedMeds.clear();
                        }
                    });
                    AlertDialog mDialog2 = mDialogMeds.create();
                    mDialog2.show();
                    mDialog2.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(getBaseContext(), R.color.colorPrimaryDark));
                    mDialog2.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(getBaseContext(), R.color.colorPrimaryDark));
                }
            });
        }
        // сохранение данных лекарства
        savebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent replyIntent = new Intent();
                // если не добавлено название лекарства, то сохранение не происходит
                if (TextUtils.isEmpty(editMedName.getText())) {
                    setResult(RESULT_CANCELED, replyIntent);
                } else {
                    // узнаём дни приёма
                    RadioGroup rdg_days = (RadioGroup) findViewById(R.id.days_rdg);
                    int wd = rdg_days.indexOfChild(findViewById(rdg_days.getCheckedRadioButtonId()));
                    if (wd == 0) { // если выбран переключатель "каждый день", то строка = "1234567"
                        num = "1234567";
                    }
                    //узнаём время приёма
                    RadioGroup rdg_timetype = (RadioGroup) findViewById(R.id.time_rdg);
                    int tt = rdg_timetype.indexOfChild(findViewById(rdg_timetype.getCheckedRadioButtonId()));
                    boolean timetype;
                    int t;
                    if (tt == 0) {
                        timetype = false; // N раз в день (частота)
                        t = Integer.parseInt(editTimeFreq.getText().toString());
                    } else {
                        timetype = true;  // Каждые N часов (инервалы)
                        t = Integer.parseInt(editTimeInt.getText().toString());
                    }
                    // узнаём инструкции
                    // 0 - до еды
                    // 1 - во время
                    // 2 - после еды
                    // 3 - не важно
                    byte instr = (byte) rdg_instr.indexOfChild(findViewById(rdg_instr.getCheckedRadioButtonId()));

                    umed.setName(editMedName.getText().toString());
                    umed.setTimePer(t);
                    umed.setTimeType(timetype);
                    if (medID == -1)
                        umed.setCourseStart(c.getTime().getTime());
                    umed.setWeekdays(num);
                    umed.setDose(editDose.getText().toString());
                    umed.setDoseForm(spinFormDose.getSelectedItem().toString());
                    umed.setInstruct(instr);
                    umed.setAddInstruct(add_instr.getText().toString());
                    umed.setHasNoncompat(noncompatID!=null);
                    // добавляем новый объект-запись о лекарстве
                    /*
                    UserMedicine med = new UserMedicine(editMedName.getText().toString(), t,
                            c.getTi, timetype, num, editDose.getText().toString(),
                            spinFormDose.getSelectedItem().toString(), instr, add_instr.getText().toString(),
                            daysCount, true, noncompatID!=null);
                            */
                    if (medID > -1) {
                        dao.update(umed);
                        new updateMedAsyncTask(umed, ttmaker).execute();
                    } else mMedicineViewModel.insert(umed, noncompatID);

                    Log.d("SAVE_MED", "name: " + umed.getName() +
                            "\nperiod: " + umed.getTimePer() +
                            "\nstartCourse: " + new Date(umed.getCourseStart()) +
                            "\ntimeType: " + umed.isTimeType() +
                            "\nweekdays: " + umed.getWeekdays() +
                            "\ndose: " + umed.getDose() + " " + umed.getDoseForm() +
                            "\ninstruct: " + umed.getInstruct() + " " + umed.getAddInstruct() +
                            "\ndaysCount: " + umed.getDuration());
                    finish();
                }
            }
        });
    }

    // обновляем расписание в соответствии с изменённми данными лекарства
    public static class updateMedAsyncTask extends AsyncTask<UserMedicine, Void, Void> {
        private UserMedicine umed;
        private TimetableMaker ttMaker;

        updateMedAsyncTask(UserMedicine umed, TimetableMaker tm) {
            this.umed = umed;
            ttMaker = tm;
        }

        @Override
        protected Void doInBackground(final UserMedicine... params) {
            /*
            if (nc != null) { //если есть список несовместимых лекарств
                if (nc.length>0){ //точно, что этот список есть и его длина не равна 0
                    for (int i = 0; i < nc.length; i++) {
                        mAsyncTaskDao.addNoncompat(new NonCompatMeds(r,nc[i])); // добавляем в таблицу несовместимых лекарств связи
                        Log.d("SAVE_MED","NC: "+r+ "; "+nc[i]); // и выводим их в лог, для проверки
                    }
                }
            }
            */
            ttMaker.deleteMedBeforeUpdate(umed.getID());
            for (int i = 0; i < umed.getWeekdays().length(); i++) {
                ttMaker.setPriority(umed.getWeekdays().charAt(i));
                ttMaker.setTimeAllMeds();
                ttMaker.sortAndSaveTimetable();
                ttMaker.clearDayTimetable();
            }
            ttMaker.createHistoryTable();
            ttMaker.createNextAlarm();
            return null;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        c.set(Calendar.YEAR, year); c.set(Calendar.MONTH, month); c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        umed.setCourseStart(c.getTimeInMillis());
        String pickedDate = DateFormat.getDateInstance().format(c.getTime());
        beginCoursePicker.setText(pickedDate);
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        finish();
    }
}
