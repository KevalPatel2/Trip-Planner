package com.example.tripplanner;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper{

    public static final String databaseName = "TripPlanner.db";

    // User profile table
    public static final String TABLE_USERS = "users";
    public static final String USERS_COL_1 = "email";
    public static final String USERS_COL_2 = "username";
    public static final String USERS_COL_3 = "password";

    // Trip history table
    public static final String TABLE_TRIP_HISTORY = "trip_history";
    public static final String TRIP_HISTORY_COL_1 = "id";
    public static final String TRIP_HISTORY_COL_2 = "start_point";
    public static final String TRIP_HISTORY_COL_3 = "destination";
    public static final String TRIP_HISTORY_COL_4 = "date";
    public static final String TRIP_HISTORY_COL_5 = "time";
    public static final String TRIP_HISTORY_COL_6 = "notes";

    public DatabaseHelper(@Nullable Context context) {
        super(context, "TripPlanner.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase MyDatabase) {
        // Create User Profile Table
        MyDatabase.execSQL("create Table " + TABLE_USERS + " (email TEXT primary key, username TEXT, password TEXT)");

        // Create Trip History Table
        MyDatabase.execSQL("create table " + TABLE_TRIP_HISTORY + " (id INTEGER primary key AUTOINCREMENT, " +
                "start_point TEXT, destination TEXT, date TEXT, time TEXT, notes TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase MyDatabase, int i, int i1) {
        MyDatabase.execSQL("drop Table if exists " + TABLE_USERS);
        MyDatabase.execSQL("drop Table if exists " + TABLE_TRIP_HISTORY);

        //Recreate tables
        onCreate(MyDatabase);
    }

    public boolean insertData(String email, String username, String password){
        SQLiteDatabase MyDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("email", email);
        contentValues.put("username", username);
        contentValues.put("password", password);
        long result = MyDatabase.insert(TABLE_USERS, null, contentValues);

        return result != -1;
    }

    public boolean checkEmail(String email) {
        SQLiteDatabase MyDatabase = this.getWritableDatabase();
        Cursor cursor = MyDatabase.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " + USERS_COL_1 + "=?", new String[]{email});
        int count = cursor.getCount();
        cursor.close();
        return count > 0;
    }

    // Check if username exists in the database
    public boolean checkUsername(String username) {
        SQLiteDatabase MyDatabase = this.getWritableDatabase();
        Cursor cursor = MyDatabase.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " + USERS_COL_2 + "=?", new String[]{username});
        int count = cursor.getCount();
        cursor.close();
        return count > 0;
    }

    // Check if email and password combination exists in the database
    public boolean checkEmailPassword(String email, String password) {
        SQLiteDatabase MyDatabase = this.getWritableDatabase();
        Cursor cursor = MyDatabase.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " + USERS_COL_1 + "=? AND " + USERS_COL_3 + "=?", new String[]{email, password});
        int count = cursor.getCount();
        cursor.close();
        return count > 0;
    }

    // Insert trip data into the database
    public boolean insertTripData(String startPoint, String destination, String date, String time, String notes) {
        SQLiteDatabase MyDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TRIP_HISTORY_COL_2, startPoint);
        contentValues.put(TRIP_HISTORY_COL_3, destination);
        contentValues.put(TRIP_HISTORY_COL_4, date);
        contentValues.put(TRIP_HISTORY_COL_5, time);
        contentValues.put(TRIP_HISTORY_COL_6, notes);
        long result = MyDatabase.insert(TABLE_TRIP_HISTORY, null, contentValues);
        return result != -1;
    }

    // Retrieve all trip history records from the database
    public Cursor getAllTripHistory() {
        SQLiteDatabase MyDatabase = this.getWritableDatabase();
        String[] columns = {TRIP_HISTORY_COL_2, TRIP_HISTORY_COL_3, TRIP_HISTORY_COL_4, TRIP_HISTORY_COL_5, TRIP_HISTORY_COL_6}; // Define the columns to retrieve
        return MyDatabase.query(TABLE_TRIP_HISTORY, columns, null, null, null, null, null);
    }

}
