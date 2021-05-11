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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class EditTaskActivity extends AppCompatActivity {

    EditText task;
    TextView displayDate, displayTime;
    DatabaseHelper databaseHelper;
    RadioGroup priorityGroup;
    RadioButton priorityHigh, priorityMedium, priorityLow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);

        //Menu
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Edytuj zadanie");

        //Database
        int idI = getIntent().getIntExtra("ID", 0);
        databaseHelper = new DatabaseHelper(this);
        ArrayList<ToDoTable> taskList = databaseHelper.getData(idI);

        //Values
        String taskI = taskList.get(0).getTask();
        String dateI = taskList.get(0).getDate();
        String timeI = taskList.get(0).getTime();
        int priorityI = taskList.get(0).getPriority();

        //Priority
        priorityGroup = (RadioGroup) findViewById(R.id.radioGroupPriority);
        priorityHigh = (RadioButton) findViewById(R.id.radioButtonHigh);
        priorityMedium = (RadioButton) findViewById(R.id.radioButtonMedium);
        priorityLow = (RadioButton) findViewById(R.id.radioButtonLow);

        //Set priority
        if (priorityI == 1){
            priorityHigh.setChecked(true);
        }
        else if (priorityI == 2){
            priorityMedium.setChecked(true);
        }
        else if (priorityI == 3){
            priorityLow.setChecked(true);
        }

        //Task
        task = findViewById(R.id.editTaskValue);
        task.setText(taskI);

        //Date
        displayDate = findViewById(R.id.editDateValue);
        displayDate.setText(dateI);
        findViewById(R.id.dateValueButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        //Time
        displayTime = findViewById(R.id.editTimeValue);
        displayTime.setText(timeI);
        findViewById(R.id.timeValueButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog();
            }
        });

        //Edit task
        findViewById(R.id.editTaskButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String taskTrim = task.getText().toString().trim();

                int priorityVal = 0;
                if (priorityHigh.isChecked()){ priorityVal = 1; }
                else if (priorityMedium.isChecked()){ priorityVal = 2; }
                else if (priorityLow.isChecked()){ priorityVal = 3; }

                if (priorityVal==0){
                    showAlertDialogEmpty();
                }
                else {
                    if (taskTrim.length()==0) { showAlertDialogEmpty(); }
                    else { editTask(taskTrim, priorityVal); }
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

    void editTask(String taskTrim, int priority) {
        int idUpdate = getIntent().getIntExtra("ID",0);
        String dateUpdate = displayDate.getText().toString();
        String timeUpdate = displayTime.getText().toString();
        databaseHelper.update(idUpdate, taskTrim, dateUpdate, timeUpdate, priority);

        Toast.makeText(this, "Zadanie edytowane pomyślnie", Toast.LENGTH_SHORT).show();
    }
}