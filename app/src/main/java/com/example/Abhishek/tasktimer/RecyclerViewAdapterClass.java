package com.example.Abhishek.tasktimer;

import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerViewAdapterClass extends RecyclerView.Adapter<RecyclerViewAdapterClass.TaskViewHolder> {
    private static final String TAG = "RecyclerViewAdapterClas";
    private Cursor mcursor;

    private OnTaskClickListener mOnTaskClickListener;
    interface OnTaskClickListener{
        void onEditClick(@NonNull Task task);
        void onDeleteClick(@NonNull Task task);
        void onTaskLongClick(@NonNull Task task);
    }
    public RecyclerViewAdapterClass(Cursor mcursor , OnTaskClickListener listener) {
//        Log.d(TAG, "RecyclerViewAdapterClass: Constructor is called");
        this.mcursor = mcursor;
        mOnTaskClickListener = listener;
    }

    @NonNull
    @Override
    public RecyclerViewAdapterClass.TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        Log.d(TAG, "onCreateViewHolder: new view requested  ");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_item_list , parent,false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TaskViewHolder holder, int position) {
//        Log.d(TAG, "onBindViewHolder: starts");
        if((mcursor == null) || (mcursor.getCount() == 0)){
//            Log.d(TAG, "onBindViewHolder: providing instructions.+.+.+  ");
            holder.name.setText(R.string.Instruction_name);
            holder.description.setText(R.string.Instruction_description);
            holder.meditButton.setVisibility(View.GONE); // try with invisible to
            holder.mdeleteButton.setVisibility(View.GONE);
        }else{
            if(!mcursor.moveToPosition(position)){
                throw new IllegalStateException("Couldn't move to the desired position " + position);
            }
            try{
                Task task = new Task(mcursor.getLong(mcursor.getColumnIndexOrThrow(TaskContract.Columns._ID)) ,
                                    mcursor.getString(mcursor.getColumnIndexOrThrow(TaskContract.Columns.TASK_NAME)),
                                    mcursor.getString(mcursor.getColumnIndexOrThrow(TaskContract.Columns.TASK_DESCRIPTION)),
                                    mcursor.getInt(mcursor.getColumnIndexOrThrow(TaskContract.Columns.TASK_SORTORDER)));
                holder.name.setText(task.getName());
                holder.description.setText(task.getDescription());
                holder.meditButton.setVisibility(View.VISIBLE);         // need to define listener for these
                holder.mdeleteButton.setVisibility(View.VISIBLE);       // buttons

                View.OnClickListener buttonListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        Log.d(TAG, "onClick: starts ");
                        switch (v.getId()){
                            case   R.id.tli_edit:
                                if(mOnTaskClickListener != null){
                                    mOnTaskClickListener.onEditClick(task);
                                }
                                break;
                            case R.id.tli_delete:
                                if(mOnTaskClickListener != null){
                                    mOnTaskClickListener.onDeleteClick(task);
                                }
                        }
                    }
                };

                View.OnLongClickListener buttonLongistener = new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
//                        Log.d(TAG, "onLongClick: starts");
                        if(mOnTaskClickListener != null){
                            mOnTaskClickListener.onTaskLongClick(task);
                            return true;
                        }
                        return false;
                    }
                };
                holder.mdeleteButton.setOnClickListener(buttonListener);
                holder.meditButton.setOnClickListener(buttonListener);
                holder.itemView.setOnLongClickListener(buttonLongistener);
            }catch (IllegalArgumentException e){
                Log.e(TAG, "onBindViewHolder: column doesn 't exist  "+ e.getMessage() );
            }

        }

    }

    @Override
    public int getItemCount() {
//        Log.d(TAG, "getItemCount: starts");
        if((mcursor == null) || (mcursor.getCount() == 0)){
            return 1; // because we populate the list by instruction view
        }else{
            return mcursor.getCount();
        }
    }

    /**
     * Swap in a new Cursor, returning the old Cursor.
     * The returned old Cursor is <em> not</em> closed.
     *
     * @param cursor The new cursor to be used
     * @return Returns the previously set Cursor, or null if there wasn't one
     * If the given new Cursor is the same instance as the previously set
     * Cursor, null is also returned.
     */
    Cursor swapCursor(Cursor cursor){
        if(mcursor == cursor){
            return null;
        }
        final Cursor oldCursor  = mcursor;
        mcursor = cursor;
        if(cursor != null){
            // notify the observers about the change in dataset
            notifyDataSetChanged();
        }else{
            //notify dataset about the lack of datasets
            notifyItemRangeRemoved(0,getItemCount());
        }
        return oldCursor;
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder{
        private static final String TAG = "TaskViewHolder";
        TextView name = null;
        TextView description = null;
        ImageButton meditButton = null;
        ImageButton mdeleteButton = null;
        public TaskViewHolder(View itemView){
                super(itemView);
//            Log.d(TAG, "TaskViewHolder: starts");
            this.name = (TextView) itemView.findViewById(R.id.tli_name);
            this.description = (TextView) itemView.findViewById(R.id.tli_description);
            this.meditButton = (ImageButton) itemView.findViewById(R.id.tli_edit);
            this.mdeleteButton = (ImageButton) itemView.findViewById(R.id.tli_delete);
        }
    }
}
