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

package liqui.droid.service.task;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import lfapi.v2.services.LiquidFeedbackException;
import lfapi.v2.services.LiquidFeedbackServiceFactory;
import liqui.droid.Constants;
import liqui.droid.service.SyncService;

public abstract class SyncAbstractTask implements Runnable {
    
    public static final long SYNC_TIME_MIN_5   =  5 * 60 * 1000;
    public static final long SYNC_TIME_MIN_15  = 15 * 60 * 1000;
    public static final long SYNC_TIME_HOUR_1  = 60 * 60 * 1000;
    public static final long SYNC_TIME_HOUR_12 = SYNC_TIME_HOUR_1 * 12;
    
    LiquidFeedbackServiceFactory mFactory;
    
    Context mCtx;
    
    String databaseName;
    
    String tableName;
    
    Exception mException;
    
    long mSyncTime;
    
    protected Intent mIntent;
    
    protected String mApiName;
    
    protected String mApiUrl;
    
    protected String mMemberId;
    
    protected String mSessionKey;

    public SyncAbstractTask(Context ctx, Intent intent, LiquidFeedbackServiceFactory factory,
            String databaseName, String tableName, long syncTime) {
        this.mCtx = ctx;
        this.mIntent = intent;
        this.mFactory = factory;
        this.databaseName = databaseName;
        this.tableName = tableName;
        this.mSyncTime = syncTime;

        /*
        this.mApiName  = intent.getExtras().getString(Constants.Account.API_NAME);
        this.mApiUrl   = intent.getExtras().getString(Constants.Account.API_URL);
        this.mMemberId = intent.getExtras().getString(Constants.Account.MEMBER_ID);
        this.mSessionKey = intent.getExtras().getString(Constants.Account.SESSION_KEY);
        */
    }
    
    public abstract void sync(Context ctx, String ids) throws LiquidFeedbackException;
    
    protected void dirtyMark() {
        ContentValues values = new ContentValues();
        values.put("meta_cached", 0);
        
        mCtx.getContentResolver().update(dbUri("content://liqui.droid.db/" + tableName + "s"), values, null, null);
    }
    
    protected void dirtyDelete() {
        mCtx.getContentResolver().delete(dbUri("content://liqui.droid.db/" + tableName + "s"), "meta_cached = 0", null);
    }
    
    public void run() {
        String ids = null;
        
        if (SyncService.updateNeeded(mCtx, databaseName, tableName, mSyncTime)) {
            try {
                // Log.d("XXX", "Syncing " + tableName + " ids " + ids);
                dirtyMark();
                sync(mCtx, ids);
                dirtyDelete();
                SyncService.updated(mCtx, databaseName, tableName);
            } catch (Exception e) {
                mException = e;
                Log.d("Exception in sync " + tableName + " ids " + ids, e.toString());
                e.printStackTrace();
            }
        }
    }

    public boolean hasException() {
        return mException != null;
    }
    
    public Exception getException() {
        return mException;
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
    
    protected Intent getIntent() {
        return mIntent;
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
    
}