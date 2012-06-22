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
import liqui.droid.LQFBApplication;
import liqui.droid.db.DBSystem;
import liqui.droid.holder.BreadCrumbHolder;
import liqui.droid.service.SyncService;
import liqui.droid.util.ActionBar;
import liqui.droid.util.DetachableResultReceiver;
import liqui.droid.util.ScrollingTextView;
import liqui.droid.util.ActionBar.IntentAction;
import liqui.droid.R;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import java.lang.ref.WeakReference;
import java.util.HashMap;

/**
 * The Base activity.
 */
public class Base extends FragmentActivity implements DetachableResultReceiver.Receiver {
    
    protected boolean mSyncing = false;

    protected String mApiName;
    
    protected String mApiUrl;
    
    protected String mMemberId;
    
    protected String mSessionKey;

    protected DetachableResultReceiver mReceiver;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        
        outState.putBoolean("SYNCING", mSyncing);
    }
    
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
      super.onRestoreInstanceState(savedInstanceState);
      
      mSyncing = savedInstanceState.getBoolean("SYNCING");
      // setProgressVisible(mSyncing);
      
    }

    /* (non-Javadoc)
     * @see android.content.ContextWrapper#getApplicationContext()
     */
    @Override
    public LQFBApplication getApplicationContext() {
        return (LQFBApplication) super.getApplicationContext();
    }

    /**
     * Common function when device search button pressed, then open
     * SearchActivity.
     *
     * @return true, if successful
     */
    @Override
    public boolean onSearchRequested() {
    	Intent intent = new Intent().setClass(getApplication(), Search.class);
    	
        Bundle extras = new Bundle();
        extras.putString(Constants.Account.API_NAME,    getAPIName());
        extras.putString(Constants.Account.API_URL,     getAPIUrl());
        extras.putString(Constants.Account.MEMBER_ID,   getMemberId());
        extras.putString(Constants.Account.SESSION_KEY, getSessionKey());
        intent.putExtras(extras);
        
    	startActivity(intent);
        return true;
    }

    /**
     * Hide keyboard.
     *
     * @param binder the binder
     */
    public void hideKeyboard(IBinder binder) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(binder, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        if (isAuthenticated()
//                && this instanceof UserActivity) {
//            MenuInflater inflater = getMenuInflater();
//            inflater.inflate(R.menu.authenticated_menu, menu);
//        }
        if (!isAuthenticated()) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.anon_menu, menu);
        }
        return true;        
    }
    
    /* (non-Javadoc)
     * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection

        Bundle extras = new Bundle();
        extras.putString(Constants.Account.API_NAME,    getAPIName());
        extras.putString(Constants.Account.API_URL,     getAPIUrl());
        extras.putString(Constants.Account.MEMBER_ID,   getMemberId());
        extras.putString(Constants.Account.SESSION_KEY, getSessionKey());
        
        switch (item.getItemId()) {
        case R.id.menu_accounts:
            openAccounts();
            return true;
            /*
        case R.id.menu_logout:
            Uri LQFBUri = Uri.parse("content://liqui.droid.system/lqfbs");
            ContentValues values = new ContentValues();
            values.put(DBSystem.TableLQFBs.COLUMN_LAST_ACTIVE, 0);
            getContentResolver().update(LQFBUri, values,
                    DBSystem.TableLQFBs.COLUMN_NAME + " = ?",
                    new String[] { getAPIName() });

            Intent intent = new Intent().setClass(this, LQFB.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            startActivity(intent);
            Toast.makeText(this, getResources().getString(R.string.successful_signout), Toast.LENGTH_SHORT).show();
            this.finish();
            return true;
            */
        case R.id.menu_edit_lqfbs:
            Intent intentLQFBEdit = new Intent().setClass(this, LQFBListCached.class);
            intentLQFBEdit.putExtras(extras);
            startActivity(intentLQFBEdit);
            return true;
        case R.id.about:
            openAboutDialog();
            return true;
        case R.id.menu_contacts:
            openContacts();
            return true;
        case R.id.menu_feedback:
            openFeedbackDialog();
            return true;
        case R.id.menu_refresh:
            triggerRefresh();
            return true;
        case R.id.menu_test:
            triggerTest();
            return true;
            /*
        case R.id.membership:
            Intent intentMembership = new Intent().setClass(this, AreaListSelectActivity.class);
            startActivity(intentMembership);
            return true;
            */
        default:
            return setMenuOptionItemSelected(item);
        }
    }
    
    private void triggerTest() {
        Intent intent = new Intent().setClass(Base.this, Test.class);
        
        Bundle extras = new Bundle();
        extras.putString(Constants.Account.API_NAME,    getAPIName());
        extras.putString(Constants.Account.API_URL,     getAPIUrl());
        extras.putString(Constants.Account.MEMBER_ID,   getMemberId());
        extras.putString(Constants.Account.SESSION_KEY, getSessionKey());
        intent.putExtras(extras);

        startActivity(intent);
    }
    
    private void openAccounts() {
        final Intent intent = new Intent(Intent.ACTION_VIEW, null, Base.this, Accounts.class);
        
        Bundle extras = new Bundle();
        extras.putString(Constants.Account.API_NAME,    getAPIName());
        extras.putString(Constants.Account.API_URL,     getAPIUrl());
        extras.putString(Constants.Account.MEMBER_ID,   getMemberId());
        extras.putString(Constants.Account.SESSION_KEY, getSessionKey());
        intent.putExtras(extras);
        
        startActivityForResult(intent, 42);
    }

    private void openContacts() {
        final Intent intent = new Intent(Intent.ACTION_VIEW, null, Base.this, ContactListCached.class);
        
        Bundle extras = new Bundle();
        extras.putString(Constants.Account.API_NAME,    getAPIName());
        extras.putString(Constants.Account.API_URL,     getAPIUrl());
        extras.putString(Constants.Account.MEMBER_ID,   getMemberId());
        extras.putString(Constants.Account.SESSION_KEY, getSessionKey());
        intent.putExtras(extras);
        
        startActivity(intent);
    }

    private void triggerRefresh() {
        final Intent intent = new Intent(Intent.ACTION_SYNC, null, this, SyncService.class);
        intent.putExtra(SyncService.EXTRA_STATUS_RECEIVER, mReceiver);
        
        Bundle extras = new Bundle();
        extras.putString(Constants.Account.API_NAME,    getAPIName());
        extras.putString(Constants.Account.API_URL,     getAPIUrl());
        extras.putString(Constants.Account.MEMBER_ID,   getMemberId());
        extras.putString(Constants.Account.SESSION_KEY, getSessionKey());
        intent.putExtras(extras);
        
        startService(intent);
    }

    /**
     * Sets the menu option item selected.
     *
     * @param item the item
     * @return true, if successful
     */
    public boolean setMenuOptionItemSelected(MenuItem item) {
        return true;
    }
    
    /**
     * Open about dialog.
     */
    public void openAboutDialog() {
        Dialog dialog = new Dialog(this);

        dialog.setContentView(R.layout.dlg_about);
        
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String versionName = packageInfo.versionName;
            dialog.setTitle(getResources().getString(R.string.app_name) + " v" + versionName);
        } 
        catch (PackageManager.NameNotFoundException e) {
            dialog.setTitle(getResources().getString(R.string.app_name));
        }
        
        Button btnByEmail = (Button) dialog.findViewById(R.id.btn_by_email);
        btnByEmail.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View arg0) {
                Intent sendIntent = new Intent(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{getResources().getString(R.string.my_email)});
                sendIntent.setType("message/rfc822");
                startActivity(Intent.createChooser(sendIntent, "Select email application."));
            }
        });
        
        dialog.show();
    }
    
    /**
     * Open feedback dialog.
     */
    public void openFeedbackDialog() {
        Dialog dialog = new Dialog(this);

        dialog.setContentView(R.layout.dlg_feedback);
        dialog.setTitle(getResources().getString(R.string.feedback));
        
        Button btnByEmail = (Button) dialog.findViewById(R.id.btn_by_email);
        btnByEmail.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View arg0) {
                Intent sendIntent = new Intent(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{getResources().getString(R.string.my_email)});
                sendIntent.setType("message/rfc822");
                startActivity(Intent.createChooser(sendIntent, "Select email application."));
            }
        });
        
        dialog.show();
    }
    
    /**
     * Open donate dialog.
     */
    public void openDonateDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setTitle(getResources().getString(R.string.donate));
        dialog.setContentView(R.layout.dlg_donate);
        Button btn = (Button) dialog.findViewById(R.id.btn_donate);
        btn.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View view) {
                String url = "https://www.paypal.com/cgi-bin/webscr?cmd=_donations&business=CLFEUAAXKXLLU&lc=MY&item_name=Donate%20for%20Gh4a&currency_code=USD&bn=PP%2dDonationsBF%3abtn_donateCC_LG%2egif%3aNonHosted";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);     
            }
        });
        dialog.show();
    }
    
    /**
     * Creates the breadcrumb.
     *
     * @param subTitle the sub title
     * @param breadCrumbHolders the bread crumb holders
     */
    public void createBreadcrumb(String subTitle, BreadCrumbHolder... breadCrumbHolders) {
        if (breadCrumbHolders != null) {
            LinearLayout llPart = (LinearLayout) this.findViewById(R.id.ll_part);
            for (int i = 0; i < breadCrumbHolders.length; i++) {
                TextView tvBreadCrumb = new TextView(getApplication());
                SpannableString part = new SpannableString(breadCrumbHolders[i].getLabel());
                part.setSpan(new UnderlineSpan(), 0, part.length(), 0);
                tvBreadCrumb.append(part);
                tvBreadCrumb.setTag(breadCrumbHolders[i]);
                tvBreadCrumb.setBackgroundResource(R.drawable.default_link);
                tvBreadCrumb.setTextAppearance(getApplication(), R.style.default_text_small);
                tvBreadCrumb.setSingleLine(true);
                tvBreadCrumb.setOnClickListener(new OnClickBreadCrumb(this));
    
                llPart.addView(tvBreadCrumb);
    
                if (i < breadCrumbHolders.length - 1) {
                    TextView slash = new TextView(getApplication());
                    slash.setText(" / ");
                    slash.setTextAppearance(getApplication(), R.style.default_text_small);
                    llPart.addView(slash);
                }
            }
        }

        ScrollingTextView tvSubtitle = (ScrollingTextView) this.findViewById(R.id.tv_subtitle);
        tvSubtitle.setText(subTitle);
    }

    /**
     * Sets the up action bar.
     */
    public void setUpActionBar() {
        ActionBar actionBar = (ActionBar) findViewById(R.id.actionbar);
        
        Bundle extras = new Bundle();
        extras.putString(Constants.Account.API_NAME,    getAPIName());
        extras.putString(Constants.Account.API_URL,     getAPIUrl());
        extras.putString(Constants.Account.MEMBER_ID,   getMemberId());
        extras.putString(Constants.Account.SESSION_KEY, getSessionKey());

        if (isAuthenticated()) {
           Intent intent = new Intent().setClass(getApplicationContext(), MemberActivity.class);
           intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

           intent.putExtras(extras);
           
           actionBar.setHomeAction(new IntentAction(this, intent, R.drawable.ic_home));
        }
        // actionBar.addAction(new IntentAction(this, new Intent(getApplication(),
        //         ExploreActivity.class), R.drawable.ic_explore));
        
        Intent searchIntent = new Intent(getApplication(), Search.class);
        searchIntent.putExtras(extras);
        actionBar.addAction(new IntentAction(this, searchIntent, R.drawable.ic_search));
        
        setActionBarTitle();
    }
    
    public void setActionBarTitle() {
        ActionBar actionBar;
        
        actionBar = (ActionBar) findViewById(R.id.actionbar);

        if (this instanceof LiquiDroid) {
            actionBar.setTitle(R.string.title_sign_in);
            return;
        }
        
        if (this instanceof LQFBListCached
                || this instanceof LQFBEdit
                || this instanceof Accounts) {
            actionBar.setTitle(R.string.app_name);
        } else {       
            actionBar.setTitle(getAPIName());
        }
    }
    
    /**
     * Checks if is authenticated.
     *
     * @return true, if is authenticated
     */
    public boolean isAuthenticated() {
        return getMemberId() != null;
    }
    
    /**
     * The Class OnClickBreadCrumb.
     */
    private class OnClickBreadCrumb implements OnClickListener {

        /**
         * The target.
         */
        private WeakReference<Base> mTarget;

        /**
         * Instantiates a new on click bread crumb.
         *
         * @param activity the activity
         */
        public OnClickBreadCrumb(Base activity) {
            mTarget = new WeakReference<Base>(activity);
        }

        /* (non-Javadoc)
         * @see android.view.View.OnClickListener#onClick(android.view.View)
         */
        @Override
        public void onClick(View view) {
            TextView breadCrumb = (TextView) view;
            BreadCrumbHolder b = (BreadCrumbHolder) breadCrumb.getTag();
            String tag = b.getTag();
            HashMap<String, String> data = b.getData();

            Base baseActivity = mTarget.get();

            if (Constants.Member.LOGIN.equals(tag)) {
                mTarget.get().getApplicationContext().openUserInfoActivity(baseActivity,
                        data.get(Constants.Member.LOGIN), null);
            }
            else if (Constants.EXPLORE.equals(tag)) {
                Intent intent = new Intent().setClass(mTarget.get(), Explore.class);

                Bundle extras = new Bundle();
                extras.putString(Constants.Account.API_NAME,    getAPIName());
                extras.putString(Constants.Account.API_URL,     getAPIUrl());
                extras.putString(Constants.Account.MEMBER_ID,   getMemberId());
                extras.putString(Constants.Account.SESSION_KEY, getSessionKey());
                intent.putExtras(extras);
                
                mTarget.get().startActivity(intent);
            }
        }
    };

    /**
     * Show error.
     */
    public void showError() {
        Toast
                .makeText(getApplication(), "An error occured while fetching data",
                        Toast.LENGTH_SHORT).show();
        super.finish();
    }

    /**
     * Show error.
     *
     * @param finishThisActivity the finish this activity
     */
    public void showError(boolean finishThisActivity) {
        Toast
                .makeText(getApplication(), "An error occured while talking to the API",
                        Toast.LENGTH_SHORT).show();
        if (finishThisActivity) {
            super.finish();
        }
    }
    
    /**
     * Show message.
     *
     * @param message the message
     * @param finishThisActivity the finish this activity
     */
    public void showMessage(String message, boolean finishThisActivity) {
        Toast
                .makeText(getApplication(), message,
                        Toast.LENGTH_SHORT).show();
        if (finishThisActivity) {
            super.finish();
        }
    }
    
    /**
     * Checks if is setting enabled.
     *
     * @param key the key
     * @return true, if is setting enabled
     */
    public boolean isSettingEnabled(String key) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        return sp.getBoolean(key, false);
    }
    
    /**
     * Gets the setting string value.
     *
     * @param key the key
     * @return the setting string value
     */
    public String getSettingStringValue(String key) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        return sp.getString(key, null);
    }
    
    /**
     * Gets the setting string value.
     *
     * @param key the key
     * @param nullValue the null value
     * @return the setting string value
     */
    public String getSettingStringValue(String key, String nullValue) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        return sp.getString(key, nullValue);
    }
    
    /**
     * Sets the setting string value.
     *
     * @param key the key
     * @param value the value
     */
    public void setSettingStringValue(String key, String value) {
    	SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
    	sp.edit().putString(key, value).commit();
    }

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        
        mReceiver = new DetachableResultReceiver(new Handler());
        mReceiver.setReceiver(this);
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        // Toast.makeText(this, "resultCode: " + resultCode, Toast.LENGTH_SHORT).show();

        switch (resultCode) {
            case SyncService.STATUS_RUNNING: {
                mSyncing = true;
                // setProgressVisible(true);
                // Toast.makeText(this, "syncing..", Toast.LENGTH_SHORT).show();
                // Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                // v.vibrate(300);
                break;
            }
            case SyncService.STATUS_FINISHED: {
                mSyncing = false;
                // setProgressVisible(false);
                // Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                // v.vibrate(300);
                // Toast.makeText(this, "finished syncing.", Toast.LENGTH_SHORT).show();
                break;
            }
            case SyncService.STATUS_ERROR: {
                // Error happened down in SyncService, show as toast.
                mSyncing = false;
                // setProgressVisible(false);
                final String errorText = "sync error: " + resultData.getString(Intent.EXTRA_TEXT);
                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(300);
                Toast.makeText(this, errorText, Toast.LENGTH_LONG).show();
                break;
            }
        }
    }
    
    /**
     * Returns the current API URL.
     * 
     * @return the current API URL.
     */
    public String getAPIUrl() {
        if (mApiUrl != null) {
            return mApiUrl;
        } else {
            Bundle extras = getIntent().getExtras();
            
            String apiUrl = null;
            
            if (extras != null) {
                apiUrl = extras.getString(Constants.Account.API_URL);
            }
            
            return apiUrl;
        }
    }
    
    /**
     * Returns the current API name.
     * 
     * @return the current API name.
     */
    public String getAPIName() {
        if (mApiName != null) {
            return mApiName;
        } else {
            Bundle extras = getIntent().getExtras();
            
            String apiName = null;
            
            if (extras != null) {
                apiName = extras.getString(Constants.Account.API_NAME);
            }
            
            return apiName;
        }
    }

    /**
     * Returns the current DB name.
     * 
     * @return the current DB name.
     */
    public String getAPIDB() {
        if (getAPIName() != null) {
            return getMemberId() + "@" + getAPIName();
        } else {
            return null;
        }
    }

    /**
     * Returns the current session key.
     * 
     * @return the current session key.
     */
    public String getSessionKey() {
        if (mSessionKey != null) {
            return mSessionKey;
        } else {
            Bundle extras = getIntent().getExtras();
            
            String sessionKey = null;
            
            if (extras != null) {
                sessionKey = extras.getString(Constants.Account.SESSION_KEY);
            }
            
            return sessionKey;
        }
    }
    
    /**
     * Returns the current member id.
     * 
     * @return the current member id.
     */
    public String getMemberId() {
        if (mMemberId != null) {
            return mMemberId;
        } else {
            Bundle extras = getIntent().getExtras();
            
            String memberId = null;
            
            if (extras != null) {
                memberId = extras.getString(Constants.Account.MEMBER_ID);
            }
            
            return memberId;
        }
    }
    
    public void share(String subject, String text) {
        final Intent intent = new Intent(Intent.ACTION_SEND);

        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, text);

        startActivity(Intent.createChooser(intent, getString(R.string.share)));
    }
    
    public Uri dbUri(Uri uri) {
        return dbUri(uri.toString());
    }
    
    public Uri dbUri(String path) {
        String apiDB = getAPIDB();
        
        if (apiDB != null) {
            return Uri.parse(path).buildUpon().appendQueryParameter("db", getAPIDB()).build();
        } else {
            return Uri.parse(path);
        }
    }
    
    public boolean isResultEmpty(Uri uri, String selection, String[] selectionArgs, String orderBy) {
        boolean empty;
        
        Cursor c = getContentResolver().query(uri, null, selection, selectionArgs, orderBy);
        c.moveToFirst();
        
        if (c.isAfterLast()) {
            empty = true;
        } else {
            empty = false;
        }
        c.close();
        
        return empty;
    }
    
    public String queryString(Uri uri, String column, String selection, String[] selectionArgs, String orderBy) {
        Cursor c = getContentResolver().query(uri, null, selection, selectionArgs, orderBy);
    
        c.moveToFirst();
        
        
        String str = null;
    
        if (!c.isAfterLast()) {
            str = c.getString(c.getColumnIndex(column));
        }
    
        c.close();
        
        return str;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        
        switch(requestCode) { 
            case (42) : { // open accounts 
              if (resultCode == Activity.RESULT_OK) { 
              
                      Account account = intent.getParcelableExtra("account");
                      
                      AccountManager am = AccountManager.get(this);
                      
                      Log.d("XXX", "Selected account: " + account);
                      
                      Uri LQFBUri = Uri.parse("content://liqui.droid.system/lqfbs");

                      // clear all last active entries
                      ContentValues valuesActive = new ContentValues();
                      valuesActive.put(DBSystem.TableLQFBs.COLUMN_LAST_ACTIVE, 0);
                      getContentResolver().update(LQFBUri, valuesActive, null, null);

                      // save last active entry and member + session values
                      ContentValues values = new ContentValues();
                      values.put(DBSystem.TableLQFBs.COLUMN_MEMBER_ID, am.getUserData(account, Constants.Account.MEMBER_ID));
                      values.put(DBSystem.TableLQFBs.COLUMN_SESSION_KEY, am.getUserData(account, Constants.Account.SESSION_KEY));
                      values.put(DBSystem.TableLQFBs.COLUMN_LAST_ACTIVE, 1);
                      values.put(DBSystem.TableLQFBs.COLUMN_META_CACHED, System.currentTimeMillis());
                      getContentResolver().update(LQFBUri, values,
                              DBSystem.TableLQFBs.COLUMN_NAME + " = ?",
                              new String[] { am.getUserData(account, Constants.Account.API_NAME) });
                      
                      // start member activity
                      Bundle extras = new Bundle();
                      extras.putString(Constants.Account.API_NAME,    am.getUserData(account, Constants.Account.API_NAME));
                      extras.putString(Constants.Account.API_URL,     am.getUserData(account, Constants.Account.API_URL));
                      extras.putString(Constants.Account.MEMBER_ID,   am.getUserData(account, Constants.Account.MEMBER_ID));
                      extras.putString(Constants.Account.SESSION_KEY, am.getUserData(account, Constants.Account.SESSION_KEY));

                      Intent i = new Intent().setClass(getApplicationContext(), MemberActivity.class);
                      i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                      i.putExtras(extras);
                      startActivity(i);
              }
              break; 
            } 
          } 
    }
    
    
}