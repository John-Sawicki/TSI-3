package com.example.android.tsi.Sqlite;

import android.net.Uri;
import android.provider.BaseColumns;

public class TaskContract {
    public static final String AUTHORITY = "com.example.android.tsi";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://"+AUTHORITY);
    public static final String PATH_TASKS = "tasks";    //same as table name
    public static final class TaskEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_TASKS).build();
        public static final String TABLE_NAME ="tasks";
        public static final String DATE ="date";                 //0
        public static final String SYSTEM ="system";             //1
        public static final String DESCRIPTION ="description";   //2
        public static final String LOCATION = "location";        //3
        public static final String STATUS ="status";             //4
    }
}
