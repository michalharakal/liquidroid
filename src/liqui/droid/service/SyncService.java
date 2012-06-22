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

package liqui.droid.service;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.OperationCanceledException;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.util.Log;

import org.joda.time.DateTime;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import lfapi.v2.services.LiquidFeedbackServiceFactory;
import liqui.droid.Constants;
import liqui.droid.db.DBProvider;
import liqui.droid.service.task.SyncAbstractTask;
import liqui.droid.service.task.SyncArea;
import liqui.droid.service.task.SyncBattle;
import liqui.droid.service.task.SyncDelegatingVoter;
import liqui.droid.service.task.SyncDelegation;
import liqui.droid.service.task.SyncDraft;
import liqui.droid.service.task.SyncEvent;
import liqui.droid.service.task.SyncInitiative;
import liqui.droid.service.task.SyncInitiator;
import liqui.droid.service.task.SyncInterest;
import liqui.droid.service.task.SyncIssue;
import liqui.droid.service.task.SyncIssueComment;
import liqui.droid.service.task.SyncMember;
import liqui.droid.service.task.SyncMemberImage;
import liqui.droid.service.task.SyncMembership;
import liqui.droid.service.task.SyncOpinion;
import liqui.droid.service.task.SyncPolicy;
import liqui.droid.service.task.SyncPrivilege;
import liqui.droid.service.task.SyncSuggestion;
import liqui.droid.service.task.SyncSupporter;
import liqui.droid.service.task.SyncUnit;
import liqui.droid.service.task.SyncVote;
import liqui.droid.service.task.SyncVoter;
// import liqui.droid.service.task.SyncVoteComment;

public class SyncService extends BaseService {

    private static final String TAG = "SyncService";
    
    public static final int STATUS_RUNNING  = 0x1;
    public static final int STATUS_ERROR    = 0x2;
    public static final int STATUS_FINISHED = 0x3;

    public static final String EXTRA_STATUS_RECEIVER = "liqui.droid.extra.STATUS_RECEIVER";

    LiquidFeedbackServiceFactory mFactory;
    
    ExecutorService mExecutor;
    
    public static final int SLOTS = 2;
    
    SyncAbstractTask[] mTasks;
    
    public SyncService() {
        super(TAG);
    }
    
    protected void setup(Intent intent) {
        // mExecutor = Executors.newSingleThreadExecutor();
        mExecutor = Executors.newFixedThreadPool(SLOTS);
        
        mFactory = LiquidFeedbackServiceFactory.newInstance(getAPIUrl());
        String databaseName = getAPIDB();
        
        mTasks = new SyncAbstractTask[] {
                new SyncArea(this, intent, mFactory, databaseName),
                new SyncBattle(this, intent, mFactory, databaseName),
                new SyncIssueComment(this, intent, mFactory, databaseName),
                new SyncDelegation(this, intent, mFactory, databaseName),
                new SyncDraft(this, intent, mFactory, databaseName),
                new SyncEvent(this, intent, mFactory, databaseName),
                new SyncInitiative(this, intent, mFactory, databaseName),
                new SyncInitiator(this, intent, mFactory, databaseName),
                new SyncInterest(this, intent, mFactory, databaseName),
                new SyncIssue(this, intent, mFactory, databaseName),
                new SyncMember(this, intent, mFactory, databaseName),
                // FIXME new SyncMemberContact(this, intent, mFactory, databaseName),
                new SyncMemberImage(this, intent, mFactory, databaseName),
                new SyncMembership(this, intent, mFactory, databaseName),
                new SyncOpinion(this, intent, mFactory, databaseName),
                new SyncPolicy(this, intent, mFactory, databaseName),
                new SyncPrivilege(this, intent, mFactory, databaseName),
                new SyncSuggestion(this, intent, mFactory, databaseName),
                new SyncSupporter(this, intent, mFactory, databaseName),
                new SyncUnit(this, intent, mFactory, databaseName),
                new SyncVote(this, intent, mFactory, databaseName),
                // FIXME new SyncVoteComment(this, intent, mFactory, databaseName),
                new SyncVoter(this, intent, mFactory, databaseName),
                new SyncDelegatingVoter(this, intent, mFactory, databaseName)
        };
    }
    
    @Override
    public void onCreate() {
        super.onCreate();        
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        super.onHandleIntent(intent);
        
        Log.d(TAG, "onHandleIntent(intent=" + intent.toString() + ")");
        
        setup(intent);
        
        final ResultReceiver receiver = intent.getParcelableExtra(EXTRA_STATUS_RECEIVER);
        if (receiver != null) receiver.send(STATUS_RUNNING, Bundle.EMPTY);
        
        try {
            for (SyncAbstractTask task : mTasks) {
                mExecutor.execute(task);
            }
            
            mExecutor.shutdown();
            mExecutor.awaitTermination(30, TimeUnit.MINUTES);
            
            clearSyncError();
        } catch (Exception e) {
            Log.e(TAG, "Problem while syncing", e);
            
            setSyncError();

            if (receiver != null) {
                // Pass back error to surface listener
                final Bundle bundle = new Bundle();
                bundle.putString(Intent.EXTRA_TEXT, e.getMessage());
                receiver.send(STATUS_ERROR, bundle);
            }
        }

        Log.d(TAG, "sync finished");

        for (SyncAbstractTask task : mTasks) {
            if (task.hasException()) {
                Log.d(TAG, "Exception: " + task.getException());
            }
        }

        // Announce success to any surface listener
        if (receiver != null) receiver.send(STATUS_FINISHED, Bundle.EMPTY);
    }
    
