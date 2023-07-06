package com.example.Abhishek.tasktimer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.security.InvalidParameterException;


public class Main_activity_Fragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,
        RecyclerViewAdapterClass.OnTaskClickListener {
    private static final String TAG = "Main_activity_Fragment";
    public static final int LOADER_ID = 0;
    private RecyclerViewAdapterClass madapter;

    private Timing mCurrentTiming = null;

    public Main_activity_Fragment() {
        // Required empty public constructor
        Log.d(TAG, "Main_activity_Fragment: starts _+_+_+_+_+_+_+_+_+  ");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
//        Log.d(TAG, "onActivityCreated: starts");
        super.onActivityCreated(savedInstanceState);

        Activity activity =getActivity();
        if(!(activity instanceof RecyclerViewAdapterClass.OnTaskClickListener)){
            throw new ClassCastException(activity.getClass().getSimpleName() + " must implement RecyclerViewAdapterClass.OnTaskClickListener  interface ");
        }
        LoaderManager.getInstance(this).initLoader(LOADER_ID,null,this);
        setTimingText(mCurrentTiming);
    }

    @Override
    public void onEditClick(@NonNull Task task) {
        Log.d(TAG, "onEditClick: starts");
        RecyclerViewAdapterClass.OnTaskClickListener listener = (RecyclerViewAdapterClass.OnTaskClickListener) getActivity();
        if(listener != null){
        listener.onEditClick(task);
        }
    }

    @Override
    public void onDeleteClick(@NonNull Task task) {
        Log.d(TAG, "onEditClick: starts");
        RecyclerViewAdapterClass.OnTaskClickListener listener = (RecyclerViewAdapterClass.OnTaskClickListener) getActivity();
        if(listener != null){
        listener.onDeleteClick(task);
        }
    }

    @Override
    public void onTaskLongClick(@NonNull Task task) {
        Log.d(TAG, "onTaskLongClick: called");
        if(mCurrentTiming != null){
            if(task.getId() == mCurrentTiming.getTask().getId()){
                // the current task was tapped a second time , so stop timing the task
                saveTiming(mCurrentTiming);
                mCurrentTiming = null;
                setTimingText(null);
                Toast.makeText(getActivity(),"timing is stopped",Toast.LENGTH_LONG).show();
            }else{
                // a new task is being timed, so stop the old one first
                saveTiming(mCurrentTiming);
                mCurrentTiming = new Timing(task);
                setTimingText(mCurrentTiming);
                Toast.makeText(getActivity(),"timing is stopped and started",Toast.LENGTH_LONG).show();
            }
        }else{
            // no task being timed , so start the new task
            mCurrentTiming = new Timing(task);
            setTimingText(mCurrentTiming);
            Toast.makeText(getActivity(),"timing is started",Toast.LENGTH_LONG).show();
        }
    }

    private void saveTiming(@NonNull Timing CurrentTiming){
        Log.d(TAG, "saveTiming: Entering saveTime");

        // if we have an open timing , set the duration and save
        CurrentTiming.setDuration();

        ContentResolver contentResolver = getActivity().getContentResolver();
        ContentValues values = new ContentValues();
        values.put(TimingContract.Columns.TIMING_TASK_ID , CurrentTiming.getTask().getId());
        values.put(TimingContract.Columns.TIMING_START_TIME, CurrentTiming.getstartTime());
        values.put(TimingContract.Columns.TIMING_DURATION,CurrentTiming.getDuration());

        contentResolver.insert(TimingContract.CONTENT_URI,values);
        Log.d(TAG, "saveTiming: Exiting saveTimings");
    }
    private void setTimingText(Timing timing){
        TextView taskName = getActivity().findViewById(R.id.current_task);

        if(timing != null){
            taskName.setText(getString(R.string.current_timing_task, timing.getTask().getName()));
        } else {
            taskName.setText(R.string.no_task_entered);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d(TAG, "onCreateView: starts");
        View view = inflater.inflate(R.layout.fragment_main_activity_, container, false);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.tli_task_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        if (madapter == null) {
        madapter = new RecyclerViewAdapterClass(null,this);
        }
        recyclerView.setAdapter(madapter);
        Log.d(TAG, "onCreateView: ends");
        return view;
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        Log.d(TAG, "onCreateLoader: starts with id "+ id);
        String[] projections ={TaskContract.Columns._ID ,
                                TaskContract.Columns.TASK_NAME,
                                TaskContract.Columns.TASK_DESCRIPTION ,
                                TaskContract.Columns.TASK_SORTORDER};
        // <order by> Tasks.SortOrder, Tasks.Name COLLATE NOCASE
        String SortOrder = TaskContract.Columns.TASK_SORTORDER + ","+ TaskContract.Columns.TASK_NAME + " COLLATE NOCASE";
        switch(id){
            case LOADER_ID:
                return new CursorLoader(getActivity(),
                        TaskContract.CONTENT_URI,
                        projections,
                        null,
                        null,
                        SortOrder);
            default:
                throw new InvalidParameterException(TAG + " .onCreate loader called with invalid id "+ id);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {

        Log.d(TAG, "onLoadFinished: Entering ");
        madapter.swapCursor(data);
        int count = madapter.getItemCount();
        Log.d(TAG, "onLoadFinished: count is "+ count);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

        Log.d(TAG, "onLoaderReset: starts");
        madapter.swapCursor(null);
    }
}