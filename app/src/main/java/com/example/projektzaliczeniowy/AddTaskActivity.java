package com.example.projektzaliczeniowy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddTaskActivity extends AppCompatActivity {

    EditText task;
    TextView displayDate;
    TextView displayTime;
    RadioGroup priorityGroup;
    RadioButton priorityHigh, priorityMedium, priorityLow;
    DatabaseHelper databaseHelper;


    //***
    private ImageView photo;
    ListView attachmentList;
    ArrayList<String> attachments;
    AdapterAttachment attachmentListAdapter;
    public static final int GALLERY_REQUEST_CODE = 105;
    public static final int PICK_IMAGE_MULTIPLE = 1;
    //***


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);


        //***
        photo = (ImageView) findViewById(R.id.imageView2);
        attachmentList = (ListView) findViewById(R.id.attachmentList);
        attachmentListAdapter = new AdapterAttachment(this, attachments);

        findViewById(R.id.attachButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                gallery.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(gallery, GALLERY_REQUEST_CODE);
            }
        });
        //***


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
        final Calendar calendar=Calendar.getInstance();
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




    //***
    public void checkPermission(){
        if (ContextCompat.checkSelfPermission(AddTaskActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            //loadFiles();
        }
        else {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == GALLERY_REQUEST_CODE){
            if (grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //loadFiles();
            }
            else {
                //textView.setText("Brak uprawnień do wyświetlenia galerii");
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_MULTIPLE && resultCode == Activity.RESULT_OK) {
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            attachments = new ArrayList<String>();

            if (data.getClipData() != null) {
                int count = data.getClipData().getItemCount();
                Uri imageUri;
                for (int i = 0; i < count; i++){
                    imageUri = data.getClipData().getItemAt(i).getUri();
                    attachments.set(0, String.valueOf(imageUri));
                }
            }
            else if (data.getData() != null) {
                String imagePath = data.getData().getPath();
                photo.setImageURI(Uri.parse(imagePath));
            }
        }
        showAttachments();
    }

    void showAttachments() {
        attachmentListAdapter = new AdapterAttachment(this,attachments);
        attachmentList.setAdapter(attachmentListAdapter);
    }
    //***
}