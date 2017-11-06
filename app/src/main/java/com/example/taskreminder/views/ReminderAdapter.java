package com.example.taskreminder.views;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.taskreminder.R;
import com.example.taskreminder.database.ReminderContract;
import com.example.taskreminder.database.ReminderDataHelper;

import java.text.SimpleDateFormat;

import static com.example.taskreminder.database.ReminderContract.PATH_DATETIME;

public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ViewHolder> {

    private Context mContext;
    private Cursor mCursor;
    private ReminderDataHelper mDatabase;
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm, MMM d ''yy");


    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView content;
        public TextView time;
        public TextView location;

        public ViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            content = (TextView) view.findViewById(R.id.reminder);
            time = (TextView) view.findViewById(R.id.timeLabel);
            location = (TextView) view.findViewById(R.id.locationLabel);
        }
    }

    public ReminderAdapter(Context context, Cursor cursor) {
        mContext = context;
        mCursor = cursor;
        mDatabase = new ReminderDataHelper(mContext);
    }

    // inflating layout from XML and returning the holder
    @Override
    public ReminderAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        if (mDatabase.isEmpty(ReminderContract.PATH_ALL)) {
            View emptyView = parent.findViewById(R.id.empty);
            return new ViewHolder(emptyView);
        } else {

            // Inflate the custom layout
            View reminderView = inflater.inflate(R.layout.list_item_layout, parent, false);

            // Return a new holder
            ViewHolder viewHolder = new ViewHolder(reminderView);
            return viewHolder;
        }
    }

    // Populating the items in the holder
    @Override
    public void onBindViewHolder(ReminderAdapter.ViewHolder viewHolder, int position) {
        mCursor.moveToPosition(position);
        String type = mCursor.getString(mCursor.getColumnIndex(ReminderDataHelper.DB_COLUMN_TYPE));
        if (type.equalsIgnoreCase(PATH_DATETIME)) {
            viewHolder.time.setText(timeFormat.format(mCursor.getLong(mCursor.getColumnIndex(ReminderDataHelper.DB_COLUMN_TIME))));
            viewHolder.time.setVisibility(View.VISIBLE);
            viewHolder.location.setVisibility(View.GONE);

        } else {
            viewHolder.time.setVisibility(View.GONE);
            viewHolder.location.setVisibility(View.VISIBLE);

            String locx = mCursor.getString(mCursor.getColumnIndex(ReminderDataHelper.DB_COLUMN_LOCATION_X));
            String locy = mCursor.getString(mCursor.getColumnIndex(ReminderDataHelper.DB_COLUMN_LOCATION_Y));
            String locname = mCursor.getString(mCursor.getColumnIndex(ReminderDataHelper.DB_COLUMN_LOCATION_NAME));
            String locationdet = locx + " " + locy + " " + locname;
            viewHolder.location.setText(locationdet);

        }
        viewHolder.title.setText(mCursor.getString(mCursor.getColumnIndex(ReminderDataHelper.DB_COLUMN_TITLE)));
        viewHolder.content.setText(mCursor.getString(mCursor.getColumnIndex(ReminderDataHelper.DB_COLUMN_CONTENT)));
    }

    public int getItemCount() {
        return mCursor.getCount();
    }


    @Override
    public long getItemId(int position) {
        mCursor.moveToPosition(position);
        return (long) mCursor.getInt(mCursor.getColumnIndex(ReminderDataHelper.DB_COLUMN_ID));
    }


}


