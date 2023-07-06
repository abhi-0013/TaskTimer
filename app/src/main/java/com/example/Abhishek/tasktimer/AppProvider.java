package com.example.Abhishek.tasktimer;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * This class is the only one available for external application to know about {@link AppDatabase}
 */
public class AppProvider extends ContentProvider {
    private static final String TAG = "AppProvider";

    private AppDatabase mOpenHelper;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    static final String CONTENT_AUTHORITY = "com.example.Abhishek.tasktimer.provider";
    public static final Uri CONTENT_AUTHORITY_URI = Uri.parse("content://"+CONTENT_AUTHORITY);

    private static final int TASKS = 100;
    private static final int TASKS_ID = 101;

    private static final int TIMINGS = 200;
    private static final int TIMINGS_ID = 201;

    /*
    private static final int TASK_TIMINGS = 300;
    private static final int TASK_TIMINGS_ID = 301;
    */

    private static final int TASK_DURATIONS = 400;
    private static final int TASK_DURATIONS_ID = 401;

    private static UriMatcher buildUriMatcher(){
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

        //example content://com.example.Abhishek.tasktimer.provider.Tasks
        matcher.addURI(CONTENT_AUTHORITY,TaskContract.TABLE_NAME,TASKS);
        //example content://com.example.Abhishek.tasktimer.provider/Tasks/4
        matcher.addURI(CONTENT_AUTHORITY,TaskContract.TABLE_NAME + "/#",TASKS_ID);

        matcher.addURI(CONTENT_AUTHORITY,TimingContract.TABLE_NAME, TIMINGS);

        matcher.addURI(CONTENT_AUTHORITY,TimingContract.TABLE_NAME + "/#", TIMINGS_ID);

        matcher.addURI(CONTENT_AUTHORITY,DurationContract.TABLE_NAME,TASK_DURATIONS);
        matcher.addURI(CONTENT_AUTHORITY,DurationContract.TABLE_NAME + "/#",TASK_DURATIONS_ID);

        return matcher;
    }



