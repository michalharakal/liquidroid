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
import lfapi.v2.services.LiquidFeedbackService.MemberService;
import lfapi.v2.services.LiquidFeedbackServiceFactory;
import lfapi.v2.services.auth.SessionKeyAuthentication;
import liqui.droid.Constants;
import liqui.droid.db.DB;
import liqui.droid.db.DB2Schema;
import liqui.droid.db.DBProvider;
import liqui.droid.db.DBSystemProvider;
import liqui.droid.holder.BreadCrumbHolder;
import liqui.droid.util.LoadingDialog;
import liqui.droid.util.MemberImage;
import liqui.droid.util.StringUtils;
import liqui.droid.R;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import java.lang.ref.WeakReference;

/**
 * The Member details activity.
 */
public class MemberDetails extends Base implements OnClickListener, OnItemClickListener {

    protected Uri CONTENT_URI;
    
    protected Uri mUri;

    protected String mMemberDetailId;

    protected String mMemberName;

    protected LoadingDialog mLoadingDialog;

    protected Member mMember;
    
    protected String mSortOrder;
    
    protected String mSortDir;
    
    protected String mFilter;
    
    protected MemberDetailsPagerAdapter mAdapter;
    
    protected DetailOnPageChangeListener mDetailOnPageChangeListener;

    /**
     * Called when the activity is first created.
     * 
     * @param bundle the saved instance state
     */
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        setContentView(R.layout.act_member_details);
        setUpActionBar();
        setBreadCrumbs();
        
        CONTENT_URI = dbUri("content://liqui.droid.db/members");

        Bundle extras = getIntent().getExtras();
        
        // Check from the saved Instance
        mUri = (bundle == null) ? null : (Uri) bundle
                .getParcelable(DBProvider.MEMBER_CONTENT_ITEM_TYPE);

        // Or passed from the other activity
        if (extras != null) {
            mUri = extras
                    .getParcelable(DBProvider.MEMBER_CONTENT_ITEM_TYPE);

            mSortOrder = extras.getString("sortOrder");
            mSortDir   = extras.getString("sortDir");
            mFilter    = extras.getString("filter");
        }
        
        mMemberDetailId = mUri.getLastPathSegment();
        
        ViewPager vp = (ViewPager) findViewById(R.id.viewpager);
        mAdapter = new MemberDetailsPagerAdapter(this, dbUri("content://liqui.droid.db/members"));
        vp.setAdapter(mAdapter);
        
        mDetailOnPageChangeListener = new DetailOnPageChangeListener();
        vp.setOnPageChangeListener(mDetailOnPageChangeListener);
        
        if (mMemberDetailId != null && mMemberDetailId.length() > 0) {
            int item = mAdapter.indexOf(Integer.parseInt(mMemberDetailId));
            vp.setCurrentItem(item);
        }
        
        Log.d("XXXXXX", "mMemberDetailId " + mMemberDetailId);
        Log.d("XXXXXX", "getMemberId() " + getMemberId());
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

