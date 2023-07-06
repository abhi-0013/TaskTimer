package com.example.Abhishek.tasktimer;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * This class is used for database creation and updation
 * this class is only used by {@link AppProvider}
 */
 class AppDatabase extends SQLiteOpenHelper {
   private static final String TAG = "AppDatabase";
   private static final String DATABASE_NAME ="TaskTimer.db";
   private static final int DATABASE_VERSION = 3;
   private static AppDatabase instance = null;
     private AppDatabase(Context context){
        // this super class calls the constructor of the SQLiteOpenHelper class
        super(context ,DATABASE_NAME , null , DATABASE_VERSION);
//        Log.d(TAG, "AppDatabase: creating database constructor ");
     }

   /**
    * this function is used to get the object of the app's singelton database helper class
    *
    * @param context the content providers context
    * @return a SQLiteOpenHelper object
    */
     static AppDatabase getInstance(Context context){
        if(instance == null){
//           Log.d(TAG, "getInstance: creating SqliteHelper instance");
           instance = new AppDatabase(context);
        }

        return instance;
     }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
//        Log.d(TAG, "onCreate: for SQLiteopenHelper starts");
        String ssql;
        ssql = "create table " + TaskContract.TABLE_NAME + "("
                + TaskContract.Columns._ID + " INTEGER PRIMARY KEY NOT NULL, "
                + TaskContract.Columns.TASK_NAME + " TEXT NOT NULL, "
                + TaskContract.Columns.TASK_DESCRIPTION + " TEXT, "
                + TaskContract.Columns.TASK_SORTORDER + " INTEGER);" ;
        Log.d(TAG, "onCreate:  creating sql statement is "+ ssql);
        sqLiteDatabase.execSQL(ssql);

        addTimingsTable(sqLiteDatabase);
        addDurationView(sqLiteDatabase);
//        Log.d(TAG, "onCreate: ends here");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        Log.d(TAG, "onUpgrade: starts");
        switch(i){
            case 1:
                // upgrade logic from version 1
                addTimingsTable(sqLiteDatabase);
                // fall through, to include version 2 upgrade logic as well
            case 2:
                // upgrade logic from version 2
                addDurationView(sqLiteDatabase);
                break;
            default:
                throw new IllegalStateException("onUpgrade() with unknown new version: "+ i1);
        }

    }

    private void addTimingsTable(SQLiteDatabase db){
         String sSQL = "CREATE TABLE "+ TimingContract.TABLE_NAME+" ("
                 + TimingContract.Columns._ID+ " INTEGER PRIMARY KEY NOT NULL, "
                 + TimingContract.Columns.TIMING_TASK_ID + " INTEGER NOT NULL, "
                 + TimingContract.Columns.TIMING_START_TIME+ " INTEGER, "
                 +TimingContract.Columns.TIMING_DURATION + " INTEGER);";
        Log.d(TAG, "addTimingsTable: command executed "+ sSQL);
        db.execSQL(sSQL);

        sSQL = "CREATE TRIGGER Remove_Task"
                + " AFTER DELETE ON "+ TaskContract.TABLE_NAME
                + " FOR EACH ROW"
                + " BEGIN"
                + " DELETE FROM "+ TimingContract.TABLE_NAME
                + " WHERE "+ TimingContract.Columns.TIMING_TASK_ID+ " = OLD."+TaskContract.Columns._ID + ";"
                + " END;";
        Log.d(TAG, "addTimingsTable: trigger command executed is : "+sSQL);
        db.execSQL(sSQL);
    }

    private void addDurationView(SQLiteDatabase db){
         /*
         CREATE VIEW vwTaskDurations AS
         SELECT Timings._id,
         Tasks.Name,
         Tasks.Description,
         Timings.StartTime,
         DATE(Timings.StartTime, 'unixepoch') AS StartDate,
         SUM(Timings.Duration) AS Duration
         FROM Tasks INNER JOIN Timings
         ON Tasks._id = Timings.TaskId
         GROUP BY Tasks._id, StartDate;
         */

        String sSQL = "CREATE VIEW " + DurationContract.TABLE_NAME
                + " AS SELECT " + TimingContract.TABLE_NAME + "." + TimingContract.Columns._ID + ", "
                + TaskContract.TABLE_NAME + "." + TaskContract.Columns.TASK_NAME + ", "
                + TaskContract.TABLE_NAME + "." + TaskContract.Columns.TASK_DESCRIPTION + ", "
                + TimingContract.TABLE_NAME + "." + TimingContract.Columns.TIMING_START_TIME + ","
                + " DATE(" + TimingContract.TABLE_NAME + "." + TimingContract.Columns.TIMING_START_TIME + ", 'unixepoch')"
                + " AS " + DurationContract.Columns.DURATION_START_DATE + ","
                + " SUM(" + TimingContract.TABLE_NAME + "." + TimingContract.Columns.TIMING_DURATION + ")"
                + " AS " + DurationContract.Columns.DURATIONS_DURATION
                + " FROM " + TaskContract.TABLE_NAME + " JOIN " + TimingContract.TABLE_NAME
                + " ON " + TaskContract.TABLE_NAME + "." + TaskContract.Columns._ID + " = "
                + TimingContract.TABLE_NAME + "." + TimingContract.Columns.TIMING_TASK_ID
                + " GROUP BY " + DurationContract.Columns.DURATION_START_DATE + ", " + DurationContract.Columns.DURATIONS_NAME
                + ";";
        Log.d(TAG, sSQL);
        db.execSQL(sSQL);


    }
}
