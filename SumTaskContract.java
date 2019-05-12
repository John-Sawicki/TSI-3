package com.john.android.tsi.SqliteSum;

import android.net.Uri;
import android.provider.BaseColumns;

public class SumTaskContract {
    public static final String AUTHORITY = "com.example.android.tsi";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://"+AUTHORITY);
    public static final String PATH_SUMMARY = "summary";
    public static final long INVALID_SUM_ID = -1;
    public static final class SummaryEntry implements BaseColumns {
        public static final Uri CONTENT_URI= BASE_CONTENT_URI.buildUpon().appendPath(PATH_SUMMARY).build();
        public static final String TABLE_NAME = "summary";
        public static final String COLUMN_SYSTEM = "system";
        public static final String COLUMN_SUMMARY = "summary";
    }
}
