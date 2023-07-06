package com.example.Abhishek.tasktimer;

import android.util.Log;

import java.io.Serializable;
import java.util.Date;

public class Timing implements Serializable {
    private static final long serialVersionUID = 20230210;
    private static final String TAG = Timing.class.getSimpleName();

    private long m_ID;
    private Task task;
    private long m_startTime;
    private long mDuration;

    public Timing(Task task) {
        this.task = task;

        Date currentTime = new Date();
        m_startTime = currentTime.getTime() /1000;
        mDuration = 0;
    }

    public long getID() {
        return m_ID;
    }

    public Task getTask() {
        return task;
    }

    public long getstartTime() {
        return m_startTime;
    }

    public long getDuration() {
        return mDuration;
    }

    public void setID(long m_ID) {
        this.m_ID = m_ID;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public void setStartTime(long m_startTime) {
        this.m_startTime = m_startTime;
    }

    public void setDuration() {
        // Calculating the duration using m_startTime

        Date currentTime = new Date();
        mDuration = (currentTime.getTime()/1000) - m_startTime;
        Log.d(TAG, "setDuration "+ task.getId()+ " -startTime : "+ m_startTime+" | Duration : "+ mDuration);
    }

}