    protected void onSavedInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(DBSystemProvider.LQFBS_CONTENT_ITEM_TYPE, mUri);
    }
    
    /*
     * (non-Javadoc)
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    public void onClick(View view) {
        int id = view.getId();

        switch (id) {

            default:
                break;
        }
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
        return super.onContextItemSelected(item);
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onPrepareOptionsMenu(android.view.Menu)
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        
        if (isAuthenticated()) {
            menu.clear();
            MenuInflater inflater = getMenuInflater();
            if (!mMemberDetailId.equals(getMemberId())) {
                if (isResultEmpty(dbUri(DBProvider.CONTACT_CONTENT_URI),
                        DB.Member.Contact.COLUMN_MEMBER_ID + " = ? AND " + DB.Member.Contact.COLUMN_OTHER_MEMBER_ID + " = ?",
                        new String[] { getMemberId(), mMemberDetailId }, null)) {
                    inflater.inflate(R.menu.follow, menu);
                } else {
                    inflater.inflate(R.menu.unfollow, menu);
                }
            }
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
            case R.id.menu_follow:
                new FollowUnfollowTask(this).execute(true);
                return true;
            case R.id.menu_unfollow:
                new FollowUnfollowTask(this).execute(false);
                return true;
            default:
                return true;
        }
    }

    private class FollowUnfollowTask extends AsyncTask<Boolean, Void, Boolean> {

        /** The target. */
        private WeakReference<MemberDetails> mTarget;
        
        /** The exception. */
        private Exception mException = null;
        
        private boolean mIsFollowAction;

        /**
         * Instantiates a new load watched repos task.
         *
         * @param activity the activity
         */
        public FollowUnfollowTask(MemberDetails activity) {
            mTarget = new WeakReference<MemberDetails>(activity);
        }

        /*
         * (non-Javadoc)
         * @see android.os.AsyncTask#doInBackground(Params[])
         */
        @Override
        protected Boolean doInBackground(Boolean... arg0) {
            if (mTarget.get() != null) {
                mIsFollowAction = arg0[0];
                try {
                    LiquidFeedbackServiceFactory f = LiquidFeedbackServiceFactory.newInstance(getAPIUrl());
                    MemberService ms = f.createMemberService();
                    
                    lfapi.v2.services.auth.Authentication auth = new SessionKeyAuthentication(getSessionKey());
                    ms.setAuthentication(auth);
                    
                    Member.Contact mc = new Member.Contact();
                    mc.memberId = getMemberId();
                    mc.otherMemberId = mMemberDetailId;
                    // mc.pub = 
                    
                    if (mIsFollowAction) {
                        ms.postContact(mc, false);
                    } else {
                        ms.postContact(mc, true);
                    }
                    return true;
                } catch (Exception e) {
                    Log.e(Constants.LOG_TAG, e.getMessage(), e);
                    mException = e;
                    return null;
                }
            }
            else {
                return null;
            }
        }

        @Override
        protected void onPreExecute() {
            if (mTarget.get() != null) {
                mTarget.get().mLoadingDialog = LoadingDialog.show(mTarget.get(), true, true, false);
            }
        }
        
        /*
         * (non-Javadoc)
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        protected void onPostExecute(Boolean result) {
            if (mTarget.get() != null) {
                mTarget.get().mLoadingDialog.dismiss();
                if (mException != null) {
                    if (mIsFollowAction) {
                        mTarget.get().showMessage(getResources().getString(
                                R.string.member_error_follow, mMemberDetailId, mException.getLocalizedMessage()), false);
                    } else {
                        mTarget.get().showMessage(getResources().getString(
                                R.string.member_error_unfollow, mMemberDetailId, mException.getLocalizedMessage()), false);
                    }
                } else {
                    Uri CONTACT_URI = dbUri(DBProvider.CONTACT_CONTENT_URI);
                    if (mIsFollowAction) {
                        ContentValues values = new ContentValues();
                        values.put(DB.Member.Contact.COLUMN_MEMBER_ID, getMemberId());
                        values.put(DB.Member.Contact.COLUMN_OTHER_MEMBER_ID, mMemberDetailId);
                        values.put(DB.Member.Contact.COLUMN_PUBLIC, true);
                        getContentResolver().insert(CONTACT_URI, values);
                        
                        mTarget.get().showMessage(getResources().getString(
                                R.string.member_success_follow, mMemberDetailId), false);
                        
                    } else {
                        Uri.Builder ub = CONTACT_URI.buildUpon();
                        ub = ub.appendQueryParameter("member_id", getMemberId());
                        ub = ub.appendQueryParameter("other_member_id", mMemberDetailId);
                        getContentResolver().delete(ub.build(), null, null);
                        
                        mTarget.get().showMessage(getResources().getString(
                                R.string.member_success_unfollow, mMemberDetailId), false);
                    }
                }
            }
        }
    }
    
    /* (non-Javadoc)
     * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 100) {
        }
    }
    
    class MemberDetailsPagerAdapter extends PagerAdapter {

        private Uri contentUri;
        private View view;
        private Integer count = null;
        
        private final Context context;
        
        public MemberDetailsPagerAdapter(Context context, Uri uri) {
            // Log.d("XXXXXX", "Uri " + uri);
            
            this.context = context;
            this.contentUri = uri;
        }

        @Override
        public int getCount() {
            if (count != null) {
                return count;
            }

            Cursor c;
            if (mFilter != null) {
                c = context.getContentResolver().query(contentUri, null, "name NOTNULL AND name LIKE ?",
                    new String[] { "%" + mFilter + "%" }, mSortOrder + " " + mSortDir);
            } else {
                c = context.getContentResolver().query(contentUri, null, "name NOTNULL",
                        null, mSortOrder + " " + mSortDir);
            }
            
            int nr = c.getCount();
            
            c.close();
            
            // Log.d("XXXXXX", "getCount() " + nr);
            
            count = nr;
            
            return nr;
        }
        
        public int indexOf(int memberId) {
            Cursor c;
            
            if (mFilter != null) {
                c = context.getContentResolver().query(contentUri, null, "name NOTNULL AND name LIKE ?",
                    new String[] { "%" + mFilter + "%" }, mSortOrder + " " + mSortDir);
            } else {
                c = context.getContentResolver().query(contentUri, null, "name NOTNULL",
                        null, mSortOrder + " " + mSortDir);
            }
            
            for (int i = 0; i < c.getCount(); i++) {
                
                c.moveToPosition(i);
                Integer id = c.getInt(c.getColumnIndex(DB.Member.COLUMN_ID));
                
                if (memberId == id) {
                    c.close();
                    return c.getPosition();
                }
            }
            
            c.close();
            
            return -1;
        }
        
        public int getId(int position) {
            Cursor c;
            
            if (mFilter != null) {
                c = context.getContentResolver().query(contentUri, null, "name NOTNULL AND name LIKE ?",
                    new String[] { "%" + mFilter + "%" }, mSortOrder + " " + mSortDir);
            } else {
                c = context.getContentResolver().query(contentUri, null, "name NOTNULL",
                        null, mSortOrder + " " + mSortDir);
            }
            
            c.moveToPosition(position);
            Integer id = c.getInt(c.getColumnIndex(DB.Member.COLUMN_ID));
            c.close();
            
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
            
            view = (View) layoutInflater.inflate(R.layout.inc_user_details, null);
            Cursor c;
            
            if (mFilter != null) {
                c = context.getContentResolver().query(contentUri, null, "name NOTNULL AND name LIKE ?",
                    new String[] { "%" + mFilter + "%" }, mSortOrder + " " + mSortDir);
            } else {
                c = context.getContentResolver().query(contentUri, null, "name NOTNULL",
                        null, mSortOrder + " " + mSortDir);
            }
            // c.moveToFirst();
            c.moveToPosition(position);
           // c.move(position);
            Member user = DB2Schema.fillMember(c);
            c.close();
            
            ImageView ivGravatar = (ImageView) view.findViewById(R.id.iv_gravatar);
            
            new MemberImage(context, getAPIDB()).download("http://dummy?" + user.id + ";avatar", ivGravatar, 80, 80);
//            GravatarImage.getInstance().download(user.eMail, ivGravatar, 80);

            TextView tvName = (TextView) view.findViewById(R.id.tv_name);
            // TextView tvCreated = (TextView) findViewById(R.id.tv_created_at);

            tvName.setText(user.name);

            TextView tvEmail = (TextView) view.findViewById(R.id.tv_email);
            if (!StringUtils.isBlank(user.eMail)) {
                tvEmail.setText(user.eMail);
            }

            TextView tvWebsite = (TextView) view.findViewById(R.id.tv_website);
            if (!StringUtils.isBlank(user.website)) {
                tvWebsite.setText(user.website);
            }

            TextView tvIdentification = (TextView) view.findViewById(R.id.tv_identification);
            if (!StringUtils.isBlank(user.identification)) {
                tvIdentification.setText(user.identification);
            }

            /*
            TextView tvLocation = (TextView) view.findViewById(R.id.tv_location);
            if (!StringUtils.isBlank(user.internalPosts)) {
                tvLocation.setText(user.internalPosts);
            }*/

            TextView tvOrganization = (TextView) view.findViewById(R.id.tv_organization);
            if (!StringUtils.isBlank(user.organizationalUnit)) {
                tvOrganization.setText(user.organizationalUnit);
            }

            TextView tvRealname = (TextView) view.findViewById(R.id.tv_realname);
            if (!StringUtils.isBlank(user.realName)) {
                tvRealname.setText(user.realName);
            }

            TextView tvBirthday = (TextView) view.findViewById(R.id.tv_birthday);
            if (user.birthday != null) {
                tvBirthday.setText(user.birthday.toString());
            }

            TextView tvAddress = (TextView) view.findViewById(R.id.tv_address);
            if (!StringUtils.isBlank(user.address)) {
                tvAddress.setText(user.address);
            }
            
            ((ViewPager) container).addView(view, 0);

            return view;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((View) object);
        }
        
    }
    
    /**
     * Get the current view position from the ViewPager.
     */
    public class DetailOnPageChangeListener extends ViewPager.SimpleOnPageChangeListener {

        private int currentPage;

        @Override
        public void onPageSelected(int position) {
            mMemberDetailId = String.valueOf(mAdapter.getId(position));
        }

        public int getCurrentPage() {
            return currentPage;
        }
    }
}
