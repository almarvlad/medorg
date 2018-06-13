package ru.markova.admin.medorg;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.util.List;

import ru.markova.admin.medorg.Room.AppDatabase;
import ru.markova.admin.medorg.Room.MedicineDao;
import ru.markova.admin.medorg.Room.NonCompatMeds;
import ru.markova.admin.medorg.Room.UserMedicine;

public class MedEdit extends AppCompatActivity {
    private long medID;
    AppDatabase adb;
    MedicineDao dao;
    private UserMedicine med;
    private List<NonCompatMeds> nc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_med_edit);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Изменение лекарства");

        Intent intent = getIntent();
        medID = intent.getLongExtra("id", -1);

        adb = AppDatabase.getDatabase(MedEdit.this);
        dao = adb.Dao();
        med = dao.getById(medID);
        nc = dao.getNoncompat(medID);

        setMedInfo(med, nc);
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

    public void setMedInfo(UserMedicine med, List<NonCompatMeds> nc){
        EditText et = (EditText) findViewById(R.id.editMedName);
        et.setText(med.getName());
        if (med.isTimeType()) {
            RadioGroup rgTime = (RadioGroup) findViewById(R.id.time_rdg);
            rgTime.check(R.id.radio_interval);
        }
    }
}