    @Override
    public boolean onCreate() {
        Log.d(TAG, "onCreate: ONCREATE of provider class is called ");
        mOpenHelper = AppDatabase.getInstance(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] strings, @Nullable String s, @Nullable String[] strings1, @Nullable String s1) {
//        Log.d(TAG, "query: query method is called with URI "+ uri);
        final int match = sUriMatcher.match(uri);

        SQLiteQueryBuilder queryBuilder  = new SQLiteQueryBuilder();
        switch (match){
            case TASKS:
//                Log.d(TAG, "query: Tasks is called");
                queryBuilder.setTables(TaskContract.TABLE_NAME);
                break;
            case TASKS_ID:
                queryBuilder.setTables(TaskContract.TABLE_NAME);
                long taskID = TaskContract.getTaskID(uri);
//                Log.d(TAG, "query:  Tasks_ID is called with taskID as "+taskID);
                queryBuilder.appendWhere(TaskContract.Columns._ID + " = "+ taskID);
                break;
            case TIMINGS:
                queryBuilder.setTables(TimingContract.TABLE_NAME);
                break;
            case TIMINGS_ID:
                queryBuilder.setTables(TimingContract.TABLE_NAME);
                long timingID = TimingContract.getTimingID(uri);
                queryBuilder.appendWhere(TimingContract.Columns._ID + " = "+ timingID);
                break;
            case TASK_DURATIONS:
                Log.d(TAG, "query: task_duration is called ");
                queryBuilder.setTables(DurationContract.TABLE_NAME);
                break;
            case TASK_DURATIONS_ID:
                queryBuilder.setTables(DurationContract.TABLE_NAME);
                long durationId = DurationContract.getDurationId(uri);
                queryBuilder.appendWhere(DurationContract.Columns._ID+" = "+ durationId);
                Log.d(TAG, "query: task_duration_id is called with id "+ durationId);
                break;
            default:
                throw new IllegalArgumentException("UNKNOWN URI *@*@*@*@@**@@* ");
        }
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        Cursor cursor = queryBuilder.query(db,strings,s,strings1,null,null,s1);
//        Log.d(TAG, "query: rows in returned Cursor is " + cursor.getCount()); // TODO remove theis line

       // noinspection ConstantCOnditions
        cursor.setNotificationUri(getContext().getContentResolver(),uri);
//        getContext().getContentResolver().notifyChange(uri ,null);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case TASKS:
                return TaskContract.CONTENT_TYPE;

            case TASKS_ID:
                return TaskContract.CONTENT_ITEM_TYPE;

            case TIMINGS:
                return TimingContract.CONTENT_TYPE;

            case TIMINGS_ID:
                return TimingContract.CONTENT_ITEM_TYPE;

            case TASK_DURATIONS:
                return DurationContract.CONTENT_TYPE;

            case TASK_DURATIONS_ID:
                return DurationContract.CONTENT_ITEM_TYPE;

            default:
                throw new IllegalArgumentException("unknown Uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
//        Log.d(TAG, "insert: Entering in the insert method ");
        int match = sUriMatcher.match(uri);
//        Log.d(TAG, "insert: match found is "+ match);

        final SQLiteDatabase db;
        long recordId;
        Uri returnUri;

        switch(match){
            case TASKS:
                db = mOpenHelper.getWritableDatabase();
                recordId = db.insert(TaskContract.TABLE_NAME,null,contentValues);
                if(recordId >= 0){
                    returnUri = TaskContract.buildTaskUri(recordId);
                }else{
                    throw new android.database.SQLException("Failed to insert the data entry for the given uri :  " + uri.toString() );
                }
                break;
            case TIMINGS:
                db = mOpenHelper.getWritableDatabase();
                recordId = db.insert(TimingContract.TABLE_NAME,null,contentValues);
                if(recordId >= 0){
                    returnUri = TimingContract.buildTimingUri(recordId);
                }else{
                    throw new android.database.SQLException("Failed to insert the value in the Timing table for this uri "+ uri.toString());
                }
                break;
            default:
                throw new IllegalArgumentException("Unkown uri "+ uri);
        }
        if(recordId >= 0 ){
            // something was inserted
//            Log.d(TAG, "insert: setting notifyChanged with " + uri);
            getContext().getContentResolver().notifyChange(uri ,null);
        }else{
//            Log.d(TAG, "insert: nothing inserted");
        }
//        Log.d(TAG, "insert: exiting the insert with :: " + returnUri);

        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
//        Log.d(TAG, "delete: entering the delete method ");
        int match =  sUriMatcher.match(uri);
//        Log.d(TAG, "delete: returned the match as "+ match);

        final SQLiteDatabase db;
        int count;

        String selectionCriteria;

        switch (match){
            case TASKS:
                db = mOpenHelper.getWritableDatabase();
                count = db.delete(TaskContract.TABLE_NAME ,s ,strings);
                break;
            case TASKS_ID:
                db = mOpenHelper.getWritableDatabase();
                long taskId = TaskContract.getTaskID(uri);
                selectionCriteria = TaskContract.Columns._ID + " = "+taskId;
                if((s!= null) && (s.length()>0)){
                    selectionCriteria+= " AND (" + s + " )";
                }
                count = db.delete(TaskContract.TABLE_NAME,selectionCriteria ,strings);
                break;
            case TIMINGS:
                db = mOpenHelper.getWritableDatabase();
                count = db.delete(TimingContract.TABLE_NAME ,s ,strings);
                break;
            case TIMINGS_ID:
                db = mOpenHelper.getWritableDatabase();
                long timingsID = TimingContract.getTimingID(uri);
                selectionCriteria = TimingContract.Columns._ID + " = "+timingsID;
                if((s!= null) && (s.length()>0)){
                    selectionCriteria+= " AND (" + s + " )";
                }
                count = db.delete(TimingContract.TABLE_NAME,selectionCriteria ,strings);
                break;

            default:
                throw new IllegalArgumentException("Unknown uri ::: "+ uri);
        }
        if(count >0){
            // something was deleted
            getContext().getContentResolver().notifyChange(uri ,null);
        }
//        Log.d(TAG, "delete: exiting the delete method with count  "+ count);
        return count;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
//        Log.d(TAG, "update: enters into the update function ");
        int match = sUriMatcher.match(uri);
//        Log.d(TAG, "update: matched int is "+ match);

        final SQLiteDatabase db;
        int count;

        String selectionCriteria;

        switch (match){
            case TASKS:
                db = mOpenHelper.getWritableDatabase();
                count = db.update(TaskContract.TABLE_NAME,contentValues,s , strings);
                break;
            case TASKS_ID:
                db = mOpenHelper.getWritableDatabase();
                long taskId = TaskContract.getTaskID(uri);
                selectionCriteria = TaskContract.Columns._ID + " = " + taskId;
                if((s != null ) && (s.length()>0)){
                    selectionCriteria += " AND (" + s + ")";
                }
                count = db.update(TaskContract.TABLE_NAME , contentValues,selectionCriteria , strings);
                break;
            case TIMINGS:
                db = mOpenHelper.getWritableDatabase();
                count = db.update(TimingContract.TABLE_NAME,contentValues,s , strings);
                break;
            case TIMINGS_ID:
                db = mOpenHelper.getWritableDatabase();
                long timings_Id = TimingContract.getTimingID(uri);
                selectionCriteria = TimingContract.Columns._ID + " = " + timings_Id;
                if((s != null ) && (s.length()>0)){
                    selectionCriteria += " AND (" + s + ")";
                }
                count = db.update(TimingContract.TABLE_NAME , contentValues,selectionCriteria , strings);
                break;
            default:
                throw  new IllegalArgumentException("Unknown Uri "+ uri);

        }
        if(count > 0 ){
            //something was updated
//            Log.d(TAG, "update: setting notify change with uri "+ uri);
            getContext().getContentResolver().notifyChange(uri , null);
        }
//        Log.d(TAG, "update: Exiting update , returing "+ count );
        return count;
    }
}
