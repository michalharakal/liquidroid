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
import liqui.droid.db.DB2Schema;
import liqui.droid.db.DBProvider;
import liqui.droid.util.LoadingDialog;
import liqui.droid.util.MemberImage;
import liqui.droid.util.StringUtils;
import liqui.droid.R;

import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import org.ocpsoft.pretty.time.PrettyTime;

/**
 * The Member activity.
 */
public class MemberActivity extends Base implements OnClickListener, OnItemClickListener {

    protected Uri mContentUri;

    protected LoadingDialog mLoadingDialog;

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

        setContentView(R.layout.act_member);
        setUpActionBar();
        
        mContentUri = dbUri(DBProvider.MEMBER_CONTENT_URI);
        
        mAdapter = new ExploreArrayAdapter(this);
        
        mContentObserver = new LQFBContentObserver(new Handler());

        fillData();
    }
    
    /**
     * Fill data into UI components.
     * 
     * @param user the user
     */
    protected void fillData(Member user) {
        if (user == null)
            return;

        ImageView ivGravatar = (ImageView) findViewById(R.id.iv_gravatar);
        ivGravatar.setOnClickListener(this);

        new MemberImage(this, getAPIDB()).download("http://dummy?" + user.id + ";avatar", ivGravatar, 80, 80);

        // GravatarDownloader.getInstance().download(user.eMail, ivGravatar, 80);

        TextView tvName = (TextView) findViewById(R.id.tv_name);

        String userName = user.name;
        userName += (user.realName != null ? " - " + user.realName : "");
        tvName.setText(userName);

        TextView tvCreated = (TextView) findViewById(R.id.tv_created_at);

        PrettyTime pt = new PrettyTime();
        
        if (user.lastActivity != null) {
            tvCreated.setText("Last active " + pt.format(user.lastActivity.toDate()));
            tvCreated.setVisibility(View.VISIBLE);
        } else {
            tvCreated.setVisibility(View.GONE);
        }
        
        /*
        tvCreated.setText(user.lastLoginPublic != null ? getResources().getString(
                R.string.user_last_login, user.lastLoginPublic != null ? StringUtils.formatDate(user.lastLoginPublic.toDate()) : "---")
                : "");
         */
        
        // show email row if not blank
        TextView tvEmail = (TextView) findViewById(R.id.tv_email);
        if (!StringUtils.isBlank(user.eMail)) {
            tvEmail.setText(user.eMail);
            tvEmail.setVisibility(View.VISIBLE);
        } else {
            tvEmail.setVisibility(View.GONE);
        }

        // show website if not blank
        TextView tvWebsite = (TextView) findViewById(R.id.tv_website);
        if (!StringUtils.isBlank(user.website)) {
            tvWebsite.setText(user.website);
            tvWebsite.setVisibility(View.VISIBLE);
        } else {
            tvWebsite.setVisibility(View.GONE);
        }

        // show company if not blank
        TextView tvCompany = (TextView) findViewById(R.id.tv_company);
        if (!StringUtils.isBlank(user.externalMemberships)) {
            tvCompany.setText(user.externalMemberships);
            tvCompany.setVisibility(View.VISIBLE);
        } else {
            tvCompany.setVisibility(View.GONE);
        }

        // Show location if not blank
        TextView tvLocation = (TextView) findViewById(R.id.tv_location);
        if (!StringUtils.isBlank(user.address)) {
            tvLocation.setText(user.address);
            tvLocation.setVisibility(View.VISIBLE);
        } else {
            tvLocation.setVisibility(View.GONE);
        }
    }

    /*
     * (non-Javadoc)
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    public void onClick(View view) {
        int id = view.getId();

        switch (id) {
            case R.id.iv_gravatar:
                editUser(view);
                break;

            default:
                break;
        }
    }

    /**
     * Gets the feeds when Activity button clicked.
     * 
     * @param view the view
     */
    public void editUser(View view) {
        Intent intent = new Intent().setClass(this, MemberEdit.class);
        intent.putExtra(Constants.Member.LOGIN, mMemberId);
        intent.putExtra(Constants.ACTIONBAR_TITLE, mMemberId);
        intent.putExtra(Constants.SUBTITLE, getResources().getString(R.string.user_news_feed));
        
        Bundle extras = new Bundle();
        extras.putString(Constants.Account.API_NAME,    getAPIName());
        extras.putString(Constants.Account.API_URL,     getAPIUrl());
        extras.putString(Constants.Account.MEMBER_ID,   getMemberId());
        extras.putString(Constants.Account.SESSION_KEY, getSessionKey());
        intent.putExtras(extras);

        startActivity(intent);
    }

    /*
     * (non-Javadoc)
     * @see
     * android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget
     * .AdapterView, android.view.View, int, long)
     */
    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onCreateContextMenu(android.view.ContextMenu, android.view.View, android.view.ContextMenu.ContextMenuInfo)
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onContextItemSelected(android.view.MenuItem)
     */
    public boolean onContextItemSelected(MenuItem item) {
        return true;
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onPrepareOptionsMenu(android.view.Menu)
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.refresh, menu);
        if (isAuthenticated()) {
            // menu.clear();
            // if (getMemberId() != null) {
            //    inflater.inflate(R.menu.follow, menu);
            //}
            // inflater.inflate(R.menu.about_menu, menu);
            inflater.inflate(R.menu.authenticated, menu);
        }
        return true;
    }

    /* (non-Javadoc)
     * @see liqui.droid.BaseActivity#setMenuOptionItemSelected(android.view.MenuItem)
     */
    @Override
    public boolean setMenuOptionItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_profile:
                Intent intent = new Intent().setClass(this, MemberEdit.class);

                Bundle extras = new Bundle();
                extras.putString(Constants.Account.API_NAME,    getAPIName());
                extras.putString(Constants.Account.API_URL,     getAPIUrl());
                extras.putString(Constants.Account.MEMBER_ID,   getMemberId());
                extras.putString(Constants.Account.SESSION_KEY, getSessionKey());
                intent.putExtras(extras);
                
                startActivity(intent);
                return true;
            default:
                return true;
        }
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
    }
    
    /**
     * Fill data.
     */
    private void fillData() {
        ListView listView = (ListView) findViewById(R.id.lv_actions);
        
        listView.setAdapter(mAdapter);
        
        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                if (position == 5) {
                    Intent intent = new Intent().setClass(MemberActivity.this,
                            ContactListCached.class);

                    Bundle extras = new Bundle();
                    extras.putString(Constants.Account.API_NAME,    getAPIName());
                    extras.putString(Constants.Account.API_URL,     getAPIUrl());
                    extras.putString(Constants.Account.MEMBER_ID,   getMemberId());
                    extras.putString(Constants.Account.SESSION_KEY, getSessionKey());
                    intent.putExtras(extras);

                    startActivity(intent);
                } else if (position == 4) {
                        Intent intent = new Intent().setClass(MemberActivity.this,
                                MemberListCached.class);

                        Bundle extras = new Bundle();
                        extras.putString(Constants.Account.API_NAME,    getAPIName());
                        extras.putString(Constants.Account.API_URL,     getAPIUrl());
                        extras.putString(Constants.Account.MEMBER_ID,   getMemberId());
                        extras.putString(Constants.Account.SESSION_KEY, getSessionKey());
                        intent.putExtras(extras);

                        startActivity(intent);
                } else if (position == 3) {
                    Intent intent = new Intent().setClass(MemberActivity.this,
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
                    Intent intent = new Intent().setClass(MemberActivity.this,
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
                     Intent intent = new Intent().setClass(MemberActivity.this,
                             EventListCached.class);
                     
                     Bundle extras = new Bundle();
                     extras.putString(Constants.Account.API_NAME,    getAPIName());
                     extras.putString(Constants.Account.API_URL,     getAPIUrl());
                     extras.putString(Constants.Account.MEMBER_ID,   getMemberId());
                     extras.putString(Constants.Account.SESSION_KEY, getSessionKey());
                     intent.putExtras(extras);

                     startActivity(intent);
                } else if (position == 0) {
                    Intent intent = new Intent().setClass(MemberActivity.this,
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

        Cursor c = getContentResolver().query(mContentUri, null , "_id = ?",
                new String[] { getMemberId() }, null);
        
        c.moveToFirst();
        
        if (!c.isAfterLast()) {
            fillData(DB2Schema.fillMember(c));
        }
        
        c.close();
        
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
            
            for (String s : getResources().getStringArray(R.array.array_explore_auth)) {
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
                    Uri AREA_URI = dbUri(DBProvider.AREA_CONTENT_URI);
                    c = context.getContentResolver().query(AREA_URI, null, null, null, null);
                    c.moveToFirst(); count += c.getCount();
                    c.close();
                break;
                
                case 2:
                    Uri EVENT_URI = dbUri(DBProvider.EVENT_CONTENT_URI);
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
                    Uri MEMBERS_URI = dbUri(DBProvider.MEMBER_CONTENT_URI);
                    c = context.getContentResolver().query(MEMBERS_URI, null, null, null, null);
                    c.moveToFirst(); count += c.getCount();
                    c.close();
                break;

                case 6:
                    Uri CONTACTS_URI = dbUri(DBProvider.CONTACT_CONTENT_URI);
                    c = context.getContentResolver().query(CONTACTS_URI, null, "member_id = ?",
                            new String[] { getMemberId() }, null);
                    c.moveToFirst(); count += c.getCount();
                    c.close();
                break;

                case 7:
                break;
                
                case 8:
                break;
                
            }
            
            tvCount.setText(count);
        }
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
