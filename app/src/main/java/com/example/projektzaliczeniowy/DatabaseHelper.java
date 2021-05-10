package com.example.projektzaliczeniowy;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME ="database.db";
    private static final String TABLE_TODO ="todo";
    public static final String COLUMN_ID ="id";
    private static final String COLUMN_TASK ="task";
    private static final String COLUMN_DATE ="date";
    private static final String COLUMN_TIME ="time";
    private static final String COLUMN_CHECKED ="checked";
    private static final String COLUMN_PRIORITY ="priority";

    SQLiteDatabase db;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION );
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_TODO + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TASK + " TEXT NOT NULL, " +
                COLUMN_DATE + " TEXT NOT NULL, " +
                COLUMN_TIME + " TEXT NOT NULL, " +
                COLUMN_CHECKED + " INTEGER NOT NULL, " +
                COLUMN_PRIORITY + " INTEGER NOT NULL )");
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TODO);
        onCreate(db);
    }

    public boolean insert(String task, String date, String time, int priority) {
        db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+ TABLE_TODO + " WHERE "+ COLUMN_TASK+ "=? AND "+ COLUMN_DATE+ "=? AND "+ COLUMN_TIME +"= ?", new String[]{String.valueOf(task), String.valueOf(date), String.valueOf(time)});

        if (cursor.getCount() == 0) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(COLUMN_TASK, task);
            contentValues.put(COLUMN_DATE, date);
            contentValues.put(COLUMN_TIME, time);
            contentValues.put(COLUMN_CHECKED, 0);
            contentValues.put(COLUMN_PRIORITY, priority);

            db=this.getWritableDatabase();
            db.insert(TABLE_TODO,null,contentValues);
            db.close();
            cursor.close();

            return true;
        }
        else {
            return false;
        }
    }

    public void update(int id, String task, String date, String time, int priority) {
        db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TASK, task);
        values.put(COLUMN_DATE, date);
        values.put(COLUMN_TIME, time);
        values.put(COLUMN_PRIORITY, priority);

        db.update(TABLE_TODO, values, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
    }

    public void delete(int id) {
        SQLiteDatabase db=this.getWritableDatabase();
        db.delete(TABLE_TODO, COLUMN_ID + "=?",  new String[]{String.valueOf(id)});
        db.close();
    }

    public ArrayList<ToDoTable> getAllData(int sortData) {
        Cursor cursor;
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<ToDoTable> arrayList = new ArrayList<>();

        if (sortData == 0) {
            cursor = db.rawQuery("SELECT * FROM " + TABLE_TODO + " ORDER BY " + COLUMN_ID + " ASC", new String[]{});
        }
        else if (sortData == 1) {
            cursor = db.rawQuery("SELECT * FROM " + TABLE_TODO + " ORDER BY " + COLUMN_DATE + " ASC", new String[]{});
        }
        else if (sortData == 2) {
            cursor = db.rawQuery("SELECT * FROM " + TABLE_TODO + " ORDER BY " + COLUMN_PRIORITY + " ASC", new String[]{});
        }
        else if (sortData == 3) {
            cursor = db.rawQuery("SELECT * FROM " + TABLE_TODO + " ORDER BY " + COLUMN_TASK + " ASC", new String[]{});
        }
        else {
            cursor = db.rawQuery("SELECT * FROM " + TABLE_TODO + " ORDER BY " + COLUMN_CHECKED + " ASC", new String[]{});
        }

        if (cursor.moveToFirst()) {
            do {
                ToDoTable toDoTable = new ToDoTable();
                toDoTable.setId((cursor.getInt(0)));
                toDoTable.setTask(cursor.getString(1));
                toDoTable.setDate(cursor.getString(2));
                toDoTable.setTime(cursor.getString(3));
                toDoTable.setChecked(cursor.getInt(4));
                toDoTable.setPriority(cursor.getInt(5));
                arrayList.add(toDoTable);

            } while(cursor.moveToNext());
        }
        cursor.close();
        return arrayList;
    }

    public ArrayList<ToDoTable> getData(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_TODO + " WHERE "+ COLUMN_ID+ "=?", new String[]{String.valueOf(id)});;
        ArrayList<ToDoTable> arrayList = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                ToDoTable toDoTable = new ToDoTable();
                toDoTable.setId((cursor.getInt(0)));
                toDoTable.setTask(cursor.getString(1));
                toDoTable.setDate(cursor.getString(2));
                toDoTable.setTime(cursor.getString(3));
                toDoTable.setChecked(cursor.getInt(4));
                toDoTable.setPriority(cursor.getInt(5));
                arrayList.add(toDoTable);

            } while(cursor.moveToNext());
        }
        cursor.close();
        return arrayList;
    }

    public void setAsChecked(int id) {
        db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CHECKED, 1);

        db.update(TABLE_TODO, values, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
    }

    public void setAsUnchecked(int id) {
        db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CHECKED, 0);

        db.update(TABLE_TODO, values, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
    }

}
