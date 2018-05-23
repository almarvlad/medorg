package com.example.admin.medorg;

import android.app.DatePickerDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.persistence.room.Room;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.DialogFragment;
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
import android.widget.Toast;

import com.example.admin.medorg.Fragments.DatePickerfragment;
import com.example.admin.medorg.Fragments.FragmentMeds;
import com.example.admin.medorg.Room.AppDatabase;
import com.example.admin.medorg.Room.DBDao;
import com.example.admin.medorg.Room.MedicineViewModel;
import com.example.admin.medorg.Room.NonCompatMeds;
import com.example.admin.medorg.Room.UserMedicine;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MedEdit extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{

    private static final String TAG = "MedEdit";
    private static final String SM = "SAVE_MED";
    TextView beginCoursePicker;
    String[] weekdays_list;

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

    Calendar c = Calendar.getInstance();
    String num = ""; // для формирования строки с номерами дней недели // по умолчанию - все дни
    int daysCount = 0; // для хранения количества дней для фиксированного курса

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_med_edit);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true); // отображение кнопки "назад"

        // ПРОДОЛЖИТЕЛЬНОСТЬ КУРСА
        // если выбрано фиксированное кол-во дней, то создать новое диалоговое окно с вводом
        fixedDays = (RadioButton) findViewById(R.id.radio_numdays);
        fixedDays.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater li = LayoutInflater.from(getApplicationContext());
                View promptsView = li.inflate(R.layout.fixed_days_course, null); // получаем лэйаут диалогового окна
                //Создаем AlertDialog
                AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(MedEdit.this);
                //Настраиваем fixed_days_course.xml для нашего AlertDialog:
                mDialogBuilder.setView(promptsView);

                // получаем объект Edittext из диалог. окна
                final EditText userInput = (EditText) promptsView.findViewById(R.id.fixeddayscourse);
                if (daysCount != 0) {
                    String s = Integer.toString(daysCount);
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
                                        daysCount = Integer.parseInt(userInput.getText().toString());
                                        String s = getString(R.string.numDays) + ": " + daysCount;
                                        fixedDays.setText(s);
                                    }
                                })
                        .setNegativeButton(getString(R.string.cancel),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        dialog.cancel();
                                        if (daysCount == 0) {
                                            RadioGroup rdg = (RadioGroup) findViewById(R.id.duration_rdg);
                                            rdg.check(R.id.radio_continuous);
                                            fixedDays.setText("количество дней");
                                        }
                                    }
                                });
                AlertDialog alertDialog = mDialogBuilder.create();  // Создаем AlertDialog
                alertDialog.show();                                 // и отображаем его:
            }
        });

        // ВЫБОР ДНЕЙ ПРИЁМА
        weekdays_list = getResources().getStringArray(R.array.weekdays); // образуем массив из строковых ресурсов
        checkedItems = new boolean[weekdays_list.length]; // для хранения состояний чекбоксов
        weekdays = (RadioButton) findViewById(R.id.radio_weekdays);
        weekdays.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mDialogDays = new AlertDialog.Builder(MedEdit.this);
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
                                    RadioGroup rdg_days = (RadioGroup) findViewById(R.id.days_rdg);
                                    rdg_days.check(R.id.radio_everyday);
                                } else weekdays.setText("определённые дни: " + item);
                            }
                        })
                        .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                RadioGroup rdg_days = (RadioGroup) findViewById(R.id.days_rdg);
                                rdg_days.check(R.id.radio_everyday);
                                weekdays.setText("определённые дни");
                            }
                        });
                AlertDialog mDialog = mDialogDays.create();
                mDialog.show();
            }
        });

        // выставляем текущую дату и вешаем на него обработчик нажатия
        String currDate = DateFormat.getDateInstance().format(c.getTime());
        beginCoursePicker = (TextView) findViewById(R.id.date);
        beginCoursePicker.setText(currDate);
        beginCoursePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new DatePickerfragment();
                datePicker.show(getSupportFragmentManager(), "date_picker");
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

        editMedName = (EditText) findViewById(R.id.editMedName);
        editTimeFreq = (EditText) findViewById(R.id.editTimeFreq);
        editTimeInt = (EditText) findViewById(R.id.editTimeInt);
        editDose = (EditText) findViewById(R.id.editDose);
        add_instr = (EditText) findViewById(R.id.add_instr);
        spinFormDose = (Spinner) findViewById(R.id.spinDose);
        View compat = (View) findViewById(R.id.compatibility);
        final Button savebtn = (Button) findViewById(R.id.save_btn);

        mMedicineViewModel = ViewModelProviders.of(this).get(MedicineViewModel.class);
        if (mMedicineViewModel.getMedsCount().length>0) {
            compat.setVisibility(View.VISIBLE); // если у пользователя уже есть лекарства, то можно настроить для них совместимость

            final String[] medsarray = mMedicineViewModel.getMedsCount(); // получаем названия всех лекарств
            checkedMeds = new boolean[medsarray.length];

            noncompat = (RadioButton) findViewById(R.id.radio_noncompat_yes);
            noncompat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder mDialogMeds = new AlertDialog.Builder(MedEdit.this);
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
                    float t;
                    if (tt == 0) {
                        timetype = false; // N раз в день (частота)
                        t = Float.parseFloat(editTimeFreq.getText().toString());
                    } else {
                        timetype = true;  // Каждые N часов (инервалы)
                        t = Float.parseFloat(editTimeInt.getText().toString());
                    }
                    // узнаём инструкции
                    // 0 - до еды
                    // 1 - во время
                    // 2 - после еды
                    // 3 - не важно
                    RadioGroup rdg_instr = (RadioGroup) findViewById(R.id.instruct_rdg);
                    byte instr = (byte) rdg_instr.indexOfChild(findViewById(rdg_instr.getCheckedRadioButtonId()));

                    // добавляем новый объект-запись о лекарстве
                    UserMedicine med = new UserMedicine(editMedName.getText().toString(), t,
                            c.getTime().getTime(), timetype, num, Float.parseFloat(editDose.getText().toString()),
                            spinFormDose.getSelectedItem().toString(), instr, add_instr.getText().toString(),
                            daysCount, true);
                    mMedicineViewModel.insert(med, noncompatID);

                    Log.d("SAVE_MED", "name: " + med.getName() +
                            "\nperiod: " + med.getTimePer() +
                            "\nstartCourse: " + new Date(med.getCourseStart()) +
                            "\ntimeType: " + med.isTimeType() +
                            "\nweekdays: " + med.getWeekdays() +
                            "\ndose: " + med.getDose() + " " + med.getDoseForm() +
                            "\ninstruct: " + med.getInstruct() + " " + med.getAddInstruct() +
                            "\ndaysCount: " + med.getDuration());
                    finish();
                }
            }
        });
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
        String pickedDate = DateFormat.getDateInstance().format(c.getTime());
        beginCoursePicker.setText(pickedDate);
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        finish();
    }
}
