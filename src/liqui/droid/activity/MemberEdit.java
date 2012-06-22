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

import java.lang.ref.WeakReference;

import lfapi.v2.schema.Member;
import lfapi.v2.services.LiquidFeedbackService.MemberService;
import lfapi.v2.services.auth.SessionKeyAuthentication;
import lfapi.v2.services.LiquidFeedbackServiceFactory;
import liqui.droid.Constants;
import liqui.droid.db.DB2Schema;
import liqui.droid.db.DBProvider;
import liqui.droid.util.LoadingDialog;
import liqui.droid.util.MemberImage;
import liqui.droid.util.StringUtils;
import liqui.droid.R;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import org.joda.time.DateTime;

/**
 * The Member edit activity.
 */
public class MemberEdit extends Base implements OnClickListener, OnItemClickListener {

    protected Uri mContentUri;

    protected LoadingDialog mLoadingDialog;

    /**
     * Called when the activity is first created.
     * 
     * @param savedInstanceState the saved instance state
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.act_member_edit);
        setUpActionBar();
        
        mContentUri = dbUri(DBProvider.MEMBER_CONTENT_URI);
        
        Cursor c = getContentResolver().query(mContentUri, null , "_id = ?",
                new String[] { getMemberId() }, null);
        
        c.moveToFirst();
        
        if (!c.isAfterLast()) {
            fillData(DB2Schema.fillMember(c));
        }
        
        c.close();
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
        
        new MemberImage(this, getAPIDB()).download("http://dummy?" + user.id + ";avatar", ivGravatar, 80, 80);
//        GravatarImage.getInstance().download(user.eMail, ivGravatar, 80);

        TextView tvName = (TextView) findViewById(R.id.tv_name);
        // TextView tvCreated = (TextView) findViewById(R.id.tv_created_at);

        tvName.setText(user.name);

        TextView tvEmail = (TextView) findViewById(R.id.tv_email);
        if (!StringUtils.isBlank(user.eMail)) {
            tvEmail.setText(user.eMail);
        }

        TextView tvWebsite = (TextView) findViewById(R.id.tv_website);
        if (!StringUtils.isBlank(user.website)) {
            tvWebsite.setText(user.website);
        }

        TextView tvIdentification = (TextView) findViewById(R.id.tv_identification);
        if (!StringUtils.isBlank(user.identification)) {
            tvIdentification.setText(user.identification);
        }

        TextView tvLocation = (TextView) findViewById(R.id.tv_location);
        if (!StringUtils.isBlank(user.internalPosts)) {
            tvLocation.setText(user.internalPosts);
        }

        TextView tvOrganization = (TextView) findViewById(R.id.tv_organization);
        if (!StringUtils.isBlank(user.organizationalUnit)) {
            tvOrganization.setText(user.organizationalUnit);
        }

        TextView tvRealname = (TextView) findViewById(R.id.tv_realname);
        if (!StringUtils.isBlank(user.realName)) {
            tvRealname.setText(user.realName);
        }

        TextView tvBirthday = (TextView) findViewById(R.id.tv_birthday);
        if (user.birthday != null) {
            tvBirthday.setText(user.birthday.toString());
        }

        TextView tvAddress = (TextView) findViewById(R.id.tv_address);
        if (!StringUtils.isBlank(user.address)) {
            tvAddress.setText(user.address);
        }

        Button btnSave = (Button) findViewById(R.id.btn_user_save);
        btnSave.setOnClickListener(this);
    }

    /**
     * Save profile.
     *
     * @param view the view
     */
    public void saveProfile(View view) {
        TextView tvName = (TextView) findViewById(R.id.tv_name);
        TextView tvEmail = (TextView) findViewById(R.id.tv_email);
        TextView tvWebsite = (TextView) findViewById(R.id.tv_website);

        TextView tvOrganization = (TextView) findViewById(R.id.tv_organization);
        TextView tvRealname = (TextView) findViewById(R.id.tv_realname);
        TextView tvBirthday = (TextView) findViewById(R.id.tv_birthday);
        TextView tvAddress = (TextView) findViewById(R.id.tv_address);
        
        Member member = new Member();

        member.name = "" + tvName.getText();
        member.eMail = "" + tvEmail.getText();
        member.website = "" + tvWebsite.getText();
        member.organizationalUnit = "" + tvOrganization.getText();
        member.realName = "" + tvRealname.getText();
        try {
            member.birthday = !StringUtils.isBlank(tvBirthday.getText().toString()) ? DateTime.parse(tvBirthday.getText().toString()) : null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        member.address = "" + tvAddress.getText();

        new SaveUserInfoTask(this).execute(member);
    }

    /**
     * An asynchronous task that runs on a background thread to load user info.
     */
    private class SaveUserInfoTask extends AsyncTask<Member, Integer, Member> {

        /** The target. */
        private WeakReference<MemberEdit> mTarget;

        /** The exception. */
        private boolean mException;

        /** The is auth error. */
        private boolean isAuthError;

        /**
         * Instantiates a new load user info task.
         * 
         * @param activity the activity
         */
        public SaveUserInfoTask(MemberEdit activity) {
            mTarget = new WeakReference<MemberEdit>(activity);
        }

        /*
         * (non-Javadoc)
         * @see android.os.AsyncTask#doInBackground(Params[])
         */
        @Override
        protected Member doInBackground(Member... arg0) {
            if (mTarget.get() != null) {
                try {

                    Log.e(Constants.LOG_TAG, "Saving Profile..");

                    LiquidFeedbackServiceFactory f = LiquidFeedbackServiceFactory
                            .newInstance(getAPIUrl());
                    MemberService ms = f.createMemberService();

                    ms.setAuthentication(new SessionKeyAuthentication(getSessionKey()));

                    Member.Options mo = new Member.Options();
                    mo.memberId = mTarget.get().mMemberId;

                    ms.postMember(arg0[0]);
                    return null;

                    // return
                    // userService.getUserByUsername(mTarget.get().mUserLogin);
                } catch (Exception e) {
                    Log.e(Constants.LOG_TAG, e.getMessage(), e);
                    if (e.getCause() != null
                            && e.getCause().getMessage()
                                    .equalsIgnoreCase("Received authentication challenge is null")) {
                        isAuthError = true;
                    }
                    mException = true;
                    return null;
                }
            } else {
                return null;
            }
        }

        /*
         * (non-Javadoc)
         * @see android.os.AsyncTask#onPreExecute()
         */
        @Override
        protected void onPreExecute() {
            if (mTarget.get() != null) {
                mTarget.get().mLoadingDialog = LoadingDialog.show(mTarget.get(), true, true);
            }
        }

        /*
         * (non-Javadoc)
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        protected void onPostExecute(Member result) {
            if (mTarget.get() != null) {
                mTarget.get().mLoadingDialog.dismiss();
                if (mException && isAuthError) {
                    mTarget.get().showError(false);
                } else if (mException) {
                    mTarget.get().showError(false);
                } else {
                    mTarget.get().finish();
                    // mTarget.get().fillData(result);
                }
            }
        }
    }

    /*
     * (non-Javadoc)
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    public void onClick(View view) {
        int id = view.getId();

        switch (id) {
            case R.id.btn_user_save:
                saveProfile(view);
                break;

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
        return true;
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onPrepareOptionsMenu(android.view.Menu)
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (isAuthenticated()) {
            menu.clear();
            MenuInflater inflater = getMenuInflater();
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
            default:
                return true;
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
}
