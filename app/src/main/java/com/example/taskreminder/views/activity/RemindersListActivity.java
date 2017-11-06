package com.example.taskreminder.views.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.taskreminder.R;
import com.example.taskreminder.ReminderManagerService;
import com.example.taskreminder.database.ReminderContract;
import com.example.taskreminder.database.ReminderDataHelper;
import com.example.taskreminder.views.ReminderAdapter;

public class RemindersListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>
{
    private ReminderDataHelper mDataHelper;
    private TextView mEmptyView;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private ReminderAdapter mAdapter;
    private String mType=ReminderContract.PATH_ALL;
    private Cursor mCursor;
    private RecyclerView.LayoutManager layoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminders_list);

        mDataHelper = new ReminderDataHelper(RemindersListActivity.this);

        getSupportLoaderManager().initLoader(0, null,this);

        mRecyclerView = (RecyclerView) findViewById(R.id.reminder_list);
        mEmptyView = (TextView) findViewById(R.id.empty);

        layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        Intent intent = new Intent(getApplicationContext(),ReminderManagerService.class);
        startService(intent);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri;
        switch(mType){
            case ReminderContract.PATH_ALL:
                uri = ReminderContract.All.CONTENT_URI;
                break;
            case ReminderContract.PATH_DATETIME:
                uri = ReminderContract.DateTimeAlert.CONTENT_URI;
                break;
            case ReminderContract.PATH_LOC:
                uri = ReminderContract.LocationAlert.CONTENT_URI;
                break;
            default:
                return null;
        }
        return new CursorLoader(RemindersListActivity.this, uri, null, null, null, null);
    }

    private void emptyCheck(String type) {
        if (mDataHelper.isEmpty(type)) {
            mEmptyView.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        } else {
            mEmptyView.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void invalidate(Cursor data) {
        mAdapter = new ReminderAdapter(RemindersListActivity.this, data);
        mAdapter.setHasStableIds(true);
        mRecyclerView.swapAdapter(mAdapter, false);
        emptyCheck(mType);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursor = data;
        invalidate(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_reminders_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_add) {
            displayDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void displayDialog()
    {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(RemindersListActivity.this);
        builderSingle.setTitle("Select Reminder Type");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(RemindersListActivity.this, android.R.layout.simple_list_item_1);
        arrayAdapter.add(getString(R.string.datetime_reminder));
        arrayAdapter.add(getString(R.string.location_reminder));

        builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String strName = arrayAdapter.getItem(which);
                if(strName.equals(getString(R.string.datetime_reminder)))
                {
                    startActivity(new Intent(RemindersListActivity.this, DateTimeReminderActivity.class));
                }
                else
                {
                    startActivity(new Intent(RemindersListActivity.this, MapsActivity.class));
                }
            }
        });
        builderSingle.show();
    }
}
