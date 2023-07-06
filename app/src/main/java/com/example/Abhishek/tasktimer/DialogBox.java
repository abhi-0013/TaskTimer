package com.example.Abhishek.tasktimer;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

public class DialogBox extends AppCompatDialogFragment {
    private static final String TAG = "DialogBox";

    public static final String DIALOG_ID = "id";
    public static final String DIALOG_MESSAGE = "message";
    public static final String DIALOG_POSITIVE_RID = "positive_rid";
    public static final String DIALOG_NEGATIVE_RID = "negative_rid";

    interface DialogEvents{
     void OnPositiveDialogResult(int dialog_id , Bundle args);
     void OnNegativeDialogResult(int dialog_id, Bundle args);
     void OnCancelDialogResult(int dialog_id);
    }

    private DialogEvents mdialogEvents;

    @Override
    public void onAttach(@NonNull Context context) {
        Log.d(TAG, "onAttach: Entering onAttach , activity  is "+ context.toString());
        super.onAttach(context);

        // Activities containing this fragment must implement its fragment
        if(!(context instanceof DialogEvents)){
            throw new ClassCastException(context.toString() + " must implement DialogEvents interface to use this fragment ");
        }
        mdialogEvents =(DialogEvents) context;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateDialog: starts");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        final Bundle argumentes = getArguments();
        final int dialog_id;
        String messageString;
        int positiveStringId;
        int negativeStringId;

        if(argumentes != null){
            dialog_id = argumentes.getInt(DIALOG_ID);
            messageString = argumentes.getString(DIALOG_MESSAGE);

            if(dialog_id == 0 || messageString == null){
                throw new IllegalArgumentException("Dialog Id/ message string is not present in the arguments Bundle");
            }
            positiveStringId = argumentes.getInt(DIALOG_POSITIVE_RID);
            if(positiveStringId == 0){
                Log.d(TAG, "onCreateDialog:No string for ok button ./.././././././ ");
                positiveStringId = R.string.ok;
            }
            negativeStringId = argumentes.getInt(DIALOG_NEGATIVE_RID);
            if(negativeStringId == 0){
                Log.d(TAG, "onCreateDialog: NO string for cancell button /../././/./.././ ");
                negativeStringId = R.string.cancel;
            }
        }else{
                throw new IllegalArgumentException("Must pass dialog id and dialog message in the arguments bundle ");
        }

        builder.setMessage(messageString).
                setPositiveButton(positiveStringId, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG, "onClick: positive button is clicked ");
                        if(mdialogEvents != null ){
                            mdialogEvents.OnPositiveDialogResult(dialog_id , argumentes);
                        }
                    }
                })
                .setNegativeButton(negativeStringId, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG, "onClick: negative button is clicked ");
                        if(mdialogEvents != null ){
                            mdialogEvents.OnNegativeDialogResult(dialog_id , argumentes);
                        }
                    }
                });


        return builder.create();
    }


    @Override
    public void onDetach() {
        Log.d(TAG, "onDetach: starts");
        super.onDetach();
        // reset the active callbacks interface , because we don't have an activity any longer
        mdialogEvents = null;
    }

    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        Log.d(TAG, "onCancel: called");
        if( mdialogEvents != null){
            int dialog_id = getArguments().getInt(DIALOG_ID);
            mdialogEvents.OnCancelDialogResult(dialog_id);
        }
    }
}
