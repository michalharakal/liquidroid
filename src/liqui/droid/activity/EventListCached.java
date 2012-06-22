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

import lfapi.v2.schema.Event;
import liqui.droid.Constants;
import liqui.droid.db.DB2Schema;
import liqui.droid.holder.BreadCrumbHolder;
import liqui.droid.R;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import org.ocpsoft.pretty.time.PrettyTime;

/**
 * The Class Event list cached Activity.
 */
public class EventListCached extends Base implements LoaderCallbacks<Cursor>, OnItemClickListener {

    protected EventCursorAdapter mAdapter;
    
    protected Uri mContentUri;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.act_generic_list);
        setUpActionBar();
        setBreadCrumbs();
        
        mContentUri = dbUri("content://liqui.droid.db/events");

        getSupportLoaderManager().initLoader(0, null, this);
        mAdapter = new EventCursorAdapter(this, null, true);
        
        ListView listView = (ListView) findViewById(R.id.list_view);
        listView.setOnItemClickListener(this);
        listView.setAdapter(mAdapter);
    }
    
    /**
     * Sets the bread crumbs.
     */
    protected void setBreadCrumbs() {
        BreadCrumbHolder[] breadCrumbHolders = new BreadCrumbHolder[1];

        BreadCrumbHolder b = new BreadCrumbHolder();
        b.setLabel(getResources().getString(R.string.title_explore));
        b.setTag(Constants.EXPLORE);
        breadCrumbHolders[0] = b;
            
        createBreadcrumb(getString(R.string.events), breadCrumbHolders);
    }

    
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        // Cursor c = (Cursor) adapterView.getAdapter().getItem(position);
        
        /*
        Intent intent = new Intent().setClass(EventListCachedActivity.this, EventActivity.class);
        
        Bundle data = new Bundle();
        data.putInt(Constants.Event.ID, c.getInt(c.getColumnIndex(DB.Event.COLUMN_ID)));
        
        intent.putExtra(Constants.DATA_BUNDLE, data);
        startActivity(intent);
        */
    }

    @Override
    public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
        CursorLoader cursorLoader = new CursorLoader(getApplication(), mContentUri, null, null, null, "_id DESC");
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
     * The Event Cursor adapter.
     */
    public class EventCursorAdapter extends CursorAdapter {

        public EventCursorAdapter(Context context, Cursor c, boolean autoRequery) {
            super(context, c, autoRequery);
        }

        public EventCursorAdapter(Context context, Cursor c, int flags) {
            super(context, c, flags);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            Event e = DB2Schema.fillEvent(cursor);
            
            TextView tvId    = (TextView)view.findViewById(R.id.tv_event_id);
            TextView tvEvent = (TextView)view.findViewById(R.id.tv_event);
            TextView tvDesc = (TextView)view.findViewById(R.id.tv_desc);
            ImageView ivEvent = (ImageView)view.findViewById(R.id.iv_event);
            
            tvId.setText("" + e.id);
            // summary.setText(e.name);

            // desc.setText(e.);
            
            String event = ""; String desc = "";
            switch (e.event) {
                case issue_state_changed:
                    event += "Issue state changed";
                    desc += "IssueId " + e.issueId;
                    ivEvent.setImageResource(R.drawable.ic_user_edit);
                    break;
                case initiative_created_in_new_issue:
                    event += "Initiative created in new issue";
                    desc += "IssueId " + e.issueId;
                    ivEvent.setImageResource(R.drawable.ic_user_add);
                    break;
                case initiative_created_in_existing_issue:
                    event += "Initiative created in existing issue";
                    desc += " IssueId " + e.issueId;
                    ivEvent.setImageResource(R.drawable.ic_user_add);
                    break;
                case initiative_revoked:
                    event += "Initiative revoked";
                    desc += "InitiativeId " + e.initiativeId;
                    ivEvent.setImageResource(R.drawable.ic_user_delete);
                    break;
                case new_draft_created:
                    event += "New draft created";
                    desc += "DraftId " + e.draftId;
                    ivEvent.setImageResource(R.drawable.ic_user_add);
                    break;
                case suggestion_created:
                    event += "Suggestion created";
                    desc += "SuggestionId " + e.suggestionId;
                    ivEvent.setImageResource(R.drawable.ic_user_comment);
                    break;
                case finished_without_winner:
                    event += "Finished without winner";
                    desc += "IssueId " + e.issueId;
                    ivEvent.setImageResource(R.drawable.ic_award_star_silver_2);
                    break;
                case finished_with_winner:
                    event += "Finished with winner";
                    desc += "IssueId " + e.issueId;
                    ivEvent.setImageResource(R.drawable.ic_award_star_gold_2);
                    break;
                default:
                    event += "Unknown event id " + e.id;
                    desc += "IssueId " + e.issueId + " InitiativeId " + e.initiativeId +
                            " DraftId " + e.draftId + " SuggestionId " + e.suggestionId;
                    ivEvent.setImageResource(R.drawable.ic_application_form);
                    break;
            }
            
            PrettyTime pt = new PrettyTime();
            event += "\n" + pt.format(e.occurrence.toDate());
            
            tvEvent.setText(event);
            tvDesc.setText(desc);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View v = inflater.inflate(R.layout.row_event, viewGroup, false);
            bindView(v, context, cursor);
            return v;
        }

    }

}
