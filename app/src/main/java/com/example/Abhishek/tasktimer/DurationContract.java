package com.example.Abhishek.tasktimer;

import static com.example.Abhishek.tasktimer.AppProvider.CONTENT_AUTHORITY;
import static com.example.Abhishek.tasktimer.AppProvider.CONTENT_AUTHORITY_URI;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * This class contains the contracting information about the Task table of our database
 */
public class DurationContract {
    static final String TABLE_NAME = "vwTaskDurations";

    // Timings table columns
    public static  class Columns{
        public static final String _ID = BaseColumns._ID;
        public static final String DURATIONS_NAME  = TaskContract.Columns.TASK_NAME;
        public static final String DURATIONS_DESCRIPTION = TaskContract.Columns.TASK_DESCRIPTION;
        public static final String DURATIONS_START_TIME = TimingContract.Columns.TIMING_START_TIME;
        public static final String DURATION_START_DATE = "StartDate";
        public static final String DURATIONS_DURATION = TimingContract.Columns.TIMING_DURATION;

        private Columns(){
            // empty constructor so just that no one can call it
        }
    }

    public static final Uri CONTENT_URI = Uri.withAppendedPath(CONTENT_AUTHORITY_URI,TABLE_NAME);

    static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd." + CONTENT_AUTHORITY + "." + TABLE_NAME;
    static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd." + CONTENT_AUTHORITY + "." + TABLE_NAME;

    public static long getDurationId(Uri uri){
        return ContentUris.parseId(uri);
    }

}