    public static long lastSyncError(Context ctx, String databaseName) {
        return lastUpdate(ctx, databaseName, "_syncerror");
    }
    
    public void setSyncError() {
        updated(this, getAPIDB(), "_syncerror");
    }
    
    public void clearSyncError() {
        updated(this, getAPIDB(), "_syncerror", 0l);
    }

    public static void println(String txt) {
        Log.d(TAG, txt);
    }

    public static boolean updateNeeded(Context context, String databaseName, String table, long syncTime) {
        /*
        if ("event".equals(table)) {
            println("needs update: " + table);
            return true;
        }*/
        
        String selection = "name = ?";
        String[] selectionArgs = new String[] { table };
        
        Uri uri = DBProvider.UPDATED_CONTENT_URI.buildUpon().appendQueryParameter("db", databaseName).build();
        Cursor cursor = context.getContentResolver().query(uri, null, selection, selectionArgs, "updated DESC LIMIT 1");
        
        long updated = -1;
        
        try {
            if (!cursor.moveToFirst()) {
                updated = 1;
            } else if (cursor.getCount() == 0) {
                updated = 0;
            } else {
                updated = cursor.getLong(1);
            }
        } finally {
            cursor.close();
        }
        
        // println("updated " + table + " " + updated);
        
        DateTime now = DateTime.now();
        DateTime t = new DateTime(updated);
        boolean before = t.plus(syncTime).isBefore(now);
        
        if (updated == 0 || updated == 1 || before) {
            println("needs update: " + table);
            return true;
        } else {
            println("needs no update (last update " + (-t.getMillis() + now.getMillis()) / 1000 + " seconds ago): " +table );
            return false;
        }
    }
    
    public static void updated(Context context, String databaseName, String table) {
        updated(context, databaseName, table, DateTime.now().getMillis());
    }

    public static void updated(Context context, String databaseName, String table, Long value) {
        ContentValues values = new ContentValues();
        
        values.put("name", table);
        values.put("updated", value);
        
        Uri uri = DBProvider.UPDATED_CONTENT_URI.buildUpon().appendQueryParameter("db", databaseName).build();
        context.getContentResolver().insert(uri, values);
    }

    @Override
    protected boolean isFinished() {
        return mExecutor.isTerminated();
    }

    public static long lastUpdate(Context context, String databaseName, String table) {

        String selection = "name = ?";
        String[] selectionArgs = new String[] { table };
        
        Uri uri = DBProvider.UPDATED_CONTENT_URI.buildUpon().appendQueryParameter("db", databaseName).build();
        Cursor cursor = context.getContentResolver().query(uri, null, selection, selectionArgs, "updated DESC LIMIT 1");
        
        long updated = -1;
        
        try {
            if (!cursor.moveToFirst()) {
                updated = 1;
            } else if (cursor.getCount() == 0) {
                updated = 0;
            } else {
                updated = cursor.getLong(1);
            }
        } finally {
            cursor.close();
        }
        
        return updated;
    }

    private class SyncAdapterImpl extends AbstractThreadedSyncAdapter {
        private Context mContext;
        
        public SyncAdapterImpl(Context context, boolean autoInitialize) {
            super(context, autoInitialize);
            mContext = context;
        }
    
        @Override
        public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
            try {
                SyncService.this.performSync(mContext, account, extras, authority, provider, syncResult);
            } catch (OperationCanceledException e) {
            }
        }
    }
    
    @Override
    public IBinder onBind(Intent intent) {
        IBinder ret = null;
        ret = getSyncAdapter().getSyncAdapterBinder();
        return ret;
    }
    
    private static SyncAdapterImpl sSyncAdapter = null;
    
    private SyncAdapterImpl getSyncAdapter() {
        if (sSyncAdapter == null)
            sSyncAdapter = new SyncAdapterImpl(this, true);
        return sSyncAdapter;
    }
    
    private void performSync(Context context, Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult)
            throws OperationCanceledException {

        AccountManager am = AccountManager.get(context);
        
        mApiName     = am.getUserData(account, Constants.Account.API_NAME);
        mApiUrl      = am.getUserData(account, Constants.Account.API_URL);
        mMemberId    = am.getUserData(account, Constants.Account.MEMBER_ID);
        mSessionKey  = am.getUserData(account, Constants.Account.SESSION_KEY);
        
        mIntent = new Intent();
        Bundle b = new Bundle();
        b.putString(Constants.Account.API_NAME,    mApiName);
        b.putString(Constants.Account.API_URL,     mApiUrl);
        b.putString(Constants.Account.MEMBER_ID,   mMemberId);
        b.putString(Constants.Account.SESSION_KEY, mSessionKey);
        mIntent.putExtras(b);
        
        Log.i(TAG, "performSync: " + account.toString() + "bundle: " + b.toString() );
        
        onHandleIntent(mIntent);
        
        syncResult.delayUntil = 60 * 15; // wait 15 minutes for the next sync to happen.
    }
}
