package com.example.projektzaliczeniowy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuItem;

public class MainActivity extends AppCompatActivity {

    EditText inputFilename;
    ListView listView;
    ArrayList<ToDoTable> toDoArrayList;
    AdapterToDo toDoAdapter;
    DatabaseHelper databaseHelper;
    int sortTasks;
    Intent intentFile;

    public static final int READ_TXT_FILE = 1;
    public static final int WRITE_TXT_FILE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Database
        databaseHelper =new DatabaseHelper(this);
        toDoArrayList = new ArrayList<ToDoTable>();

        //List
        listView = findViewById(R.id.listView);
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
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage("Czy na pewno chcesz usunąć zaznaczone zadania?");
                    builder.setCancelable(true);

                    builder.setPositiveButton("Tak",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    ArrayList<Integer> selectedItemPositions = toDoAdapter.itemsSelected;
                                    int deleted = 0;
                                    int sumDel = selectedItemPositions.size();
                                    for (int i = selectedItemPositions.size() - 1; i >= 0; i--) {
                                        databaseHelper.delete(selectedItemPositions.get(i));
                                        deleted++;
                                    }
                                    onResume();
                                    showTasks();
                                    mode.finish();

                                    Toast.makeText(getApplicationContext(), "Usunięto " + deleted + " z " + sumDel, Toast.LENGTH_SHORT).show();
                                }
                            });

                    builder.setNegativeButton("Anuluj",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                    AlertDialog alert = builder.create();
                    alert.show();

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
                    showTasks();
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
                    showTasks();
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

        SwipeMenuListView listView = findViewById(R.id.listView);
        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem openItem = new SwipeMenuItem(
                        getApplicationContext());
                openItem.setBackground(R.color.colorPrimary);
                openItem.setWidth(180);
                openItem.setTitle("Edytuj");
                openItem.setTitleSize(18);
                openItem.setTitleColor(Color.WHITE);
                menu.addMenuItem(openItem);
            }
        };
        listView.setMenuCreator(creator);
        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                if (index == 0) {
                    toDoArrayList = databaseHelper.getAllData(sortTasks);
                    ToDoTable dataTable = toDoArrayList.get(position);

                    Intent intent = new Intent(getBaseContext(), EditTaskActivity.class);
                    intent.putExtra("ID", dataTable.getId());
                    startActivity(intent);
                }
                return false;
            }
        });

        showTasks();
    }

    @Override
    public void onPause() {
        super.onPause();
        savePreferences();
        showTasks();
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferences();
        showTasks();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
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
                checkPermissionWrite();
                break;
            case R.id.importIcon:
                checkPermissionRead();
                break;
            case R.id.deleteAllIcon:
                deleteTasks();
                break;

            default:
                return false;
        }
        return true;
    }

    void savePreferences(){
        SharedPreferences preferences = getSharedPreferences("preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("sortData", sortTasks);
        editor.apply();
    }

    void getPreferences(){
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

    void deleteTasks() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Czy na pewno chcesz usunąć wszystkie zadania?");
        builder.setCancelable(true);

        builder.setPositiveButton("Tak",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        databaseHelper.deleteAllData();
                        showTasks();
                    }
                });

        builder.setNegativeButton("Anuluj",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
    }

    void showTasks() {
        toDoArrayList= databaseHelper.getAllData(sortTasks);
        toDoAdapter = new AdapterToDo(this,toDoArrayList);
        listView.setAdapter(toDoAdapter);
    }

    void exportTasks(String filename) {
        String filenameFull = filename+".txt";
        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
        StringBuilder sb = new StringBuilder();

        for (ToDoTable toDo : toDoArrayList) {
            sb.append(toDo.getTask()).append(";").append(toDo.getDate())
                    .append(";").append(toDo.getTime()).append(";").append(toDo.getChecked()).append(";")
                    .append(toDo.getPriority()).append("\n");
        }

        String strToSave = sb.toString();

        try {
            File file = new File(path,filenameFull);
            FileWriter fw;

            if (file.exists() && file.isFile()) {
                file.delete();
            }
            file.createNewFile();

            file = new File(path,filenameFull);
            fw = new FileWriter(file,true);

            fw.append(strToSave);
            fw.flush();
            fw.close();

            Toast.makeText(this, "Zadania wyeksportowane pomyślnie.", Toast.LENGTH_SHORT).show();
        } catch (Exception e){
            Toast.makeText(this, "Wystąpił błąd.", Toast.LENGTH_SHORT).show();
        }
    }

    void importTasks(String filePath){
        try {
            File path = Environment.getExternalStorageDirectory();
            String filename = path+"/"+filePath.substring(filePath.lastIndexOf(":")+1);
            File file = new File(filename);

            StringBuilder text = new StringBuilder();
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                String[] values = line.split(";");
                databaseHelper.insertFromFile(values[0],values[1], values[2], Integer.parseInt(values[3]), Integer.parseInt(values[4]));

                text.append(line);
                text.append('\n');
            }
            br.close();
            Toast.makeText(this, "Zadania zaimportowane pomyślnie.", Toast.LENGTH_SHORT).show();
            onResume();
        }
        catch (IndexOutOfBoundsException e) {
            Toast.makeText(this, "Zła struktutura pliku.", Toast.LENGTH_SHORT).show();
        }
        catch (NumberFormatException e) {
            Toast.makeText(this, "Nieprawidłowy format danych.", Toast.LENGTH_SHORT).show();
        }
        catch (IOException e) {
            Toast.makeText(this, "Coś poszło nie tak.", Toast.LENGTH_SHORT).show();
        }
    }

    void showAlertFilename(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Podaj nazwę pliku:");
        inputFilename = new EditText(this);
        builder.setView(inputFilename);

        builder.setPositiveButton("Eksportuj",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String filename = inputFilename.getText().toString();
                        String taskTrim = filename.trim();
                        if (taskTrim.length()!=0){
                            exportTasks(filename);
                        }
                    }
                });

        builder.setNegativeButton("Anuluj",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
    }

    void showFileManager(){
        intentFile = new Intent(Intent.ACTION_GET_CONTENT);
        intentFile.setType("text/plain");
        startActivityForResult(intentFile, READ_TXT_FILE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == READ_TXT_FILE && resultCode == Activity.RESULT_OK) {
            String path = data.getData().getPath();
            importTasks(path);
        }
    }

    void checkPermissionRead(){
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            showFileManager();
        }
        else {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE }, READ_TXT_FILE);
        }
    }

    void checkPermissionWrite(){
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            showAlertFilename();
        }
        else {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_TXT_FILE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == WRITE_TXT_FILE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            showAlertFilename();
        }
        else if (requestCode == READ_TXT_FILE && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            showFileManager();
        }
    }
}