package com.example.admin.medorg;

import android.app.DatePickerDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
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
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.admin.medorg.Fragments.DatePickerfragment;
import com.example.admin.medorg.Fragments.FragmentMeds;
import com.example.admin.medorg.Room.AppDatabase;
import com.example.admin.medorg.Room.DBDao;
import com.example.admin.medorg.Room.UserMedicine;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class MedEdit extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{

    private static final String TAG = "MedEdit";
    TextView beginCoursePicker;
    String[] weekdays_list;
    boolean[] checkedItems;
    ArrayList<Integer> mSelectedItems = new ArrayList();  // Where we track the selected items
    RadioButton weekdays;

    // для сохранения в бд
    EditText editMedName;
    EditText editTimeFreq;
    public static final String EXTRA_REPLY = "com.example.android.wordlistsql.REPLY";
    AppDatabase db = AppDatabase.getDatabase(this);
    DBDao medDao = db.Dao();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_med_edit);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        weekdays_list = getResources().getStringArray(R.array.weekdays);
        checkedItems = new boolean[weekdays_list.length];
        weekdays = (RadioButton) findViewById(R.id.radio_weekdays);
        weekdays.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(MedEdit.this);
                mBuilder.setTitle("Выберите дни:")
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
                                    if (i != mSelectedItems.size()-1)
                                        item += ", ";
                                }
                                weekdays.setText("определённые дни: " + item);
                            }
                        })
                        .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                AlertDialog mDialog = mBuilder.create();
                mDialog.show();

            }
        });

        weekdays = (RadioButton) findViewById(R.id.radio_weekdays);
        beginCoursePicker = (TextView) findViewById(R.id.date);
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
        View compat = (View) findViewById(R.id.compatibility);
        final Button savebtn = (Button) findViewById(R.id.save_btn);

        if (medDao.medsCount().size()>0)
            compat.setVisibility(View.VISIBLE);

        savebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
/*

               if (TextUtils.isEmpty(editMedName.getText())) {
                   setResult(RESULT_CANCELED, replyIntent);
               } else {

               }
*/
                Intent replyIntent = new Intent();
                String word = editMedName.getText().toString();
                replyIntent.putExtra(EXTRA_REPLY, word);
                setResult(RESULT_OK, replyIntent);
                finish();
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
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year); c.set(Calendar.MONTH, month); c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        String pickedDate = DateFormat.getDateInstance().format(c.getTime());
        beginCoursePicker.setText(pickedDate);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

}
