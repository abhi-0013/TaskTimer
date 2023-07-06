package com.example.Abhishek.tasktimer;

import static com.example.Abhishek.tasktimer.AppProvider.CONTENT_AUTHORITY;
import static com.example.Abhishek.tasktimer.AppProvider.CONTENT_AUTHORITY_URI;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * This class contains the contracting information about the Task table of our database
 */
public class TimingContract {
    static final String TABLE_NAME = "Timings";

    // Timings table columns
    public static  class Columns{
        public static final String _ID = BaseColumns._ID;
        public static final String TIMING_TASK_ID  = "TaskId";
        public static final String TIMING_START_TIME = "StartTime";
        public static final String TIMING_DURATION = "Duration";
        private Columns(){
            // empty constructor so just that no one can call it
        }
    }

    public static final Uri CONTENT_URI = Uri.withAppendedPath(CONTENT_AUTHORITY_URI,TABLE_NAME);

    static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd." + CONTENT_AUTHORITY + "." + TABLE_NAME;
    static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd." + CONTENT_AUTHORITY + "." + TABLE_NAME;

    public static Uri buildTimingUri(long TimingId){
        return ContentUris.withAppendedId(CONTENT_URI,TimingId);
    }

    public static long getTimingID(Uri uri){
        return ContentUris.parseId(uri);
    }

}
