/*
 * Copyright 2012 Jakob Flierl
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package liqui.droid.activity;

import liqui.droid.holder.BreadCrumbHolder;
import liqui.droid.R;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * The Class ExploreActivity.
 */
public class Test extends Base implements LoaderCallbacks<Cursor>, OnItemClickListener {

    private CalendarCursorAdapter mAdapter;
    
    protected Uri mCalendarsUri;
    
    protected Uri mEventsUri;
    
    protected Uri mAttendeesUri;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.act_generic_list);
        setUpActionBar();

        createBreadcrumb(getResources().getString(R.string.title_explore), (BreadCrumbHolder[]) null);
        
        String calendarLocation;
        if (Build.VERSION.SDK_INT >= 8) {
            calendarLocation = "content://com.android.calendar/"; 
        } else {
            calendarLocation = "content://calendar/";
        }
        
        mCalendarsUri = Uri.parse(calendarLocation + "calendars");
        mEventsUri    = Uri.parse(calendarLocation + "events");
        mAttendeesUri = Uri.parse(calendarLocation + "attendees");
        getSupportLoaderManager().initLoader(0, null, this);
        mAdapter = new CalendarCursorAdapter(this, null, true);
        
        ListView listView = (ListView) findViewById(R.id.list_view);
        listView.setOnItemClickListener(this);
        listView.setAdapter(mAdapter);
        registerForContextMenu(listView);
        
    }
    
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
       // Uri ISSUES_URI = DBProvider.ISSUE_PURE_CONTENT_URI.buildUpon().appendQueryParameter("db", getAPIDB()).build();
       
       /*
       Cursor c = getContentResolver().query(ISSUES_URI, null, null, null, null);
       
       c.moveToFirst();
       while (!c.isAfterLast()) {
           String issueId      = c.getString(c.getColumnIndex(DB.Issue.COLUMN_ID));
           Long   issueCreated = c.getLong(c.getColumnIndex(DB.Issue.COLUMN_CREATED));
           String issueArea    = c.getString(c.getColumnIndex(DB.Issue.COLUMN_AREA_ID));

           PrettyTime pt = new PrettyTime();
           
           Log.d("XXXXXX", "id " + issueId + " areaId " + issueArea +
                   " created " + pt.format(new DateTime(issueCreated).toDate()));
           
           
           // http://stackoverflow.com/questions/5564530
           ContentValues event = new ContentValues();
           
           event.put("calendar_id", id);
           event.put("title", "Created issue " + issueId + " area " + issueArea);
           event.put("dtstart", issueCreated);
           event.put("dtend", issueCreated + 60 * 5 * 1000);
           event.put("allDay", 0);   // 0 for false, 1 for true
           event.put("eventStatus", 1);
           event.put("visibility", 3); // public
           event.put("hasAlarm", 1); // 0 for false, 1 for true
           event.put("eventTimezone", TimeZone.getDefault().getID());
           
           Uri url = getContentResolver().insert(EVENTS_URI, event);
           
           c.moveToNext();
       }
       
       c.close();
       
       */

       /*
       Intent calendarIntent = new Intent() ;
       calendarIntent.setClassName("com.android.calendar","com.android.calendar.AgendaActivity");
       startActivity(calendarIntent);
       */
       
       Intent calDetailsIntent = new Intent();
       calDetailsIntent.setClassName("liqui.droid", "liqui.droid.CalendarEventsActivity");
       
       Bundle extras = new Bundle();
       extras.putString("_id", String.valueOf(id));
       calDetailsIntent.putExtras(extras);

       startActivity(calDetailsIntent);
    }
    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.test_menu, menu);
    }
    
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        // AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        // int i = info.position;
        
        switch (item.getItemId()) {

            case R.id.open_calendar:
                try {
                    Intent intent = new Intent(Intent.ACTION_EDIT);
                    intent.setType("vnd.android.cursor.item/event");
                    intent.putExtra("beginTime", System.currentTimeMillis());
                    intent.putExtra("endTime", System.currentTimeMillis() + 1000 * 60 * 5);
                    intent.putExtra("title", "LQFB Test");
                    intent.putExtra("description", "");
                    intent.putExtra("eventLocation", "");
                    intent.putExtra("eventStatus", "");
                    
                    startActivity(intent);
                    return true;
                } catch (Exception e) {
                    
                }
            default:
                return super.onContextItemSelected(item);
        }
    }


    @Override
    public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
        CursorLoader cursorLoader = new CursorLoader(getApplication(), mCalendarsUri,
                new String[] { "_id", "displayName", "selected" }, "selected = 1", null, null);
        
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cl, Cursor cursor) {
        mAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursor) {
        mAdapter.swapCursor(null);
    }

    /**
     * The Calendar cursor adapter.
     */
    public class CalendarCursorAdapter extends CursorAdapter {

        public CalendarCursorAdapter(Context context, Cursor c, boolean autoRequery) {
            super(context, c, autoRequery);
        }

        public CalendarCursorAdapter(Context context, Cursor c, int flags) {
            super(context, c, flags);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            TextView t1 = (TextView)view.findViewById(R.id.tv_title);
            TextView t2 = (TextView)view.findViewById(R.id.tv_desc);
            
            String id = cursor.getString(cursor.getColumnIndex("_id")); 
            String selected = cursor.getString(cursor.getColumnIndex("selected")); 
            
            t1.setText(id + " selected: " + selected);
            t2.setText(cursor.getString(cursor.getColumnIndex("displayName")));
        }        

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View v = inflater.inflate(R.layout.row_simple_2, viewGroup, false);
            bindView(v, context, cursor);
            return v;
        }

    }
}
