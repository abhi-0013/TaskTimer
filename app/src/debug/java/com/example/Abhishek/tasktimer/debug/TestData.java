package com.example.Abhishek.tasktimer.debug;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.example.Abhishek.tasktimer.TaskContract;
import com.example.Abhishek.tasktimer.TimingContract;

import java.util.GregorianCalendar;

public class TestData {

    private static final String TAG = "TestData";
    public static void generateTestData(ContentResolver contentResolver){
        final int SECS_IN_DAY = 86400;
        final int LOWER_BOUND = 30;
        final int UPPER_BOUND = 60;
        final int MAX_DURATION = SECS_IN_DAY /6;

        //get a list of task ID's from the database.
        String[] Projection = {TaskContract.Columns._ID};
        Uri uri = TaskContract.CONTENT_URI;
        Cursor cursor = contentResolver.query(uri,
                                              Projection,
                                      null,
                                   null,
                                     null);

        if((cursor != null) && (cursor.moveToFirst())){
            do{
                try{
                long taskId = cursor.getLong(cursor.getColumnIndexOrThrow(TaskContract.Columns._ID));
                //generate between 30 and 60 random timings for this task
                int LoopCount = LOWER_BOUND + getRandomInt(UPPER_BOUND - LOWER_BOUND);

                for(int i =0 ;i< LoopCount ;i++){

                    long randomDate = randomDateTime();
                    //generate a random duration between 0 and 4 hours
                    long duration = (long) getRandomInt(MAX_DURATION);

                    // create new TestTiming object
                    TestTiming testTiming = new TestTiming(taskId , randomDate, duration);

                    //add it to the databases
                    saveCurrentTiming(contentResolver, testTiming);

                }

                }catch (IllegalArgumentException e){
                    Log.e(TAG, "generateTestData: "+ e.getMessage() );
                }
            }while(cursor.moveToNext());
        }

    }

    private static void saveCurrentTiming(ContentResolver contentResolver, TestTiming testTiming) {
        //save the timing record
        ContentValues values = new ContentValues();
        values.put(TimingContract.Columns.TIMING_TASK_ID , testTiming.taskId);
        values.put(TimingContract.Columns.TIMING_START_TIME,testTiming.startTime);
        values.put(TimingContract.Columns.TIMING_DURATION,testTiming.duration);

        // update database
        contentResolver.insert(TimingContract.CONTENT_URI, values);

    }

    private static int getRandomInt(int max){
        return (int) Math.round(Math.random()*max);
    }

    private static  long randomDateTime(){
        // Set the range of years
        final int startYear = 2022;
        final int endYear = 2023;

        int sec = getRandomInt(59);
        int min = getRandomInt(59);
        int hour = getRandomInt(23);
        int month = getRandomInt(11);
        int year = startYear + getRandomInt(endYear - startYear);

        GregorianCalendar gc = new GregorianCalendar(year , month , 1);
        int day = 1+ getRandomInt(gc.getActualMaximum(GregorianCalendar.DAY_OF_MONTH) -1);

        gc.set(year,month,day,hour,min,sec);
        return gc.getTimeInMillis();
    }
}
