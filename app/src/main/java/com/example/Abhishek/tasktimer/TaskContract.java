package com.example.Abhishek.tasktimer;

import static com.example.Abhishek.tasktimer.AppProvider.CONTENT_AUTHORITY;
import static com.example.Abhishek.tasktimer.AppProvider.CONTENT_AUTHORITY_URI;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * This class contains the contracting information about the Task table of our database
 */
public class TaskContract {
    static final String TABLE_NAME = "Tasks";

    // Tasks table columns
    public static  class Columns{
        public static final String _ID = BaseColumns._ID;
        public static final String TASK_NAME  = "Name";
        public static final String TASK_DESCRIPTION = "Description";
        public static final String TASK_SORTORDER = "SortOrder";
        private Columns(){
            // empty constructor so just that no one can call it
        }
    }

    public static final Uri CONTENT_URI = Uri.withAppendedPath(CONTENT_AUTHORITY_URI,TABLE_NAME);

    static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd." + CONTENT_AUTHORITY + "." + TABLE_NAME;
    static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd." + CONTENT_AUTHORITY + "." + TABLE_NAME;

    public static Uri buildTaskUri(long taskId){
        return ContentUris.withAppendedId(CONTENT_URI,taskId);
    }

    public static long getTaskID(Uri uri){
        return ContentUris.parseId(uri);
    }

}
