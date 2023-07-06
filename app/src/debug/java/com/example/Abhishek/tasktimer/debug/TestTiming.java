package com.example.Abhishek.tasktimer.debug;

public class TestTiming {
    long taskId;
    long startTime;
    long duration;

    public TestTiming(long taskId, long startTime, long duration) {
        this.taskId = taskId;
        this.startTime = startTime /1000; // storing the time in seconds not in milliseconds
        this.duration = duration;
    }
}
