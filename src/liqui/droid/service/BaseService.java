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

import android.content.Intent;
import android.os.Bundle;

import liqui.droid.Constants;
import liqui.droid.util.InfiniteWakelockIntentService;

public abstract class BaseService extends InfiniteWakelockIntentService {
    
    protected Intent mIntent;
    
    protected String mApiName;
    
    protected String mApiUrl;
    
    protected String mMemberId;
    
    protected String mSessionKey;

    public BaseService(String tag) {
        super(tag);
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
    
    @Override
    protected void onHandleIntent(Intent intent) {
        mIntent = intent;
    }
    
    protected Intent getIntent() {
        return mIntent;
    }
    
}
