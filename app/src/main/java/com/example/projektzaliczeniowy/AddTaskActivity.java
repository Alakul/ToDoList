package com.example.projektzaliczeniowy;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddTaskActivity extends AppCompatActivity {

    EditText task;
    String taskValue;
    TextView displayDate;
    TextView displayTime;
    RadioGroup priorityGroup;
    RadioButton priorityHigh, priorityMedium, priorityLow;
    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        //Menu
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Dodaj zadanie");

        //Database
        databaseHelper =new DatabaseHelper(this);

        //Task
        task = (EditText) findViewById(R.id.editTaskValue);

        //Priority
        priorityGroup = (RadioGroup) findViewById(R.id.radioGroupPriority);
        priorityHigh = (RadioButton) findViewById(R.id.radioButtonHigh);
        priorityMedium = (RadioButton) findViewById(R.id.radioButtonMedium);
        priorityLow = (RadioButton) findViewById(R.id.radioButtonLow);

        //Date
        displayDate = findViewById(R.id.editDateValue);
        Date currentDate = Calendar.getInstance().getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy",Locale.getDefault());
        String formattedDate = simpleDateFormat.format(currentDate);
        displayDate.setText(formattedDate);
        findViewById(R.id.dateValueButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        //Time
        displayTime = findViewById(R.id.editTimeValue);
        displayTime.setText("00:00");
        findViewById(R.id.timeValueButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog();
            }
        });

        //Add task
        findViewById(R.id.addTaskButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String taskTrim = task.getText().toString().trim();

                int priorityVal = 0;
                if (priorityHigh.isChecked()){
                    priorityVal = 1;
                }
                else if (priorityMedium.isChecked()){
                    priorityVal = 2;
                }
                else if (priorityLow.isChecked()){
                    priorityVal = 3;
                }

                if (priorityVal==0){
                    showAlertDialogEmpty();
                }
                else {
                    if (taskTrim.length()==0) {
                        showAlertDialogEmpty();
                    }
                    else {
                        taskValue = taskTrim;
                        addTask(taskTrim, priorityVal);
                    }
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int id= menuItem.getItemId();
        if (id == android.R.id.home) {
            this.finish();
        }
        return super.onOptionsItemSelected(menuItem);
    }

    void showTimePickerDialog() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                String time = String.format("%02d:%02d", hour, minute);
                displayTime.setText(time);
            }
        }, 0, 0, true);
        timePickerDialog.show();
    }

    void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                calendar.set(year, month, day);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
                String formattedDate = simpleDateFormat.format(calendar.getTime());

                displayDate.setText(formattedDate);
            }
        }, year, month, day);
        datePickerDialog.show();
    }

    void showAlertDialogEmpty() {
        AlertDialog.Builder alert=new AlertDialog.Builder(this);
        alert.setMessage("Wypełnij wszystkie pola!");
        alert.setPositiveButton("OK", null);
        alert.show();
    }

    void addTask(String taskTrim, int priority) {
        String dateT = displayDate.getText().toString();
        String timeT = displayTime.getText().toString();
        if (databaseHelper.insert(taskTrim, dateT, timeT, priority)) {
            task.getText().clear();
            Toast.makeText(this, "Rekord dodany pomyślnie", Toast.LENGTH_SHORT).show(); }
        else {
            Toast.makeText(this, "Rekord istnieje", Toast.LENGTH_SHORT).show();
        }
    }
}