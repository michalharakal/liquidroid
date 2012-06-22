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
import liqui.droid.holder.BreadCrumbHolder;
import liqui.droid.util.LoadingDialog;
import liqui.droid.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.viewpagerindicator.TitlePageIndicator;

/**
 * The Initiative activity.
 */
public class Initiative extends Base {

    protected Uri mContentUri;
    
    protected String mInitiativeId;

    protected LoadingDialog mLoadingDialog;

    protected Bundle mBundle;

    protected InitiativePagerAdapter mAdapter;

    protected InitiativeOnPageChangeListener mInitiativeOnPageChangeListener;

    /**
     * Called when the activity is first created.
     * 
     * @param savedInstanceState the saved instance state
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBundle = getIntent().getExtras().getBundle(Constants.DATA_BUNDLE);
        mInitiativeId = String.valueOf(mBundle.getInt("_id"));
        
        setContentView(R.layout.act_ini);
        setUpActionBar();
        setBreadCrumbs();

        mContentUri = dbUri("content://liqui.droid.db/issues_pure");
        
        ViewPager vp = (ViewPager) findViewById(R.id.viewpager);
        mAdapter = new InitiativePagerAdapter(this, mContentUri);
        vp.setAdapter(mAdapter);
        
        mInitiativeOnPageChangeListener = new InitiativeOnPageChangeListener();
        vp.setOnPageChangeListener(mInitiativeOnPageChangeListener);
        
        TitlePageIndicator titleIndicator = (TitlePageIndicator)findViewById(R.id.titles);
        titleIndicator.setViewPager(vp);
        titleIndicator.setOnPageChangeListener(mInitiativeOnPageChangeListener);

        if (mInitiativeId != null && mInitiativeId.length() > 0) {
            int item = mAdapter.indexOf(Integer.parseInt(mInitiativeId));
            vp.setCurrentItem(item);
        }
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

        Cursor c = getContentResolver().query(dbUri("content://liqui.droid.db/initiatives"), null, "_id = ?", new String[] { mInitiativeId }, null);
        c.moveToFirst();
        String issueId = c.getString(c.getColumnIndex(DB.Initiative.COLUMN_ISSUE_ID));
        c.close();

        Uri contentIssues      = dbUri("content://liqui.droid.db/issues");

        Cursor c2 = getContentResolver().query(contentIssues, new String[] { "policy.name AS policy_name" }, "issue.policy_id = policy._id AND issue._id = ?", new String[] { issueId }, null);
        c2.moveToFirst();
        String policyName = c2.getString(c2.getColumnIndex("policy_name"));
        c2.close();

        createBreadcrumb("#" + issueId + " - " + policyName, breadCrumbHolders);
    }

    /*
     * (non-Javadoc)
     * @see android.app.Activity#onSaveInstanceState(android.os.Bundle)
     */
    public void onSaveInstanceState(Bundle outState) {
        Log.v(Constants.LOG_TAG, this.getLocalClassName() + " onSaveInstanceState");
        outState.putAll(mBundle);
        super.onSaveInstanceState(outState);
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 100) {
        }
    }
    
    class InitiativePagerAdapter extends PagerAdapter {

        private View view;
        private Integer count = null;
        
        private final Context context;
        
        public InitiativePagerAdapter(Context context, Uri uri) {
            this.context = context;
        }

        @Override
        public int getCount() {
            if (count != null) {
                return count;
            }

            Cursor c = context.getContentResolver().query(dbUri("content://liqui.droid.db/initiatives"), null, "_id = ?", new String[] { mInitiativeId }, null);
            c.moveToFirst();
            String issueId = c.getString(c.getColumnIndex(DB.Initiative.COLUMN_ISSUE_ID));
            c.close();

            Cursor c2 = context.getContentResolver().query(dbUri("content://liqui.droid.db/initiatives"), null, "issue_id = ?", new String[] { issueId }, "rank, supporter_count DESC, _id");
            int nr = c2.getCount();
            c2.close();
            count = nr;
            
            return nr;
        }
        
        public int indexOf(int iniId) {
            Cursor c = context.getContentResolver().query(dbUri("content://liqui.droid.db/initiatives"), null, "_id = ?", new String[] { mInitiativeId }, null);
            c.moveToFirst();
            String issueId = c.getString(c.getColumnIndex(DB.Initiative.COLUMN_ISSUE_ID));
            c.close();

            Cursor c2 = context.getContentResolver().query(dbUri("content://liqui.droid.db/initiatives"), null, "issue_id = ?", new String[] { issueId }, "rank, supporter_count DESC, _id");
            
            for (int i = 0; i < c2.getCount(); i++) {
                c2.moveToPosition(i);
                Integer id = c2.getInt(c2.getColumnIndex(DB.Initiative.COLUMN_ID));
                
                if (iniId == id) {
                    c2.close();
                    return c2.getPosition();
                }
            }
            
            c2.close();
            
            return -1;
        }
        
        public int getId(int position) {
            Cursor c = context.getContentResolver().query(dbUri("content://liqui.droid.db/initiatives"), null, "_id = ?", new String[] { mInitiativeId }, null);
            c.moveToFirst();
            String issueId = c.getString(c.getColumnIndex(DB.Initiative.COLUMN_ISSUE_ID));
            c.close();

            Cursor c2 = context.getContentResolver().query(dbUri("content://liqui.droid.db/initiatives"), null, "issue_id = ?", new String[] { issueId }, "rank, supporter_count DESC, _id");
            
            c2.moveToPosition(position);
            Integer id = c2.getInt(c2.getColumnIndex(DB.Initiative.COLUMN_ID));
            c2.close();
            
            return id;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            
            if (position < 0) {
                return null;
            }
            
            LayoutInflater layoutInflater = ((Activity) context).getLayoutInflater();
            
            view = (View) layoutInflater.inflate(R.layout.inc_initiative, null);
            
            WebView wv = (WebView) view.findViewById(R.id.wv_content);
            
            Uri.Builder builder2 = dbUri("content://liqui.droid.db/drafts").buildUpon();
            
            Cursor cursor2 = getContentResolver().query(builder2.build(), null,
                    "draft.initiative_id = ?",
                    new String[] { String.valueOf(getId(position)) }, "_id DESC LIMIT 1");
            
            cursor2.moveToFirst();
            
            if (!cursor2.isAfterLast()) {
                
                String formattingEngine = cursor2.getString(cursor2.getColumnIndex(DB.Draft.COLUMN_FORMATTING_ENGINE));
                
                if ("rocketwiki".equals(formattingEngine.toLowerCase())) {
            
                    WebSettings webSettings = wv.getSettings();
                    webSettings.setJavaScriptEnabled(true);
            
                    String content = "<html><head>" +
                            "<link href='file:///android_asset/rocketwiki.css' rel='stylesheet' type='text/css'/>" +
                            "<script type=\"text/javascript\" src=\"file:///android_asset/jquery-1.4.2.min.js\"></script>" +
                            "<script type=\"text/javascript\" src=\"file:///android_asset/rocketwiki.js\"></script>" +
                            "</head><body><textarea class=\"hidden\" id=\"textarea\">" +
                            cursor2.getString(cursor2.getColumnIndex(DB.Draft.COLUMN_CONTENT)) +
                            "</textarea>" +
                            "<span style=\"display:block;margin:0px 0px 0px 0px;\" id=\"preview\"/>" +
                            "<script>$('#preview').html(rocketwiki.process($('#textarea').val()));</script>" +
                            "</body></html>";
            
                    wv.loadDataWithBaseURL("file:///android_asset/", content, "text/html", "utf-8", null);
                } else {
                    String content = "<html><head>" +
                            cursor2.getString(cursor2.getColumnIndex(DB.Draft.COLUMN_CONTENT)) +
                            "</body></html>";
                    
                    wv.loadDataWithBaseURL(null, content, "text/html", "utf-8", null);
                }
            }
            
            cursor2.close();

            ((ViewPager) container).addView(view, 0);

            return view;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((View) object);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Cursor c = context.getContentResolver().query(dbUri("content://liqui.droid.db/initiatives"), null, "_id = ?", new String[] { mInitiativeId }, null);
            c.moveToFirst();
            String issueId = c.getString(c.getColumnIndex(DB.Initiative.COLUMN_ISSUE_ID));
            c.close();

            Cursor c2 = context.getContentResolver().query(dbUri("content://liqui.droid.db/initiatives"), null, "issue_id = ?", new String[] { issueId }, "rank, supporter_count DESC, _id");
            
            c2.moveToPosition(position);
            String id   = c2.getString(c2.getColumnIndex(DB.Initiative.COLUMN_ID));
            String name = c2.getString(c2.getColumnIndex(DB.Initiative.COLUMN_NAME));
            c2.close();
            
            return "i" + id + ": " + name;
        }
        
    }

    /**
     * Get the current view position from the ViewPager.
     */
    public class InitiativeOnPageChangeListener extends ViewPager.SimpleOnPageChangeListener {

        private int currentPage;

        @Override
        public void onPageSelected(int position) {
            mInitiativeId = String.valueOf(mAdapter.getId(position));
        }

        public int getCurrentPage() {
            return currentPage;
        }
    }
}
