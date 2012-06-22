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

import lfapi.v2.schema.SysInfo;
import lfapi.v2.services.LiquidFeedbackException;
import lfapi.v2.services.LiquidFeedbackService.SessionService;
import lfapi.v2.services.LiquidFeedbackService.SysInfoService;
import lfapi.v2.services.auth.SessionKeyAuthentication;
import lfapi.v2.services.LiquidFeedbackServiceFactory;
import liqui.droid.Constants;
import liqui.droid.db.DBProvider;
import liqui.droid.db.DBSystem;
import liqui.droid.db.DBSystemProvider;
import liqui.droid.service.AccountAuthenticatorService;
import liqui.droid.service.SyncService;
import liqui.droid.util.ActionBar;
import liqui.droid.util.ActionBar.IntentAction;
import liqui.droid.R;

import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import android.accounts.Account;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

/**
 * The LQFB activity.
 */
public class LiquiDroid extends Base
    implements OnItemSelectedListener, LoaderManager.LoaderCallbacks<Cursor> {

    public static final Uri CONTENT_URI = DBSystemProvider.LQFBS_CONTENT_URI;
    
    protected SimpleCursorAdapter mAdapter;
    
    protected Spinner mSpinnerLQFBs;
    
    protected EditText mEditTextApiKey;

    protected Button mButtonLogin;

    protected ProgressDialog mProgressDialog;
    
    /**
     * The Class PasswordTextWatcher.
     */
    class PasswordTextWatcher implements TextWatcher {

        private boolean ok = false;

        /* (non-Javadoc)
         * @see android.text.TextWatcher#afterTextChanged(android.text.Editable)
         */
        @Override
        public void afterTextChanged(Editable s) {
        }

        /* (non-Javadoc)
         * @see android.text.TextWatcher#beforeTextChanged(java.lang.CharSequence, int, int, int)
         */
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        /* (non-Javadoc)
         * @see android.text.TextWatcher#onTextChanged(java.lang.CharSequence, int, int, int)
         */
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            if (s.length() > 0) {
                ok = true;
            } else {
                ok = false;
            }

            if (s.length() > 0 && mSpinnerLQFBs.getSelectedItem() != null) {
                mButtonLogin.setEnabled(true);
            } else {
                mButtonLogin.setEnabled(false);
            }
        }

        /**
         * Checks if is ok.
         *
         * @return true, if is ok
         */
        public boolean isOk() {
            return ok;
        }

    }

    PasswordTextWatcher mPasswortTextWatcher = new PasswordTextWatcher();

    /*
     * (non-Javadoc)
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @SuppressWarnings("deprecation")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        if (getIntent().getAction() != null && getIntent().getAction().equals("liqui.droid.sync.LOGIN")) {
            // we create a new account
        } else {
            Cursor c = getContentResolver().query(CONTENT_URI, null, "last_active = 1", null, null);
        
            c.moveToFirst();
        
            if (!c.isAfterLast()) {
                mApiName    = c.getString(c.getColumnIndex(DBSystem.TableLQFBs.COLUMN_NAME));
                mApiUrl     = c.getString(c.getColumnIndex(DBSystem.TableLQFBs.COLUMN_URL));
                mMemberId   = c.getString(c.getColumnIndex(DBSystem.TableLQFBs.COLUMN_MEMBER_ID));
                mSessionKey = c.getString(c.getColumnIndex(DBSystem.TableLQFBs.COLUMN_SESSION_KEY));
            }
        
            c.close();
        }

        if (isAuthenticated()) {
            Intent intent = new Intent().setClass(LiquiDroid.this, MemberActivity.class);
            
            Bundle extras = new Bundle();
            extras.putString(Constants.Account.API_NAME,    getAPIName());
            extras.putString(Constants.Account.API_URL,     getAPIUrl());
            extras.putString(Constants.Account.MEMBER_ID,   getMemberId());
            extras.putString(Constants.Account.SESSION_KEY, getSessionKey());
            intent.putExtras(extras);

            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            
            finish();
            return;
        }
        
        setContentView(R.layout.act_main);

        ActionBar actionBar = (ActionBar) findViewById(R.id.actionbar);

        actionBar.addAction(new IntentAction(this, new Intent(getApplicationContext(),
                Search.class), R.drawable.ic_search));
        
        mSpinnerLQFBs = (Spinner) findViewById(R.id.sp_lqfb_instance);
        
        mAdapter = new SimpleCursorAdapter(this,
                android.R.layout.simple_spinner_item,
                null, new String[] { DBSystem.TableLQFBs.COLUMN_NAME, DBSystem.TableLQFBs.COLUMN_ID },
                new int[]{ android.R.id.text1});
        
        mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mSpinnerLQFBs.setAdapter(mAdapter);
        mSpinnerLQFBs.setOnItemSelectedListener(this);
        
        mEditTextApiKey = (EditText) findViewById(R.id.et_api_token);
        mEditTextApiKey.addTextChangedListener(mPasswortTextWatcher);

        mButtonLogin = (Button) findViewById(R.id.btn_login);
        mButtonLogin.setEnabled(false);
        mButtonLogin.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Cursor c = (Cursor)mSpinnerLQFBs.getSelectedItem();
                
                if (c == null) return;
                
                mApiUrl     = c.getString(c.getColumnIndex(DBSystem.TableLQFBs.COLUMN_URL));

                String name = c.getString(c.getColumnIndex(DBSystem.TableLQFBs.COLUMN_NAME));
                String key  = mEditTextApiKey.getText().toString().trim();

                Uri LQFBUri = DBSystemProvider.LQFBS_CONTENT_URI;
                ContentValues values = new ContentValues();
                values.put(DBSystem.TableLQFBs.COLUMN_NAME, name);
                values.put(DBSystem.TableLQFBs.COLUMN_API_KEY, key);
                getContentResolver().update(LQFBUri, values, DBSystem.TableLQFBs.COLUMN_NAME + " = ?", new String[] { name });

                hideKeyboard(mButtonLogin.getWindowToken());
                new LoginTask(LiquiDroid.this).execute(getAPIName(), getAPIUrl());
            }
        });

        TextView tvExplore = (TextView) findViewById(R.id.tv_explore);
        tvExplore.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent().setClass(LiquiDroid.this, Explore.class);
                
                Bundle extras = new Bundle();
                extras.putString(Constants.Account.API_NAME,    mApiName);
                extras.putString(Constants.Account.API_URL,     mApiUrl);
                extras.putString(Constants.Account.MEMBER_ID,   mMemberId);
                extras.putString(Constants.Account.SESSION_KEY, mSessionKey);
                intent.putExtras(extras);
                
                startActivity(intent);
            }
        });

        if (getIntent().getAction() != null && getIntent().getAction().equals("liqui.droid.sync.LOGIN")) {
            // we create a new account
            
            LinearLayout llExplore = (LinearLayout) findViewById(R.id.ll_explore);
            llExplore.setVisibility(View.GONE);
        }
        
        getSupportLoaderManager().initLoader(0, null, this);
    }

    /**
     * The Class LoginTask.
     */
    private class LoginTask extends AsyncTask<String, Void, Void> {

        private WeakReference<LiquiDroid> mTarget;

        private boolean mException;

        private boolean isAuthError;

        private String mExceptionMsg;
        
        private String mApiName;
        
        private String mApiUrl;

        /**
         * Instantiates a new load repository list task.
         * 
         * @param activity the activity
         */
        public LoginTask(LiquiDroid activity) {
            mTarget = new WeakReference<LiquiDroid>(activity);
        }

        /*
         * (non-Javadoc)
         * @see android.os.AsyncTask#doInBackground(Params[])
         */
        @Override
        protected Void doInBackground(String... params) {
            mApiName = params[0];
            mApiUrl  = params[1];
            
            if (mTarget.get() != null) {
                try {
                    String apiKey = mTarget.get().mEditTextApiKey.getText().toString().trim();

                    LiquidFeedbackServiceFactory factory = LiquidFeedbackServiceFactory
                            .newInstance(LoginTask.this.mApiUrl);
                    
                    SysInfoService sis = factory.createSysInfoService();
                    SessionService ss = factory.createSessionService();
                    String sessionKey = ss.getSessionKey(apiKey);
                    
                    sis.setAuthentication(new SessionKeyAuthentication(sessionKey));
                    SysInfo si = sis.getInfo();
                    
                    Log.d("XXXXXXXXX" , si.toString());
                    
                    Log.d("XXXXXX", "currentMemberId " + si.currentMemberId);
                    Log.d("XXXXXX", "currentAccessLevel " + si.currentAccessLevel);
                    
                    if (!"member".equals(si.currentAccessLevel)) {
                        isAuthError = true;
                        throw new LiquidFeedbackException("Invalid API key", null);
                    }

                    // authenticated
                    mMemberId = String.valueOf(si.currentMemberId);
                    mSessionKey = sessionKey;
                    
                    Uri LQFBUri = Uri.parse("content://liqui.droid.system/lqfbs");

                    // clear all last active entries
                    ContentValues valuesActive = new ContentValues();
                    valuesActive.put(DBSystem.TableLQFBs.COLUMN_LAST_ACTIVE, 0);
                    getContentResolver().update(LQFBUri, valuesActive, null, null);

                    // save last active entry and member + session values
                    ContentValues values = new ContentValues();
                    values.put(DBSystem.TableLQFBs.COLUMN_MEMBER_ID, mMemberId);
                    values.put(DBSystem.TableLQFBs.COLUMN_SESSION_KEY, mSessionKey);
                    values.put(DBSystem.TableLQFBs.COLUMN_LAST_ACTIVE, 1);
                    values.put(DBSystem.TableLQFBs.COLUMN_META_CACHED, System.currentTimeMillis());
                    getContentResolver().update(LQFBUri, values,
                            DBSystem.TableLQFBs.COLUMN_NAME + " = ?",
                            new String[] { LoginTask.this.mApiName });
                    
                    // if (!AccountAuthenticatorService.hasAccount(mTarget.get())) {
                        Account account = new Account(mMemberId + "@" + mApiName, "liqui.droid.account");

                        Log.d("XXX", "Adding account: " + account.toString());

                        Bundle userData = new Bundle();
                        userData.putString(Constants.Account.API_NAME, mApiName);
                        userData.putString(Constants.Account.API_URL,  mApiUrl);
                        userData.putString(Constants.Account.MEMBER_ID, mMemberId);
                        userData.putString(Constants.Account.SESSION_KEY, mSessionKey);
                        
                        Parcelable authResponse = null;
                        if (getIntent() != null && getIntent().getExtras() != null) {
                            authResponse = getIntent().getExtras().getParcelable("accountAuthenticatorResponse");
                        }
                        
                        // add system account
                        AccountAuthenticatorService.addAccount(LiquiDroid.this, mMemberId, apiKey, userData, authResponse);

                        // add periodic sync
                        Bundle extrasPeriodic = new Bundle();
                        long syncInterval = 60 * 60 * 12; // 12 hours
                        ContentResolver.addPeriodicSync(account, DBProvider.AUTHORITY, extrasPeriodic, syncInterval);
                        
                        // enable sync
                        ContentResolver.setSyncAutomatically(account, DBProvider.AUTHORITY, true);
                        
                        // force sync
                        Bundle extrasForce = new Bundle();
                        extrasForce.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
                        extrasForce.putBoolean(ContentResolver.SYNC_EXTRAS_IGNORE_SETTINGS, true);
                        ContentResolver.requestSync(account, DBProvider.AUTHORITY, extrasForce);

                    // }
                } catch (LiquidFeedbackException e) {
                    Log.e(Constants.LOG_TAG, e.getMessage(), e);

                    if (e.isForbidden()) {
                        isAuthError = true;
                    }
                    
                    mException = true;
                    mExceptionMsg = e.getMessage();
                    
                    if (e.getCause() != null) {
                        mExceptionMsg += ", " + e.getCause().getMessage();
                    }
                }

                return null;
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
                mTarget.get().mProgressDialog = ProgressDialog.show(mTarget.get(), null, mTarget.get()
                        .getResources().getString(R.string.lqfb_signing_in), true, false);
            }
        }

        /*
         * (non-Javadoc)
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        protected void onPostExecute(Void result) {
            if (mTarget.get() != null) {
                LiquiDroid activity = mTarget.get();
                activity.mProgressDialog.dismiss();
                if (mException && isAuthError) {
                    Toast.makeText(activity,
                            activity.getResources().getString(R.string.lqfb_invalid_api_key),
                            Toast.LENGTH_SHORT).show();
                } else if (mException) {
                    Toast.makeText(activity, mExceptionMsg, Toast.LENGTH_LONG).show();
                } else {
                    if (getIntent().getAction() != null && getIntent().getAction().equals("liqui.droid.sync.LOGIN")) {
                        activity.finish();
                    } else {
                        Intent intent = new Intent().setClass(LiquiDroid.this, MemberActivity.class);

                        Bundle extras = new Bundle();
                        extras.putString(Constants.Account.API_NAME,    mApiName);
                        extras.putString(Constants.Account.API_URL,     mApiUrl);
                        extras.putString(Constants.Account.MEMBER_ID,   mMemberId);
                        extras.putString(Constants.Account.SESSION_KEY, mSessionKey);
                        intent.putExtras(extras);
                    
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        activity.startActivity(intent);
                        activity.finish();
                    }
                }
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        Cursor c = (Cursor)parent.getItemAtPosition(pos);
        
        String name       = c.getString(c.getColumnIndex(DBSystem.TableLQFBs.COLUMN_NAME));
        String url        = c.getString(c.getColumnIndex(DBSystem.TableLQFBs.COLUMN_URL));
        String key        = c.getString(c.getColumnIndex(DBSystem.TableLQFBs.COLUMN_API_KEY));

        setSettingStringValue("SpinnerLogin", String.valueOf(mSpinnerLQFBs.getSelectedItemPosition()));
        
        mApiName = name;
        mApiUrl = url;
        
        mEditTextApiKey.setText(key);
        
        fillData();

        setActionBarTitle();
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
    }
    
    public void fillData() {
        TextView tvExplore = (TextView) findViewById(R.id.tv_explore);
        
        if (getAPIDB() != null) {
            tvExplore.setText(getString(R.string.lqfb_explore) + " " + getAPIName());
            tvExplore.setVisibility(View.VISIBLE);
        } else {
            tvExplore.setVisibility(View.GONE);
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

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, CONTENT_URI, null, null, null, "_id");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> arg0, Cursor cursor) {
        mAdapter.swapCursor(cursor);
        if (getSettingStringValue("SpinnerLogin") != null)
            mSpinnerLQFBs.setSelection(Integer.valueOf(getSettingStringValue("SpinnerLogin")));

        fillData();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> arg0) {
        mAdapter.swapCursor(null);
    }
    
    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        switch (resultCode) {
            case SyncService.STATUS_FINISHED: {
                fillData();
                break;
            }
        }
        
        super.onReceiveResult(resultCode, resultData);
    }
    
    /* (non-Javadoc)
     * @see android.app.Activity#onPrepareOptionsMenu(android.view.Menu)
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.anon_menu, menu);
        return true;
    }

}
