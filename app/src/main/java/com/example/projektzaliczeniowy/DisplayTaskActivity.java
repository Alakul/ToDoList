package com.example.projektzaliczeniowy;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

public class DisplayTaskActivity extends AppCompatActivity {
    private TextView displayTask, displayDate, displayTime, displayPriority;
    private String taskI, dateI, timeI;
    private int idI, priorityI;
    private ArrayList<ToDoTable> taskList;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_task);

        //Menu
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Zadanie");

        databaseHelper = new DatabaseHelper(this);
        idI = getIntent().getIntExtra("ID",0);
        displayData();
    }

    @Override
    public void onPause() {
        super.onPause();
        displayData();
    }

    @Override
    public void onResume() {
        super.onResume();
        displayData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int id= menuItem.getItemId();
        if (id == android.R.id.home) {
            this.finish();
        }
        else if (id == R.id.editIcon) {
            Intent intent = new Intent(getBaseContext(), EditTaskActivity.class);
            intent.putExtra("ID", idI);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(menuItem);
    }

    void displayData() {
        taskList = databaseHelper.getData(idI);

        taskI = taskList.get(0).getTask();
        dateI = taskList.get(0).getDate();
        timeI = taskList.get(0).getTime();
        priorityI = taskList.get(0).getPriority();

        displayTask = findViewById(R.id.taskDisplay);
        displayTask.setText(taskI);
        displayDate = findViewById(R.id.dateDisplay);
        displayDate.setText(dateI);
        displayTime = findViewById(R.id.timeDisplay);
        displayTime.setText(timeI);
        displayPriority = findViewById(R.id.priorityDisplay);
        displayPriority.setText(String.valueOf(priorityI));
    }
}