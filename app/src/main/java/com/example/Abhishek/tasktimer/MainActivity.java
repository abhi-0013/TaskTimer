package com.example.Abhishek.tasktimer;

import android.annotation.SuppressLint;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;



import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

//import com.example.Abhishek.tasktimer.debug.TestData;

import java.security.InvalidParameterException;

public class MainActivity extends AppCompatActivity implements RecyclerViewAdapterClass.OnTaskClickListener,
                                                                addEdit_fragment.OnSaveClicked,
                                                                DialogBox.DialogEvents {
    private static final String TAG = "MainActivity";
//    private ActivityMainBinding binding;

    // the below variable is used to check if the activity is in 2 plane mode or not
    private boolean mTwoPlane = false;
    private static final int DIALOG_ID_DELETE = 1;      // id for delete dialog box
    private static final int DIALOG_ID_EDIT_CANCEL = 2; // id for cancel or save edited work dialog box
    private static final int DIALOG_ID_CANCEL_EDIT_UP = 3;
    private AlertDialog mDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: called");
//        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        if(findViewById(R.id.task_detail_container) != null){
//            // The detail container view  will be present only in the landscape mode
//            // thus if this view is present we display the main activity in two plane mode
//            mTwoPlane = true;
//        }

        // now setting the value of mTwoPlane on the basis of orientation
        mTwoPlane = (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE);

        FragmentManager fragmentManager =getSupportFragmentManager();
        boolean editing = fragmentManager.findFragmentById(R.id.task_detail_container)!=null;       // to check wether editing in landscape mode or not

        // getting references to the two fragments
        View mainFragment = findViewById(R.id.fragment);
        View addEditFragment = findViewById(R.id.task_detail_container);

        if(mTwoPlane){
            Log.d(TAG, "onCreate: in two plane mode ");
            mainFragment.setVisibility(View.VISIBLE);
            addEditFragment.setVisibility(View.VISIBLE);
        }else if(editing){
            Log.d(TAG, "onCreate: portrait mode and in editing ");
            mainFragment.setVisibility(View.GONE);
        }else{
            Log.d(TAG, "onCreate: in Portrait and not editing");
            addEditFragment.setVisibility(View.GONE);
            mainFragment.setVisibility(View.VISIBLE);
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        if(BuildConfig.DEBUG){
            MenuItem menuItem = menu.findItem(R.id.main_menue_Generate);
            menuItem.setVisible(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch(id){
            case R.id.main_menu_AddTask:
                taskEditRequest(null);
                break;
            case R.id.main_menu_showDurations:
                startActivity(new Intent(this, DurationReport.class));
                break;
            case R.id.main_menu_action_settings:
                break;
            case R.id.main_menue_showAbout:
                showAboutDialog();
                break;
//            case R.id.main_menue_Generate:
//                TestData.generateTestData(getContentResolver());
//                Toast.makeText(this,"generate is called ",Toast.LENGTH_LONG).show();
//                break;
            case android.R.id.home:
                Log.d(TAG, "onOptionsItemSelected: home button selected ");
                addEdit_fragment addEditFragment = (addEdit_fragment) getSupportFragmentManager().findFragmentById(R.id.task_detail_container);
                if(addEditFragment.canClose()){
                    return super.onOptionsItemSelected(item);
                }else{
                    showConfirmationDialog(DIALOG_ID_CANCEL_EDIT_UP);
                    return true; // this shows that we are handling this widget function in this case
                }
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("SetTextI18n")
    private void showAboutDialog(){
        View messageView = getLayoutInflater().inflate(R.layout.about_dialog,null ,false);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(R.string.app_name);
        builder.setIcon(R.mipmap.ic_launcher);

        builder.setView(messageView);

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if((mDialog != null) && mDialog.isShowing()){
                    mDialog.dismiss();
                }
            }
        });
        TextView tv = (TextView) messageView.findViewById(R.id.about_dial_version_id);
        tv.setText("v"+BuildConfig.VERSION_NAME);

        mDialog = builder.create();
        mDialog.setCanceledOnTouchOutside(true);
        mDialog.show();
    }

    @Override
    public void onSaveClicked() {
        Log.d(TAG, "onSaveClicked: starts");
        FragmentManager fragmentManager= getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.task_detail_container);
        if(fragment != null){
            getSupportFragmentManager().
                    beginTransaction().
                    remove(fragment).
                    commit();
        }

        if(!mTwoPlane){
            View mainFragment = findViewById(R.id.fragment);
            View addEditFragment = findViewById(R.id.task_detail_container);

            mainFragment.setVisibility(View.VISIBLE);
            addEditFragment.setVisibility(View.GONE);
        }
    }

    @Override
    public void onEditClick(@NonNull Task task) {
        taskEditRequest(task);
    }

    @Override
    public void onDeleteClick(@NonNull Task task) {
        Log.d(TAG, "onDeleteClick: is called ");
        DialogBox dialogBox = new DialogBox();
        Bundle args = new Bundle();
        args.putInt(DialogBox.DIALOG_ID,DIALOG_ID_DELETE);
        args.putString(DialogBox.DIALOG_MESSAGE , getString(R.string.Dialog_deleteMessage , task.getId() ,task.getName()));
        args.putInt(DialogBox.DIALOG_POSITIVE_RID ,R.string.Dialog_Button_delete_positve);

        args.putLong("TaskId" , task.getId());
        dialogBox.setArguments(args);
        dialogBox.show(getSupportFragmentManager(),null);
    }

    @Override
    public void onTaskLongClick(@NonNull Task task) {
        // implementing itjust to satisfy the interface
        // main operation handled in mainFragment
    }

    @Override
    public void OnPositiveDialogResult(int dialog_id, Bundle args) {
        Log.d(TAG, "OnPositiveDialogResult: is called");
        switch (dialog_id){
            case DIALOG_ID_DELETE:
                Long taskId = args.getLong("TaskId");

                // the below line is to check at run time if task id is zero then it means it does not present in the database
                // it does not execute in the production app
                if(BuildConfig.DEBUG && taskId == 0) throw  new AssertionError("Task id is zero ");
                getContentResolver().delete(TaskContract.buildTaskUri(taskId), null,null);
                break;
            case DIALOG_ID_CANCEL_EDIT_UP:
            case DIALOG_ID_EDIT_CANCEL:
                // no action required
                break;
            default:
                throw new InvalidParameterException("Unknown id for a dialog box ");
        }
    }

    @Override
    public void OnNegativeDialogResult(int dialog_id, Bundle args) {
        Log.d(TAG, "OnNegativeDialogResult: is called ");
        switch(dialog_id){
            case DIALOG_ID_DELETE:
                // No action required
                break;
            case DIALOG_ID_CANCEL_EDIT_UP:
            case DIALOG_ID_EDIT_CANCEL:
                // First we check if we are editing or not
                FragmentManager fragmentManager = getSupportFragmentManager();
                Fragment fragment = fragmentManager.findFragmentById(R.id.task_detail_container);
                if(fragment != null){
                    // we are editing, remove the editing fragment
                    fragmentManager.beginTransaction().remove(fragment).commit();

                    // second step is to check for orientaion
                    if(mTwoPlane){
                        // we are in landscape so we quit since back button is pressed
                        if(dialog_id == DIALOG_ID_EDIT_CANCEL){
                        finish();
                        }
                    }else{
                        View mainFragment = findViewById(R.id.fragment);
                        View addEditFragment = findViewById(R.id.task_detail_container);

                        addEditFragment.setVisibility(View.GONE);
                        mainFragment.setVisibility(View.VISIBLE);
                    }
                }else{
                    // not editing so just quit the program
                    finish();
                }
                break;
            default:
                throw new InvalidParameterException("Unknown id for a dialog box ");
        }

    }

    @Override
    public void OnCancelDialogResult(int dialog_id) {
        Log.d(TAG, "OnCancelDialogResult: is called");
    }

    private void taskEditRequest(Task task){
        Log.d(TAG, "taskEditRequest: starts");

        addEdit_fragment add_edit_fragment = new addEdit_fragment();

        // the below bundle is used to pass task as argument to the
        // fragment so that fragment can work as independent module
        Bundle arguments = new Bundle();
        arguments.putSerializable(Task.class.getSimpleName(),task);
        add_edit_fragment.setArguments(arguments);

//        // setting fragment in the framelayout container in landscape layout
        getSupportFragmentManager().beginTransaction().replace(R.id.task_detail_container,add_edit_fragment).commit();

        if(!mTwoPlane){
            Log.d(TAG, "taskEditRequest: editing task in portrait mode ");
            View mainFragment = findViewById(R.id.fragment);
            View addEditFragment = findViewById(R.id.task_detail_container);
            mainFragment.setVisibility(View.GONE);
            addEditFragment.setVisibility(View.VISIBLE);
        }
        Log.d(TAG, "taskEditRequest: ends");
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed: is called ");
        FragmentManager fragmentManager = getSupportFragmentManager();
        addEdit_fragment addEditFragment = (addEdit_fragment) fragmentManager.findFragmentById(R.id.task_detail_container);

        if((addEditFragment == null) || addEditFragment.canClose()){
            Log.d(TAG, "onBackPressed: is called");
            super.onBackPressed();
        }else{
            // show the dialog to get confirmation to quit editing
            showConfirmationDialog(DIALOG_ID_EDIT_CANCEL);
        }
    }

    private void showConfirmationDialog(int dialogId){
        Log.d(TAG, "showConfirmationDialog: calling....");

        DialogBox dialogBox = new DialogBox();
        Bundle args = new Bundle();
        args.putInt(DialogBox.DIALOG_ID, dialogId);
        args.putString(DialogBox.DIALOG_MESSAGE , getString(R.string.Dialog_edit_save));
        args.putInt(DialogBox.DIALOG_POSITIVE_RID ,R.string.Dialog_button_edit_save);
        args.putInt(DialogBox.DIALOG_NEGATIVE_RID,R.string.Dialog_button_edit_save_negative);

        dialogBox.setArguments(args);
        dialogBox.show(getSupportFragmentManager(),null);
    }
    @Override
    protected void onStop() {
        super.onStop();
        if((mDialog != null) && mDialog.isShowing()){
            mDialog.dismiss();
        }
    }
}