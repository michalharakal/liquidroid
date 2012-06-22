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
import liqui.droid.db.DB;
import liqui.droid.db.DBProvider;
import liqui.droid.holder.BreadCrumbHolder;
import liqui.droid.R;

import android.content.AsyncQueryHandler;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.CursorTreeAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.SimpleCursorTreeAdapter;

/**
 * The Class Area tree cached Activity.
 */
public class AreaTreeCached extends Base implements OnChildClickListener {

    protected LQFBContentObserver mContentObserver;

    protected CursorTreeAdapter mAdapter;
    
    protected QueryHandler mQueryHandler;
    
    protected Uri mContentGroup;
    
    protected Uri mContentChild;
    
    protected static final int TOKEN_GROUP = 0;
    
    protected static final int TOKEN_CHILD = 1;
    
    private static final class QueryHandler extends AsyncQueryHandler {
        private CursorTreeAdapter mAdapter;

        public QueryHandler(Context context, CursorTreeAdapter adapter) {
            super(context.getContentResolver());
            this.mAdapter = adapter;
        }

        @Override
        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
            switch (token) {
            case TOKEN_GROUP:
                mAdapter.setGroupCursor(cursor);
                break;

            case TOKEN_CHILD:
                int groupPosition = (Integer) cookie;
                try {
                    mAdapter.setChildrenCursor(groupPosition, cursor);
                } catch (NullPointerException e) {
                    Log.w("DEBUG", "Adapter expired, try again on the next query: " + e.getMessage());
                }
                break;
            }
        }
    }
    
    public class AreaTreeAdapter extends SimpleCursorTreeAdapter {

        public AreaTreeAdapter(Context context) {

            super(context, null, R.layout.row_area_2_group, null, null, R.layout.row_area_1_child, null, null);
        }

        @Override
        protected void bindChildView(View view, Context context, Cursor cursor, boolean isLastChild) {
            String name = cursor.getString(cursor.getColumnIndex("name"));
            String id   = cursor.getString(cursor.getColumnIndex("_id"));
            // Integer member_weight       = cursor.getInt(cursor.getColumnIndex("member_weight"));
            Integer direct_member_count = cursor.getInt(cursor.getColumnIndex("direct_member_count"));
            
            Integer issues_new_count   = cursor.getInt(cursor.getColumnIndex("issues_new_count"));
            Integer issues_discussion_count   = cursor.getInt(cursor.getColumnIndex("issues_discussion_count"));
            Integer issues_frozen_count   = cursor.getInt(cursor.getColumnIndex("issues_frozen_count"));
            Integer issues_voting_count   = cursor.getInt(cursor.getColumnIndex("issues_voting_count"));
            Integer issues_finished_count   = cursor.getInt(cursor.getColumnIndex("issues_finished_count"));
            Integer issues_cancelled_count   = cursor.getInt(cursor.getColumnIndex("issues_cancelled_count"));

            TextView tvName              = (TextView)view.findViewById(R.id.tv_title);
            TextView tvDirectMemberCount = (TextView)view.findViewById(R.id.tv_count);
            
            TextView tvIssuesNewCount        = (TextView)view.findViewById(R.id.tv_issues_new_count);
            TextView tvIssuesDiscussionCount = (TextView)view.findViewById(R.id.tv_issues_discussion_count);
            TextView tvIssuesFrozenCount     = (TextView)view.findViewById(R.id.tv_issues_frozen_count);
            TextView tvIssuesVotingCount     = (TextView)view.findViewById(R.id.tv_issues_voting_count);
            TextView tvIssuesFinishedCount   = (TextView)view.findViewById(R.id.tv_issues_finished_count);
            TextView tvIssuesCancelledCount  = (TextView)view.findViewById(R.id.tv_issues_cancelled_count);
            
            tvName.setText(name);
            tvDirectMemberCount.setText(String.valueOf(direct_member_count));
            
            tvIssuesNewCount.setText(String.valueOf(issues_new_count));
            tvIssuesDiscussionCount.setText(String.valueOf(issues_discussion_count));
            tvIssuesFrozenCount.setText(String.valueOf(issues_frozen_count));
            tvIssuesVotingCount.setText(String.valueOf(issues_voting_count));
            tvIssuesFinishedCount.setText(String.valueOf(issues_finished_count));
            tvIssuesCancelledCount.setText(String.valueOf(issues_cancelled_count));
            
            ImageView iv = (ImageView) view.findViewById(R.id.iv_membership);   

            if (id != null && getMemberId() != null) {
                if (!isResultEmpty(dbUri(DBProvider.MEMBERSHIP_CONTENT_URI),
                        "area_id = ? AND member_id = ?",
                        new String[] { id, getMemberId() }, "area_id LIMIT 1")) {
                    iv.setVisibility(View.VISIBLE);
                } else {
                    iv.setVisibility(View.INVISIBLE);
                }
            } else {
                iv.setVisibility(View.INVISIBLE);
            }
            
        }

        @Override
        protected void bindGroupView(View view, Context context, Cursor cursor, boolean isExpanded) {
            String unit_id   = cursor.getString(cursor.getColumnIndex("unit_id"));
            String unit_name = cursor.getString(cursor.getColumnIndex("unit_name"));
            // String unit_member_count = cursor.getString(cursor.getColumnIndex("unit_member_count"));
            
            TextView tvUnitName = (TextView) view.findViewById(android.R.id.text1);
            // TextView tvUnitMemberCount = (TextView) view.findViewById(android.R.id.text2);
            
            tvUnitName.setText(unit_name);
            // tvUnitMemberCount.setText(unit_member_count);
            
            TextView tvDelegation = (TextView) view.findViewById(R.id.tv_delegation);
            ImageView ivdelegation = (ImageView) view.findViewById(R.id.iv_arrow_right);
            
            if (isAuthenticated()) {
                Uri.Builder ub = dbUri(DBProvider.DELEGATION_CONTENT_URI).buildUpon();
                ub.appendQueryParameter("unit_id", unit_id);
                ub.appendQueryParameter("member_id", getMemberId());    
                
                String memberName = queryString(ub.build(), DB.Member.COLUMN_NAME, null, null, null);
            
                if (memberName != null && memberName.length() > 0) {
                    tvDelegation.setText(memberName);
                    tvDelegation.setVisibility(View.VISIBLE);
                    ivdelegation.setVisibility(View.VISIBLE);

                } else {
                    tvDelegation.setVisibility(View.GONE);
                    ivdelegation.setVisibility(View.GONE);
                }
            } else {
                tvDelegation.setVisibility(View.GONE);
                ivdelegation.setVisibility(View.GONE);
            }
        }

        @Override
        protected Cursor getChildrenCursor(Cursor groupCursor) {
            Uri.Builder builder = mContentGroup.buildUpon().appendPath(""+groupCursor.getLong(0));
            mQueryHandler.startQuery(TOKEN_CHILD, groupCursor.getPosition(), builder.build(), null, null, null, null);
            return null;
        }
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.act_area_tree);
        setUpActionBar();
        setBreadCrumbs();
        
        mContentChild = dbUri("content://liqui.droid.db/units/active");
        mContentGroup = dbUri("content://liqui.droid.db/areas/active/by_unit");

        ExpandableListView listView = (ExpandableListView) findViewById(R.id.list_view);
        listView.setOnChildClickListener(this);
        
        mAdapter = new AreaTreeAdapter(this);
        listView.setAdapter(mAdapter);
        
        mContentObserver = new LQFBContentObserver(new Handler());

        getContentResolver().registerContentObserver(dbUri(DBProvider.UPDATED_CONTENT_URI), true, mContentObserver);
        getContentResolver().registerContentObserver(dbUri(DBProvider.AREA_CONTENT_URI), true, mContentObserver);
        getContentResolver().registerContentObserver(dbUri(DBProvider.MEMBERSHIP_CONTENT_URI), true, mContentObserver);
        getContentResolver().registerContentObserver(dbUri(DBProvider.DELEGATION_CONTENT_URI), true, mContentObserver);

        mQueryHandler = new QueryHandler(this, mAdapter);

        mQueryHandler.startQuery(TOKEN_GROUP, null, mContentChild, null, null, null, null);
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
            
        createBreadcrumb(getString(R.string.units_and_areas), breadCrumbHolders);
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition,
            int childPosition, long id) {
        ExpandableListAdapter ela = parent.getExpandableListAdapter();
        
        Cursor child = (Cursor) ela.getChild(groupPosition, childPosition);
        
        Intent intent = new Intent().setClass(AreaTreeCached.this, IssueTreeCached.class);
        
        Bundle data = new Bundle();
        data.putString("UNIT_NAME", child.getString(child.getColumnIndex("unit_name")));
        data.putString("AREA_ID",   child.getString(child.getColumnIndex("_id")));
        data.putString("tab", "area");

        Bundle extras = new Bundle();
        extras.putString(Constants.Account.API_NAME,    getAPIName());
        extras.putString(Constants.Account.API_URL,     getAPIUrl());
        extras.putString(Constants.Account.MEMBER_ID,   getMemberId());
        extras.putString(Constants.Account.SESSION_KEY, getSessionKey());
        intent.putExtras(extras);
        
        intent.putExtra(Constants.DATA_BUNDLE, data);
        startActivity(intent);

        return false;
    }
    
    /* (non-Javadoc)
     * @see android.app.Activity#onPrepareOptionsMenu(android.view.Menu)
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
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
            mAdapter.notifyDataSetChanged();;
        }
    }
}
