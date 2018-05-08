package com.example.admin.medorg;

import android.app.DatePickerDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
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
import com.example.admin.medorg.Room.UserMedicine;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MedEdit extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{

    private static final String TAG = "MedEdit";
    TextView beginCoursePicker;
    String[] weekdays_list;
    boolean[] checkedItems;
    boolean[] checkedMeds;
    ArrayList<Integer> mSelectedItems = new ArrayList();  // Where we track the selected items
    ArrayList<Integer> mSelectedMeds = new ArrayList();  // Where we track the selected items
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
    public static final String EXTRA_REPLY = "com.example.android.wordlistsql.REPLY";
//    AppDatabase db = AppDatabase.getDatabase(this);
//    DBDao medDao = db.Dao();
    private MedicineViewModel mMedicineViewModel;

    Calendar c = Calendar.getInstance();
    String num = ""; // для формирования строки с номерами дней недели
    int daysCount = 0; // для хранения количества дней для фиксированного курса

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_med_edit);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        fixedDays = (RadioButton) findViewById(R.id.radio_numdays);
        fixedDays.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater li = LayoutInflater.from(getApplicationContext());
                View promptsView = li.inflate(R.layout.fixed_days_course, null);
                //Создаем AlertDialog
                AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(MedEdit.this);

                //Настраиваем prompt.xml для нашего AlertDialog:
                mDialogBuilder.setView(promptsView);

                //Настраиваем отображение поля для ввода текста в открытом диалоге:
                final EditText userInput = (EditText) promptsView.findViewById(R.id.fixeddayscourse);
                //Настраиваем сообщение в диалоговом окне:
                mDialogBuilder
                        .setTitle("Количество дней:")
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        //Вводим текст и отображаем в строке ввода на основном экране:
                                        daysCount = Integer.parseInt(userInput.getText().toString());
                                        fixedDays.setText("количество дней: " + daysCount);
                                    }
                                })
                        .setNegativeButton("Отмена",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        dialog.cancel();
                                        RadioGroup rdg = (RadioGroup) findViewById(R.id.duration_rdg);
                                        rdg.check(R.id.radio_continuous);
                                        fixedDays.setText("количество дней");
                                    }
                                });
                //Создаем AlertDialog:
                AlertDialog alertDialog = mDialogBuilder.create();

                //и отображаем его:
                alertDialog.show();
            }
        });

        weekdays_list = getResources().getStringArray(R.array.weekdays);
        checkedItems = new boolean[weekdays_list.length];
        weekdays = (RadioButton) findViewById(R.id.radio_weekdays);
        weekdays.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mDialogDays = new AlertDialog.Builder(MedEdit.this);
                Log.d(TAG,"создан mDialogdays");
                mDialogDays.setTitle("Выберите дни:")
                        .setMultiChoiceItems(weekdays_list, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                if (isChecked) {
                                    // If the user checked the item, add it to the selected items
                                    mSelectedItems.add(which);
                                } else if (mSelectedItems.contains(which)) {
                                    // Else, if the item is already in the array, remove it
                                    mSelectedItems.remove(Integer.valueOf(which));
                                }
                            }
                        })
                        .setCancelable(false)
                        .setPositiveButton("Выбрать", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String item = "";
                                for (int i = 0; i < mSelectedItems.size(); i++) {
                                    item += weekdays_list[mSelectedItems.get(i)];
                                    Log.d("SAVE_MED","bb "+(mSelectedItems.get(i)+1));
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
                        .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
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
            compat.setVisibility(View.VISIBLE);
            final String[] medsarray = mMedicineViewModel.getMedsCount();
            //weekdays_list = getResources().getStringArray(R.array.weekdays);
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
                                // If the user checked the item, add it to the selected items
                                mSelectedMeds.add(which);
                            } else if (mSelectedMeds.contains(which)) {
                                // Else, if the item is already in the array, remove it
                                mSelectedMeds.remove(Integer.valueOf(which));
                            }
                        }
                    });
                    mDialogMeds.setCancelable(false);
                    mDialogMeds.setPositiveButton("Выбрать", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String item = "";
                            for (int i = 0; i < mSelectedMeds.size(); i++) {
                                item += medsarray[mSelectedMeds.get(i)];
                                if (i != mSelectedMeds.size() - 1)
                                    item += ", ";
                            }
                            Toast.makeText(getApplicationContext(),"лекарства: " + item, Toast.LENGTH_LONG).show();
                            if (item == "") {
                                RadioGroup rdg_compat = (RadioGroup) findViewById(R.id.compat_rdg);
                                rdg_compat.check(R.id.radio_noncompat_no);
                            }
                        }
                    });
                    mDialogMeds.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            RadioGroup rdg_compat = (RadioGroup) findViewById(R.id.compat_rdg);
                            rdg_compat.check(R.id.radio_noncompat_no);
                        }
                    });
                    AlertDialog mDialog2 = mDialogMeds.create();
                    mDialog2.show();
                }
            });
        }


        savebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent replyIntent = new Intent();
                if (TextUtils.isEmpty(editMedName.getText())) {
                    setResult(RESULT_CANCELED, replyIntent);
                } else {
                    RadioGroup rdg_days = (RadioGroup) findViewById(R.id.days_rdg);
                    int wd = rdg_days.indexOfChild(findViewById(rdg_days.getCheckedRadioButtonId()));
                    if (wd == 0) {
                        num = "1234567";
                    }
                    RadioGroup rdg_timetype = (RadioGroup) findViewById(R.id.time_rdg);
                    int tt = rdg_timetype.indexOfChild(findViewById(rdg_timetype.getCheckedRadioButtonId()));
                    boolean timetype;
                    float t;
                    if (tt == 0) {
                        timetype = false;
                        t = Float.parseFloat(editTimeFreq.getText().toString());
                    } else {
                        timetype = true;
                        t = Float.parseFloat(editTimeInt.getText().toString());
                    }

                    RadioGroup rdg_instr = (RadioGroup) findViewById(R.id.instruct_rdg);
                    byte instr = (byte) rdg_instr.indexOfChild(findViewById(rdg_instr.getCheckedRadioButtonId()));
                    UserMedicine med = new UserMedicine(editMedName.getText().toString(), t,
                            c.getTime().getTime(), timetype, num, Float.parseFloat(editDose.getText().toString()),
                            spinFormDose.getSelectedItem().toString(), instr, add_instr.getText().toString(),
                            daysCount);
                    mMedicineViewModel.insert(med);
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
    public boolean onOptionsItemSelected(MenuItem menuItem)
    {
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
