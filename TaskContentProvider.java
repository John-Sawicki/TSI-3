package com.john.android.tsi.Sqlite;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.john.android.tsi.Sqlite.TaskContract.TaskEntry;
public class TaskContentProvider extends ContentProvider {
    private TaskDbHelper mTaskDbHelper;
    public static final int TASKS =100;
    private static final UriMatcher mUriMatcher =buildUriMatcher();
     public static UriMatcher buildUriMatcher(){
         UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
         uriMatcher.addURI(TaskContract.AUTHORITY, TaskContract.PATH_TASKS, TASKS);
         return uriMatcher;
     }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        mTaskDbHelper = new TaskDbHelper(context);
        return true;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues cv) {
         final SQLiteDatabase db = mTaskDbHelper.getWritableDatabase();
         int match =mUriMatcher.match(uri);
         Uri returnUri;
         switch (match){
             case TASKS:
                 long id = db.insert(TaskEntry.TABLE_NAME,null,cv );
                 returnUri = ContentUris.withAppendedId(TaskEntry.CONTENT_URI, id);
                 break;
             default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
         }
         getContext().getContentResolver().notifyChange(returnUri,null);
        return null;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
         final SQLiteDatabase db = mTaskDbHelper.getReadableDatabase();
         int match = mUriMatcher.match(uri);
         Cursor taskCursor;
         switch (match){
             case TASKS:
                 taskCursor = db.query(TaskEntry.TABLE_NAME, null,null, null, null,null, null);
                break;
             default:
                 throw new UnsupportedOperationException("Unknown uri: " + uri);
         }
         return taskCursor;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }
}
