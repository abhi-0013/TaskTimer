package com.example.Abhishek.tasktimer;

import android.content.Context;
import android.database.Cursor;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Date;
import java.util.Locale;

public class DurationRV_Adapter extends RecyclerView.Adapter<DurationRV_Adapter.ViewHolder> {


    private Cursor mCursor;
    private final java.text.DateFormat mdateFormat; // module level so we don't keep instantiating in bindView

    public DurationRV_Adapter(Context context , Cursor cursor){
        this.mCursor = cursor;
        mdateFormat = DateFormat.getDateFormat(context);
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_durations_items , parent,false);

        return new ViewHolder(view) ;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if((mCursor !=null) && (mCursor.getCount() != 0)){
            if(!mCursor.moveToPosition(position)){
                throw new IllegalStateException("Couldn't move cursor to position "+ position);
            }
            String name = mCursor.getString(mCursor.getColumnIndexOrThrow(DurationContract.Columns.DURATIONS_NAME));
            String description = mCursor.getString(mCursor.getColumnIndexOrThrow(DurationContract.Columns.DURATIONS_DESCRIPTION));
            long startTime = mCursor.getLong(mCursor.getColumnIndexOrThrow(DurationContract.Columns.DURATIONS_START_TIME));
            long totalDuration = mCursor.getLong(mCursor.getColumnIndexOrThrow(DurationContract.Columns.DURATIONS_DURATION));

            holder.name.setText((name));
            if(holder.description != null){
                holder.description.setText(description);
            }

            String userDate = mdateFormat.format(startTime * 1000);
            String totaltime = formatDuration(totalDuration);

            holder.startDate.setText(userDate);
            holder.duration.setText(totaltime);

        }
    }



    private String formatDuration(long duration){
// duration is in seconds, convert to hours:minutes:seconds
        // (allowing for >24 hours - so we can't a time data type);
        long hours = duration /3600;
        long remainder = duration - (hours *3600);
        long minutes = remainder / 60;
        long seconds = remainder - (minutes * 60);

        return String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds);
    }

    @Override
    public int getItemCount() {
        return mCursor != null ? mCursor.getCount():0;
    }


    /**
     * Swap in a new Cursor, returning the old Cursor.
     * The returned old Cursor is <em>not</em> closed.
     *
     * @param newCursor The new cursor to be used
     * @return Returns the previously set Cursor, or null if there wasn't one.
     * If the given new Cursor is the same instance as the previously set
     * Cursor, null is also returned.
     */
    Cursor swapCursor(Cursor newCursor) {
        if (newCursor == mCursor) {
            return null;
        }
        int oldCount = getItemCount();

        final Cursor oldCursor = mCursor;
        mCursor = newCursor;
        if(newCursor != null) {
            // notify the observers about the new cursor
            notifyDataSetChanged();
        } else {
            // notify the observers about the lack of a data set
            notifyItemRangeRemoved(0, oldCount);
        }
        return oldCursor;

    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView name;
        TextView description;
        TextView startDate;
        TextView duration;

        public ViewHolder(View itemView){
            super(itemView);
            this.name = itemView.findViewById(R.id.td_name);
            this.description = itemView.findViewById(R.id.td_description);
            this.startDate = itemView.findViewById(R.id.td_start);
            this.duration = itemView.findViewById(R.id.td_duration);
        }

    }


}
