package com.example.projektzaliczeniowy;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    TextView test;

    private ListView listView;
    private ArrayList<ToDoTable> toDoArrayList;
    private AdapterToDo toDoAdapter;
    private DatabaseHelper databaseHelper;
    private int sortTasks;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        test = (TextView) findViewById(R.id.test);

        //Database
        databaseHelper =new DatabaseHelper(this);
        toDoArrayList = new ArrayList<ToDoTable>();

        //List
        listView = findViewById(R.id.listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                toDoArrayList = databaseHelper.getAllData(sortTasks);
                ToDoTable dataTable = toDoArrayList.get(position);

                Intent intent = new Intent(getBaseContext(), DisplayTaskActivity.class);
                intent.putExtra("ID", dataTable.getId());
                startActivity(intent);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                toDoArrayList = databaseHelper.getAllData(sortTasks);
                ToDoTable dataTable = toDoArrayList.get(position);

                Intent intent = new Intent(getBaseContext(), EditTaskActivity.class);
                intent.putExtra("ID", dataTable.getId());
                startActivity(intent);

                return false;
            }
        });

        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        listView.setMultiChoiceModeListener(new ListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                int checkedCount = listView.getCheckedItemCount();
                mode.setTitle("Zaznaczono: "+ checkedCount);

                toDoArrayList = databaseHelper.getAllData(sortTasks);
                ToDoTable toDoTable = toDoArrayList.get(position);
                int idAdd = toDoTable.getId();

                if (checked) {
                    toDoAdapter.itemsSelected.add(idAdd); }
                else {
                    toDoAdapter.itemsSelected.remove(id);
                }
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                mode.getMenuInflater().inflate(R.menu.menu_selection, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                if (item.getItemId() == R.id.deleteIcon) {
                    ArrayList<Integer> selectedItemPositions = toDoAdapter.itemsSelected;
                    int deleted = 0;
                    int sumDel = selectedItemPositions.size();
                    for (int i = selectedItemPositions.size() - 1; i >= 0; i--) {
                        databaseHelper.delete(selectedItemPositions.get(i));
                        deleted++;
                    }
                    onResume();
                    mode.finish();

                    Toast.makeText(getApplicationContext(), "Usunięto " + deleted + " z " + sumDel, Toast.LENGTH_SHORT).show();
                    return true;
                }
                else if (item.getItemId() == R.id.checkIcon){

                    ArrayList<Integer> selectedItemPositions = toDoAdapter.itemsSelected;
                    int checked = 0;
                    int sumChecked = selectedItemPositions.size();
                    for (int i = selectedItemPositions.size() - 1; i >= 0; i--) {
                        databaseHelper.setAsChecked(selectedItemPositions.get(i));
                        checked++;
                    }
                    onResume();
                    mode.finish();

                    Toast.makeText(getApplicationContext(), "Oznaczono " + checked + " z " + sumChecked, Toast.LENGTH_SHORT).show();
                    return true;

                }
                else if (item.getItemId() == R.id.uncheckIcon){

                    ArrayList<Integer> selectedItemPositions = toDoAdapter.itemsSelected;
                    int unchecked = 0;
                    int sumUnchecked = selectedItemPositions.size();
                    for (int i = selectedItemPositions.size() - 1; i >= 0; i--) {
                        databaseHelper.setAsUnchecked(selectedItemPositions.get(i));
                        unchecked++;
                    }
                    onResume();
                    mode.finish();

                    Toast.makeText(getApplicationContext(), "Oznaczono " + unchecked + " z " + sumUnchecked, Toast.LENGTH_SHORT).show();
                    return true;


                }
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                toDoAdapter.itemsSelected.clear();
            }
        });

        showTasks();
    }

    @Override
    public void onPause() {
        super.onPause();
        savePreferences();
        toDoAdapter.swapItems(databaseHelper.getAllData(sortTasks));
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferences();
        toDoAdapter.swapItems(databaseHelper.getAllData(sortTasks));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.plusIcon:
                Intent intent = new Intent(getBaseContext(), AddTaskActivity.class);
                startActivity(intent);
                break;
            case R.id.sortIcon:
                sortTasks();
                break;
            case R.id.exportIcon:
                try {
                    exportTasks();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.importIcon:
                importTasks();
                break;
            default:
                return false;
        }
        return true;
    }

    public void savePreferences(){
        SharedPreferences preferences = getSharedPreferences("preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("sortData", sortTasks);
        editor.apply();
    }

    public void getPreferences(){
        SharedPreferences preferences = getSharedPreferences("preferences", Context.MODE_PRIVATE);
        sortTasks = preferences.getInt("sortData", -1);
    }

    void sortTasks() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String[] choice = {"Data dodania", "Termin zakończenia", "Priorytet", "Nazwa", "Status"};

        builder.setTitle("Sortuj według").setItems(choice, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        sortTasks = 0;
                        break;
                    case 1:
                        sortTasks = 1;
                        break;
                    case 2:
                        sortTasks = 2;
                        break;
                    case 3:
                        sortTasks = 3;
                        break;
                    case 4:
                        sortTasks = 4;
                        break;
                }
                setOrder();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    void setOrder(){
        savePreferences();
        toDoAdapter.swapItems(databaseHelper.getAllData(sortTasks));
    }

    void showTasks() {
        toDoArrayList= databaseHelper.getAllData(sortTasks);
        toDoAdapter = new AdapterToDo(this,toDoArrayList);
        listView.setAdapter(toDoAdapter);
    }








    void exportTasks() throws IOException {
        BufferedReader bf = null;
        String filename = "ae.txt";
        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
        File file = new File(path,filename);
        FileWriter fw = new FileWriter(file,true);

        StringBuffer sb = new StringBuffer();

        for (ToDoTable toDo : toDoArrayList) {
            sb.append(toDo.getId()).append(";").append(toDo.getTask()).append(";").append(toDo.getDate())
                    .append(";").append(toDo.getTime()).append(";").append(toDo.getChecked()).append(";")
                    .append(toDo.getPriority()).append("\n");
        }

        String strToSave = sb.toString();
        //FileOutputStream outputStream;

        test.setText(strToSave);
        try {
            bf = new BufferedReader(new FileReader(filename));

            fw.write(strToSave);
            fw.close();
            /*
            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(strToSave.getBytes());
            outputStream.close();*/
        } finally{
            if (bf != null)
            {
                bf.close();
                fw.close();
            }
        }
    }

    void importTasks(){
        try {
            File path = Environment.getExternalStorageDirectory();
            File file = new File(path,"ae.txt");


            StringBuilder text = new StringBuilder();
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
            test.setText(text);
        }
        catch (IOException e) {
            Log.e("SAVE_FILE", e.getMessage());
        }
    }
}