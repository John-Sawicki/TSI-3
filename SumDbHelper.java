package com.example.android.tsi.SqliteSum;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.android.tsi.SqliteSum.SumTaskContract.SummaryEntry;
public class SumDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME="summary.db";
    private static final int DATABASE_VERSION= 1;
    public SumDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_PLANTS_TABLE = "CREATE TABLE "+
                SummaryEntry.TABLE_NAME+" ("+
                SummaryEntry._ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
                SummaryEntry.COLUMN_SYSTEM+" TEXT NOT NULL,"+
                SummaryEntry.COLUMN_SUMMARY+" TEXT NOT NULL)";
        sqLiteDatabase.execSQL(SQL_CREATE_PLANTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+SummaryEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

}
