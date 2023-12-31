package com.example.Abhishek.tasktimer;

import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.security.InvalidParameterException;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class DurationReport extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,
        DatePickerDialog.OnDateSetListener,
        DialogBox.DialogEvents,
        View.OnClickListener {

    private static final String TAG = "DurationsReport";

    private static final int LOADER_ID = 1;

    public static final int DIALOG_FILTER = 1;
    public static final int DIALOG_DELETE = 2;

    private static final String SELECTION_PARAM = "SELECTION";
    private static final String SELECTION_ARGS_PARAM = "SELECTION_ARGS";
    private static final String SORT_ORDER_PARAM = "SORT_ORDER";

    public static final String DELETION_DATE = "DELETION_DATE";

    public static final String CURRENT_DATE = "CURRENT_DATE";
    public static final String DISPLAY_WEEK = "DISPLAY_WEEK";

    // module level arguments - so when we change sort order, for example, the selection
    // is retained (and vice-versa).
    private Bundle mArgs = new Bundle();
    private boolean mDisplayWeek = true;

    private DurationRV_Adapter mAdapter;

    private final GregorianCalendar mCalendar = new GregorianCalendar();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_duration_report);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            Log.d(TAG, "onCreate: inside action bar");
            actionBar.setDisplayHomeAsUpEnabled(true); // TODO not working
        }

        if(savedInstanceState != null) {
            long timeInMillis = savedInstanceState.getLong(CURRENT_DATE, 0);
            // it'll be zero when the Activity's first created, so don't set the value
            if(timeInMillis != 0) {
                mCalendar.setTimeInMillis(timeInMillis);
                // Make sure the time part is cleared, because we filter the database by seconds
                mCalendar.clear(GregorianCalendar.HOUR_OF_DAY);
                mCalendar.clear(GregorianCalendar.MINUTE);
                mCalendar.clear(GregorianCalendar.SECOND);
            }
            mDisplayWeek = savedInstanceState.getBoolean(DISPLAY_WEEK, true);
        }

        applyFilter();

        // Set the listener for the buttons so we can sort the report.
        TextView taskName = findViewById(R.id.td_name_heading);
        taskName.setOnClickListener(this);

        TextView taskDesc = findViewById(R.id.td_description_heading);
        if (taskDesc != null) {
            taskDesc.setOnClickListener(this);
        }

        TextView taskDate = findViewById(R.id.td_start_heading);
        taskDate.setOnClickListener(this);

        TextView taskDuration = findViewById(R.id.td_duration_heading);
        taskDuration.setOnClickListener(this);

        RecyclerView recyclerView = findViewById(R.id.td_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        // Create an empty adapter we will use, to display the loaded data.
        if (mAdapter == null) {
            mAdapter = new DurationRV_Adapter(this, null);
        }
        recyclerView.setAdapter(mAdapter);

        LoaderManager.getInstance(this).initLoader(LOADER_ID, mArgs, this);

    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "onClick: called");
        switch(v.getId()){
            case R.id.td_name_heading:
                mArgs.putString(SORT_ORDER_PARAM,DurationContract.Columns.DURATIONS_NAME);
                break;
            case R.id.td_description_heading:
                mArgs.putString(SORT_ORDER_PARAM, DurationContract.Columns.DURATIONS_DESCRIPTION);
                break;
            case R.id.td_start_heading:
                mArgs.putString(SORT_ORDER_PARAM, DurationContract.Columns.DURATION_START_DATE);
                break;
            case R.id.td_duration_heading:
                mArgs.putString(SORT_ORDER_PARAM, DurationContract.Columns.DURATIONS_DURATION);
                break;
        }
        LoaderManager.getInstance(this).restartLoader(LOADER_ID, mArgs, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_report, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case R.id.rm_filter_period:
                mDisplayWeek = !mDisplayWeek; // was showing a week, so now show a day or vice-versa.
                applyFilter();
                invalidateOptionsMenu();    // force call to onPrepareOptionsMenu to redraw our changed menu
                getSupportLoaderManager().restartLoader(LOADER_ID, mArgs, this);
                return true;
            case R.id.rm_filter_date:
                showDatePickerDialog("Select date for report", DIALOG_FILTER); // The actual filtering is done in onDateSet();
                return true;
            case R.id.rm_delete:
                showDatePickerDialog(getString(R.string.date_title_delete), DIALOG_DELETE); // The actual deleting is done in onDateSet();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.rm_filter_period);
        if (item != null) {
            // switch icon and title to represent 7 days or 1 day
            // as appropriate to the future function of the menu item.
            if (mDisplayWeek) {
                item.setIcon(R.drawable.baseline_filter_1_24);
                item.setTitle(R.string.rm_title_filter_day);
            } else {
                item.setIcon(R.drawable.baseline_filter_7_24);
                item.setTitle(R.string.rm_title_filter_week);
            }
        }
        return super.onPrepareOptionsMenu(menu);
    }

    private void showDatePickerDialog(String title, int dialogId) {
        Log.d(TAG, "Entering showDatePickerDialog");
        DialogFragment dialogFragment = new DatePickerFragment();

        Bundle arguments = new Bundle();
        arguments.putInt(DatePickerFragment.DATE_PICKER_ID, dialogId);
        arguments.putString(DatePickerFragment.DATE_PICKER_TITLE, title);
        arguments.putSerializable(DatePickerFragment.DATE_PICKER_DATE, mCalendar.getTime());

        dialogFragment.setArguments(arguments);
        dialogFragment.show(getSupportFragmentManager(), "datePicker");
        Log.d(TAG, "Exiting showDatePickerDialog");
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Log.d(TAG, "onDateSet: called");

        // Check the id, so we know what to di wht the result
        int dialogId = (int) view.getTag();
        mCalendar.set(year, month, dayOfMonth, 0, 0, 0);

        switch (dialogId) {
            case DIALOG_FILTER:
                applyFilter();
                LoaderManager.getInstance(this).restartLoader(LOADER_ID, mArgs, this);
                break;
            case DIALOG_DELETE:
                String fromDate = android.text.format.DateFormat.getDateFormat(this)
                        .format(mCalendar.getTimeInMillis());
                DialogBox dialog = new DialogBox();
                Bundle args = new Bundle();
                args.putInt(DialogBox.DIALOG_ID, 1);  // We only have 1 dialog in this activity
                args.putString(DialogBox.DIALOG_MESSAGE, getString(R.string.delete_timings_message, fromDate));
                args.putLong(DELETION_DATE, mCalendar.getTimeInMillis());
                dialog.setArguments(args);
                dialog.show(getSupportFragmentManager(), null);
                break;
            default:
                throw new IllegalArgumentException("Invalid mode when receiving DatePickerDialog result");
        }
    }

    private void applyFilter() {
        Log.d(TAG, "Entering applyFilter");

        if (mDisplayWeek) {
            // show records for the entire week

            Date currentCalendarDate = mCalendar.getTime(); // store the time, so we can put it back.

            // we have a date, so find out which day of week it is.
            int dayOfWeek = mCalendar.get(GregorianCalendar.DAY_OF_WEEK);
            int weekStart = mCalendar.getFirstDayOfWeek();
            Log.d(TAG, "applyFilter: first day of calendar week is " + weekStart);
            Log.d(TAG, "applyFilter: dayOfWeek is " + dayOfWeek);
            Log.d(TAG, "applyFilter: date is " + mCalendar.getTime());

            // calculate week start and end dates
            mCalendar.set(GregorianCalendar.DAY_OF_WEEK, weekStart);

            String startDate = String.format(Locale.US, "%04d-%02d-%02d",
                    mCalendar.get(GregorianCalendar.YEAR),
                    mCalendar.get(GregorianCalendar.MONTH) + 1,
                    mCalendar.get(GregorianCalendar.DAY_OF_MONTH));

            mCalendar.add(GregorianCalendar.DATE, 6); // move forward 6 days to get the last day of the week

            String endDate = String.format(Locale.US, "%04d-%02d-%02d",
                    mCalendar.get(GregorianCalendar.YEAR),
                    mCalendar.get(GregorianCalendar.MONTH) + 1,
                    mCalendar.get(GregorianCalendar.DAY_OF_MONTH));
            String[] selectionArgs = new String[]{startDate, endDate};
            // put the calendar back to where it was before we started jumping back and forth.
            mCalendar.setTime(currentCalendarDate);

            Log.d(TAG, "In applyFilter(7), Start date is " + startDate + ", End Date is " + endDate);
            mArgs.putString(SELECTION_PARAM, "StartDate Between ? AND ?");
            mArgs.putStringArray(SELECTION_ARGS_PARAM, selectionArgs);

        } else {
            // re-query for the current day.
            String startDate = String.format(Locale.US, "%04d-%02d-%02d",
                    mCalendar.get(GregorianCalendar.YEAR),
                    mCalendar.get(GregorianCalendar.MONTH) + 1,
                    mCalendar.get(GregorianCalendar.DAY_OF_MONTH));
            String[] selectionArgs = new String[]{startDate};
            Log.d(TAG, "In applyFilter(1), Start date is " + startDate);
            mArgs.putString(SELECTION_PARAM, "StartDate = ?");
            mArgs.putStringArray(SELECTION_ARGS_PARAM, selectionArgs);
        }
    }

    private void deleteRecords(long timeInMillis) {
        Log.d(TAG, "Entering deleteRecords");

        long longDate = timeInMillis / 1000; // We need time in seconds not milliseconds
        String[] selectionArgs = new String[] { Long.toString(longDate)};
        String selection = TimingContract.Columns.TIMING_START_TIME + " < ?";

        Log.d(TAG, "Delete records prior to " + longDate);

        ContentResolver contentResolver = getContentResolver();
        contentResolver.delete(TimingContract.CONTENT_URI, selection, selectionArgs);
        applyFilter();
        LoaderManager.getInstance(this).restartLoader(LOADER_ID, mArgs, this);
        Log.d(TAG, "Exiting deleteRecords");
    }

    @Override
    public void OnPositiveDialogResult(int dialog_id, Bundle args) {
        Log.d(TAG, "onPositiveDialogResult: called");
        long deleteDate = args.getLong(DELETION_DATE);
        // clear all records from Timings table prior to the date selected.
        deleteRecords(deleteDate);
        // re-query, in case we've deleted records that currently being shown
        LoaderManager.getInstance(this).restartLoader(LOADER_ID, mArgs, this);
    }

    @Override
    public void OnNegativeDialogResult(int dialog_id, Bundle args) {

    }

    @Override
    public void OnCancelDialogResult(int dialog_id) {

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(CURRENT_DATE, mCalendar.getTimeInMillis());
        outState.putBoolean(DISPLAY_WEEK, mDisplayWeek);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case LOADER_ID:

                String[] projection = {BaseColumns._ID,
                        DurationContract.Columns.DURATIONS_NAME,
                        DurationContract.Columns.DURATIONS_DESCRIPTION,
                        DurationContract.Columns.DURATIONS_START_TIME,
                        DurationContract.Columns.DURATION_START_DATE,
                        DurationContract.Columns.DURATIONS_DURATION};

                String selection = null;
                String[] selectionArgs = null;
                String sortOrder = null;

                if (args != null) {
                    selection = args.getString(SELECTION_PARAM);
                    selectionArgs = args.getStringArray(SELECTION_ARGS_PARAM);
                    sortOrder = args.getString(SORT_ORDER_PARAM);
                }

                return new CursorLoader(this,
                        DurationContract.CONTENT_URI,
                        projection,
                        selection,
                        selectionArgs,
                        sortOrder);

            default:
                throw new InvalidParameterException(TAG + ".onCreateLoader called with invalid loader id " + id);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d(TAG, "Entering onLoadFinished");
        mAdapter.swapCursor(data);
        int count = mAdapter.getItemCount();

        Log.d(TAG, "onLoadFinished: count is " + count);

    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        Log.d(TAG, "onLoaderReset: starts");
        mAdapter.swapCursor(null);
    }
}