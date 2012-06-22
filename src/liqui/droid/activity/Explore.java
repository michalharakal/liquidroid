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

import liqui.droid.Constants;
import liqui.droid.db.DBProvider;
import liqui.droid.holder.BreadCrumbHolder;
import liqui.droid.R;

import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * The Explore activity.
 */
public class Explore extends Base {

    protected ExploreArrayAdapter mAdapter;
    
    protected LQFBContentObserver mContentObserver;

    /**
     * Called when the activity is first created.
     * 
     * @param savedInstanceState the saved instance state
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.act_generic_list);
        setUpActionBar();

        createBreadcrumb(getResources().getString(R.string.title_explore), (BreadCrumbHolder[]) null);
        
        mAdapter = new ExploreArrayAdapter(this);
                
        mContentObserver = new LQFBContentObserver(new Handler());

        fillData();
    }

    /**
     * Fill data.
     */
    private void fillData() {
        ListView listView = (ListView) findViewById(R.id.list_view);
        
        listView.setAdapter(mAdapter);
        
        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                if (position == 4) {
                    Intent intent = new Intent().setClass(Explore.this,
                            MemberListCached.class);

                    Bundle extras = new Bundle();
                    extras.putString(Constants.Account.API_NAME,    getAPIName());
                    extras.putString(Constants.Account.API_URL,     getAPIUrl());
                    extras.putString(Constants.Account.MEMBER_ID,   getMemberId());
                    extras.putString(Constants.Account.SESSION_KEY, getSessionKey());
                    intent.putExtras(extras);

                    startActivity(intent);
                } else if (position == 3) {
                    Intent intent = new Intent().setClass(Explore.this,
                            IssueTreeCached.class);
                    
                    Bundle extras = new Bundle();
                    extras.putString(Constants.Account.API_NAME,    getAPIName());
                    extras.putString(Constants.Account.API_URL,     getAPIUrl());
                    extras.putString(Constants.Account.MEMBER_ID,   getMemberId());
                    extras.putString(Constants.Account.SESSION_KEY, getSessionKey());
                    intent.putExtras(extras);

                    Bundle bundle = new Bundle();
                    bundle.putString("tab", "closed");
                    
                    intent.putExtra(Constants.DATA_BUNDLE, bundle);
                    startActivity(intent);
                } else if (position == 2) {
                    Intent intent = new Intent().setClass(Explore.this,
                            IssueTreeCached.class);
                    
                    Bundle extras = new Bundle();
                    extras.putString(Constants.Account.API_NAME,    getAPIName());
                    extras.putString(Constants.Account.API_URL,     getAPIUrl());
                    extras.putString(Constants.Account.MEMBER_ID,   getMemberId());
                    extras.putString(Constants.Account.SESSION_KEY, getSessionKey());
                    intent.putExtras(extras);

                    Bundle bundle = new Bundle();
                    bundle.putString("tab", "open");
                    
                    intent.putExtra(Constants.DATA_BUNDLE, bundle);
                    startActivity(intent);
                } else if (position == 1) {
                     Intent intent = new Intent().setClass(Explore.this,
                             EventListCached.class);
                     
                     Bundle extras = new Bundle();
                     extras.putString(Constants.Account.API_NAME,    getAPIName());
                     extras.putString(Constants.Account.API_URL,     getAPIUrl());
                     extras.putString(Constants.Account.MEMBER_ID,   getMemberId());
                     extras.putString(Constants.Account.SESSION_KEY, getSessionKey());
                     intent.putExtras(extras);

                     startActivity(intent);
                } else if (position == 0) {
                    Intent intent = new Intent().setClass(Explore.this,
                            AreaTreeCached.class);
                    
                    Bundle extras = new Bundle();
                    extras.putString(Constants.Account.API_NAME,    getAPIName());
                    extras.putString(Constants.Account.API_URL,     getAPIUrl());
                    extras.putString(Constants.Account.MEMBER_ID,   getMemberId());
                    extras.putString(Constants.Account.SESSION_KEY, getSessionKey());
                    intent.putExtras(extras);

                    startActivity(intent);
                }
            }
        });

        Uri uriUpdates = dbUri(DBProvider.UPDATED_CONTENT_URI);
        getContentResolver().unregisterContentObserver(mContentObserver);
        getContentResolver().registerContentObserver(uriUpdates, true, mContentObserver);
    }
    
    /**
     * The Explore array adapter.
     */
    public class ExploreArrayAdapter extends ArrayAdapter<String> {
        
        public ExploreArrayAdapter(Context context) {
            super(context, 0);
            
            for (String s : getResources().getStringArray(R.array.array_explore)) {
                add(s);
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            View v = inflater.inflate(R.layout.row_explore, parent, false);
            bindView(v, getContext(), position);
            return v;
        }

        public void bindView(View view, Context context, int position) {
            TextView tvSummary = (TextView)view.findViewById(R.id.tv_title);
            TextView tvCount = (TextView)view.findViewById(R.id.tv_count);
            
            String name = getItem(position);
            Integer id  = position + 1;
            
            tvSummary.setText(name);
            
            String count = ""; Cursor c;
            
            switch (id.intValue()) {
                case 1:
                    Uri AREA_URI = dbUri("content://liqui.droid.db/areas");
                    c = context.getContentResolver().query(AREA_URI, null, null, null, null);
                    c.moveToFirst(); count += c.getCount();
                    c.close();
                break;
                
                case 2:
                    Uri EVENT_URI = dbUri("content://liqui.droid.db/events");
                    c = context.getContentResolver().query(EVENT_URI, null, null, null, null);
                    c.moveToFirst(); count += c.getCount();
                    c.close();
                break;
            
                case 3:
                    Uri ISSUES_OPEN_URI = dbUri("content://liqui.droid.db/issues/latest/open");
                    c = context.getContentResolver().query(ISSUES_OPEN_URI, null, null, null, null);
                    c.moveToFirst(); count += c.getCount();
                    c.close();
                break;
                
                case 4:
                    Uri ISSUES_CLOSED_URI = dbUri("content://liqui.droid.db/issues/latest/closed");
                    c = context.getContentResolver().query(ISSUES_CLOSED_URI, null, null, null, null);
                    c.moveToFirst(); count += c.getCount();
                    c.close();
                break;

                case 5:
                    Uri MEMBERS_URI = dbUri("content://liqui.droid.db/members");
                    c = context.getContentResolver().query(MEMBERS_URI, null, null, null, null);
                    c.moveToFirst(); count += c.getCount();
                    c.close();
                break;
            }
            
            tvCount.setText(count);
        }
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onPrepareOptionsMenu(android.view.Menu)
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.refresh, menu);
        return true;
    }

    class LQFBContentObserver extends ContentObserver {

        public LQFBContentObserver(Handler handler) {
            super(handler);
        }

        @Override
        public boolean deliverSelfNotifications() {
            return true;
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            fillData();
        }
        
    }
}
