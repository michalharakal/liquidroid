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


import lfapi.v2.schema.Member;
import liqui.droid.Constants;
import liqui.droid.db.DB;
import liqui.droid.db.DB2Schema;
import liqui.droid.db.DBProvider;
import liqui.droid.holder.BreadCrumbHolder;
import liqui.droid.util.IndexableListView;
import liqui.droid.util.MemberImage;
import liqui.droid.util.StringMatcher;
import liqui.droid.R;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * The Class UserListCachedActivity.
 */
public class MemberListCached extends Base
    implements LoaderCallbacks<Cursor>, OnItemClickListener, FilterQueryProvider {

    protected MemberCursorAdapter mAdapter;
    
    protected EditText mFilterText = null;
    
    protected Uri CONTENT_URI;
    
    protected String mSortOrder;
    
    protected String mSortDir;
    
    protected String mFfilter;
    
    protected IndexableListView mListView;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.act_member_list);
        setUpActionBar();
        setBreadCrumbs();
        
        CONTENT_URI = dbUri("content://liqui.droid.db/members");
        
        mFilterText = (EditText) findViewById(R.id.search_box);
        mFilterText.addTextChangedListener(filterTextWatcher);
        
        mSortOrder = getSettingStringValue("memberSortOrder", "name");
        mSortDir   = getSettingStringValue("memberSortDir", "ASC");
        
        mAdapter = new MemberCursorAdapter(this, null, true);
        mAdapter.setFilterQueryProvider(this);
        
        mListView = (IndexableListView) findViewById(R.id.list_view);
        mListView.setOnItemClickListener(this);
        mListView.setAdapter(mAdapter);
        
        if (mSortOrder.equals("name") && mSortDir.equals("ASC")) {
            mListView.setFastScrollEnabled(true);
        } else {
            mListView.setFastScrollEnabled(false);
        }

        mListView.setTextFilterEnabled(true);

        getSupportLoaderManager().initLoader(0, null, this);
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
            
        createBreadcrumb(getString(R.string.members), breadCrumbHolders);
    }
    
    /* (non-Javadoc)
     * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        
        menu.clear();
        
        if (mSortOrder.equals("name")) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.sort_id_menu, menu);
        } else if (mSortOrder.equals("_id")) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.sort_name_menu, menu);
        }
        
        if (mSortDir.equals("ASC")) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.sort_asc_menu, menu);
        } else if (mSortDir.equals("DESC")) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.sort_desc_menu, menu);
        }
        
        return true;        
    }
    
    /* (non-Javadoc)
     * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sort_id:
            case R.id.sort_name:
                if (mSortOrder.equals("name")) {
                    mSortOrder = "_id";
                } else {
                    mSortOrder = "name";
                }
                
                setSettingStringValue("memberSortOrder", mSortOrder);
                
                ActivityCompat.invalidateOptionsMenu(this);
                
                getSupportLoaderManager().destroyLoader(0);
                getSupportLoaderManager().initLoader(0, null, this);
                break;
            case R.id.sort_order_asc:
            case R.id.sort_order_desc:
                if (mSortDir.equals("ASC")) {
                    mSortDir = "DESC";
                } else {
                    mSortDir = "ASC";
                }
            
                setSettingStringValue("memberSortDir", mSortDir);

                ActivityCompat.invalidateOptionsMenu(this);
                
                getSupportLoaderManager().destroyLoader(0);
                getSupportLoaderManager().initLoader(0, null, this);
                break;
        }
        
        if (mSortOrder.equals("name") && mSortDir.equals("ASC")) {
            mListView.setFastScrollEnabled(true);
        } else {
            mListView.setFastScrollEnabled(false);
        }
        
        return super.onOptionsItemSelected(item);
    }

    private TextWatcher filterTextWatcher = new TextWatcher() {

        public void afterTextChanged(Editable s) {
        }

        public void beforeTextChanged(CharSequence s, int start, int count,
                int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before,
                int count) {
            mFfilter = s.toString();
            mAdapter.getFilter().filter(s);
        }

    };

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Cursor c = (Cursor) adapterView.getAdapter().getItem(position);
        Intent intent = new Intent().setClass(MemberListCached.this, MemberDetails.class);
        
        Uri uri = dbUri(DBProvider.MEMBER_CONTENT_URI.toString()).buildUpon().appendPath(String.valueOf(c.getInt(c.getColumnIndex(DB.Member.COLUMN_ID)))).build();
        
        intent.putExtra("sortOrder", mSortOrder);
        intent.putExtra("sortDir", mSortDir);
        intent.putExtra("filter", mFfilter);
        intent.putExtra(DBProvider.MEMBER_CONTENT_ITEM_TYPE, uri);
        
        Bundle extras = new Bundle();
        extras.putString(Constants.Account.API_NAME,    getAPIName());
        extras.putString(Constants.Account.API_URL,     getAPIUrl());
        extras.putString(Constants.Account.MEMBER_ID,   getMemberId());
        extras.putString(Constants.Account.SESSION_KEY, getSessionKey());
        intent.putExtras(extras);

        startActivity(intent);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
        CursorLoader cursorLoader = new CursorLoader(getApplication(), CONTENT_URI,
                null, "name NOTNULL", null, mSortOrder + " " + mSortDir);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mFilterText.removeTextChangedListener(filterTextWatcher);
    }

    @SuppressWarnings("deprecation")
    @Override
    public Cursor runQuery(CharSequence constraint) {
        return managedQuery(CONTENT_URI, null, "name NOTNULL AND name LIKE ?",
                new String[] { "%" + constraint.toString() + "%" }, mSortOrder + " " + mSortDir);
    }
    
    /**
     * The Member cursor adapter.
     */
    public class MemberCursorAdapter extends CursorAdapter implements SectionIndexer {
        
        MemberImage mMemberImage;

        public MemberCursorAdapter(Context context, Cursor c, boolean autoRequery) {
            super(context, c, autoRequery);
            mMemberImage = new MemberImage(context, getAPIDB());
        }

        public MemberCursorAdapter(Context context, Cursor c, int flags) {
            super(context, c, flags);
            mMemberImage = new MemberImage(context, getAPIDB());
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            Member m = DB2Schema.fillMember(cursor);
            
            TextView summary = (TextView)view.findViewById(R.id.tv_desc);
            summary.setText(m.name);

            TextView desc = (TextView)view.findViewById(R.id.tv_extra);
            desc.setText(m.eMail);
            
            ImageView ivGravatar = (ImageView) view.findViewById(R.id.iv_gravatar);

            mMemberImage.download("http://dummy?" + m.id + ";avatar", ivGravatar, 80, 80);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View v = inflater.inflate(R.layout.row_gravatar_2, viewGroup, false);
            bindView(v, context, cursor);
            return v;
        }

        private String mSections = "#ABCDEFGHIJKLMNOPQRSTUVWXYZ";

        @Override
        public int getPositionForSection(int section) {
            // If there is no item for current section, previous section will be selected
            for (int i = section; i >= 0; i--) {
                for (int j = 0; j < getCount(); j++) {
                    if (i == 0) {
                        // For numeric section
                        for (int k = 0; k <= 9; k++) {
                            Cursor c = (Cursor)getItem(j);
                            String name = c.getString(c.getColumnIndex("name"));
                            if (name == null) name = " "; name = name.toUpperCase();
                            
                            if (StringMatcher.match(String.valueOf((name).charAt(0)), String.valueOf(k)))
                                return j;
                        }
                    } else {
                        Cursor c = (Cursor)getItem(j);
                        String name = c.getString(c.getColumnIndex("name"));
                        if (name == null) name = " "; name = name.toUpperCase();
                        if (StringMatcher.match(String.valueOf((name).charAt(0)), String.valueOf(mSections.charAt(i))))
                            return j;
                    }
                }
            }
            return 0;
        }

        @Override
        public int getSectionForPosition(int position) {
            return 0;
        }

        @Override
        public Object[] getSections() {
            String[] sections = new String[mSections.length()];
            for (int i = 0; i < mSections.length(); i++)
                sections[i] = String.valueOf(mSections.charAt(i));
            return sections;
        }
    }
}
