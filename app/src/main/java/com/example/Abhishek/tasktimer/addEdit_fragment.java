package com.example.Abhishek.tasktimer;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.Abhishek.tasktimer.databinding.FragmentAddEditBinding;

public class addEdit_fragment extends Fragment {
    private static final String TAG = "addEdit_fragment";
    public enum FragmentEditmode {EDIT , ADD}
    private FragmentEditmode mMode;
    private EditText mNameTextView;
    private EditText mDescriptionTextView;
    private EditText mSortOrderTextView;
    private Button mSaveButton;

    private OnSaveClicked monSaveClickedl;

    interface OnSaveClicked{
        void onSaveClicked();
    }
    public addEdit_fragment() {
        Log.d(TAG, "addEdit_fragment: this is called");
    }

    @Override
    public void onAttach(@NonNull Context context) {
        Log.d(TAG, "onAttach: starts");
        super.onAttach(context);

        // Activities containing this fragment must implement this interface
        Activity activity = getActivity();
        if(!(activity instanceof OnSaveClicked)){
            throw new ClassCastException(activity.getClass().getSimpleName() +
                    " must implement addEditFragment.OnSaveClicked interface");
        }
        monSaveClickedl = (OnSaveClicked) activity;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onActivityCreated: starts");
        super.onActivityCreated(savedInstanceState);
        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void onDetach() {
        Log.d(TAG, "onDetach: starts");
        super.onDetach();
        monSaveClickedl = null;
        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(false);
        }
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        View view = inflater.inflate(R.layout.fragment_add_edit,container,false);

        mNameTextView = (EditText) view.findViewById(R.id.addedit_task_name);
        mDescriptionTextView = (EditText) view.findViewById(R.id.addedit_description);
        mSortOrderTextView = (EditText) view.findViewById(R.id.addedit_sortOrder);
        mSaveButton = (Button) view.findViewById(R.id.addedit_saveButton);

        if(!mSaveButton.isEnabled()){
            mSaveButton.setEnabled(true);
        }

        // this line was an incorrect way to pass arguments to a fragment as it makes
        // fragment to be specific to one activity since it had to get the argument using keys set by an activity
//        Bundle arguments = getActivity().getIntent().getExtras();

        // proper method is below
        Bundle arguments = getArguments();

        final Task task;
        if(arguments != null){
            Log.d(TAG, "onCreateView: retriving task details");
            task = (Task) arguments.getSerializable(Task.class.getSimpleName());
            if(task != null){
                Log.d(TAG, "onCreateView: task found setting mode to edit mode ....");
                mNameTextView.setText(task.getName());
                mDescriptionTextView.setText(task.getDescription());
                mSortOrderTextView.setText(Integer.toString(task.getSortOrder()));
                mMode = FragmentEditmode.EDIT;

            }else{
                Log.d(TAG, "onCreateView: task found is null so shiftng to add mode");
                mMode = FragmentEditmode.ADD;
            }
        }else{
            Log.d(TAG, "onCreateView: arguments is null ADDing the task++++++  ");
            task = null;
            mMode = FragmentEditmode.ADD;
        }

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Update the database if atleast one field has changed
                int so_numl ; // used to store numeric value of sortorder
                if(mSortOrderTextView.length()>0){
                    so_numl = Integer.parseInt(mSortOrderTextView.getText().toString());
                }else{
                    so_numl =0;
                }

                ContentResolver contentResolver = getActivity().getContentResolver();
                ContentValues values = new ContentValues();

                switch(mMode){
                    case EDIT:
                        if(!mNameTextView.getText().toString().equals(task.getName())){
                            values.put(TaskContract.Columns.TASK_NAME , mNameTextView.getText().toString());
                        }
                        if(!mDescriptionTextView.getText().toString().equals(task.getDescription())){
                            values.put(TaskContract.Columns.TASK_DESCRIPTION , mDescriptionTextView.getText().toString());
                        }
                        if(so_numl != task.getSortOrder()){
                            values.put(TaskContract.Columns.TASK_SORTORDER, so_numl);
                        }
                        if(values.size() != 0){
                            Log.d(TAG, "onClick: updating the task in editing mode ====== " );
                            contentResolver.update(TaskContract.buildTaskUri(task.getId()),values,null,null);
                        }
                        Toast.makeText(getActivity().getApplicationContext(),"successfully edited the task",Toast.LENGTH_SHORT).show();
                        break;
                    case ADD:
                        if(mNameTextView.length()>0){
                            Log.d(TAG, "onClick: adding new task ==== ");
                            values.put(TaskContract.Columns.TASK_NAME,mNameTextView.getText().toString());
                            values.put(TaskContract.Columns.TASK_DESCRIPTION,mDescriptionTextView.getText().toString());
                            values.put(TaskContract.Columns.TASK_SORTORDER,so_numl);
                            contentResolver.insert(TaskContract.CONTENT_URI,values);
                        }
                        Toast.makeText(getActivity().getApplicationContext(),"successfully added the task",Toast.LENGTH_SHORT).show();
                        mSaveButton.setEnabled(false);
                        break;
                    default:
                        throw new IllegalArgumentException(mMode + "is not valid");
                }
                Log.d(TAG, "onClick: done editing////// ");

                if(monSaveClickedl != null){
                    monSaveClickedl.onSaveClicked();
                }
            }
        });

        Log.d(TAG, "onCreateView: exiting onCreatview ..... ");
        return view;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
    public boolean canClose(){
        return false;
    }

}