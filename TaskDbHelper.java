package com.john.android.tsi.Sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.john.android.tsi.Sqlite.TaskContract.TaskEntry;
public class TaskDbHelper  extends SQLiteOpenHelper{
    private static final String DATABASE_NAME = "tasks.db";
    private static final int DATABASE_VERSION = 1;
    public TaskDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String  SQL_CREATE_TASK_LIST ="Create Table "+
                TaskEntry.TABLE_NAME+" ("+
                TaskEntry._ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
                TaskEntry.DATE+" INTEGER NOT NULL, "+   //add time in ms to db
                TaskEntry.SYSTEM+" TEXT NOT NULL, "+
                TaskEntry.DESCRIPTION+" TEXT NOT NULL, "+
                TaskEntry.LOCATION+" TEXT NOT NULL, "+
                TaskEntry.STATUS+" TEXT NOT NULL);";
        sqLiteDatabase.execSQL(SQL_CREATE_TASK_LIST);
    }
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(TaskEntry.TABLE_NAME);
    }
}
