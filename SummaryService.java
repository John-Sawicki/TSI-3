package com.john.android.tsi.Widget;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;
import android.util.Log;

import com.john.android.tsi.SqliteSum.SumDbHelper;
import com.john.android.tsi.SqliteSum.SumTaskContract.SummaryEntry;
public class SummaryService extends IntentService{
    private SQLiteDatabase mDb;
    private String sumTitle = "System Name";
    private String sumDesc = "Description";
    private StringBuffer summary = new StringBuffer(200);
    public SummaryService(){super("SummaryService");}
    public static String ACTION_UPDATE_SUMMARY =
            "com.example.android.tsi.Widget.action.update_summary";

    public static void startActionUpdateSum(Context context){
        Intent intent = new Intent(context, SummaryService.class);
        intent.setAction(ACTION_UPDATE_SUMMARY);
        context.startService(intent);
        Log.d("widget", "startAction");
    }
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if(intent!=null){
            final String action = intent.getAction();
            if(action.equals(ACTION_UPDATE_SUMMARY)){
                Log.d("widget", "onHandeIntent");
                handleActionSummary();
            }
        }
    }
    private void handleActionSummary(){//query values entered from each activity
        Log.d("widget", "handleAction");
        SumDbHelper dbHelper = new SumDbHelper(getApplicationContext());
        mDb = dbHelper.getReadableDatabase();
        Cursor cursor = mDb.query(SummaryEntry.TABLE_NAME,
                null,null,null,null,null,null);
        if(cursor.moveToFirst()){
            cursor.moveToPosition(0);
            sumTitle = cursor.getString(cursor.getColumnIndex(SummaryEntry.COLUMN_SYSTEM));
            sumDesc= cursor.getString(cursor.getColumnIndex(SummaryEntry.COLUMN_SUMMARY));
            summary.append(sumTitle).append("\n").append(sumDesc);
            Log.d("widget handle", summary.toString());
        }
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this,SummaryWidget.class ));
        SummaryWidget.updateAppWidgets(this, appWidgetManager,appWidgetIds, summary.toString() );
    }
}
