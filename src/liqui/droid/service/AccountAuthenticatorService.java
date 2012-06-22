package liqui.droid.service;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;
import android.util.Log;

import liqui.droid.Constants;
import liqui.droid.activity.LiquiDroid;
import liqui.droid.db.DBProvider;

public class AccountAuthenticatorService extends Service {
    
    private static final String TAG = "AccountAuthenticatorService";
    
    private static AccountAuthenticatorImpl sAccountAuthenticator = null;

    public AccountAuthenticatorService() {
        super();
    }
    
    private static class AccountAuthenticatorImpl extends AbstractAccountAuthenticator {
        private Context mContext;

        public AccountAuthenticatorImpl(Context context) {
            super(context);
            mContext = context;
        }

        public static Bundle addAccount(Context ctx, String memberId, String apiKey, Bundle userData) {
            Bundle result = null;
            
            String apiName = userData.getString(Constants.Account.API_NAME);
            
            Account account = new Account(memberId + "@" + apiName, "liqui.droid.account");
            AccountManager am = AccountManager.get(ctx);
            
            // Log.d("XXX", "addAccount userdata: " + userData.toString());
            
            if (am.addAccountExplicitly(account, apiKey, userData)) {
                // Log.d("XXXXXXXXXXXXXXXXXX", "addAccountExplicitly success");
                
                am.setUserData(account, Constants.Account.API_NAME, userData.getString(Constants.Account.API_NAME));
                am.setUserData(account, Constants.Account.API_URL,  userData.getString(Constants.Account.API_URL));
                
                am.setUserData(account, Constants.Account.MEMBER_ID, userData.getString(Constants.Account.MEMBER_ID));
                am.setUserData(account, Constants.Account.SESSION_KEY, userData.getString(Constants.Account.SESSION_KEY));
                
                result = new Bundle();
                result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
                result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
            }
            return result;
        }

        public static Boolean hasLQFBAccount(Context ctx) {
            AccountManager am = AccountManager.get(ctx);
            Account[] accounts = am.getAccountsByType("liqui.droid.account");
            if(accounts != null && accounts.length > 0)
                return true;
            else
                return false;
        }

        public static void removeLQFBAccount(Context ctx) {
            AccountManager am = AccountManager.get(ctx);
            Account[] accounts = am.getAccountsByType("liqui.droid.account");
            for(Account account : accounts) {
                am.removeAccount(account, null, null);
            }
        }

        /* (non-Javadoc)
         * @see android.accounts.AbstractAccountAuthenticator#addAccount(android.accounts.AccountAuthenticatorResponse, java.lang.String, java.lang.String, java.lang.String[], android.os.Bundle)
         */
        @Override
        public Bundle addAccount(AccountAuthenticatorResponse response, String accountType, String authTokenType, String[] requiredFeatures, Bundle options)
                throws NetworkErrorException {
            Bundle result;

            /*
            if(hasLQFBAccount(mContext)) {
                result = new Bundle();
                
                Log.d("XXX", "hasLQFBAccount()");

                Intent i = new Intent(mContext, AccountFailActivity.class);
                i.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
                result.putParcelable(AccountManager.KEY_INTENT, i);

                return result;
            } else { */
                result = new Bundle();

                Intent i = new Intent(mContext, LiquiDroid.class);
                i.setAction("liqui.droid.sync.LOGIN");
                i.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
                result.putParcelable(AccountManager.KEY_INTENT, i);
            //}           
            return result;
        }

        /* (non-Javadoc)
         * @see android.accounts.AbstractAccountAuthenticator#confirmCredentials(android.accounts.AccountAuthenticatorResponse, android.accounts.Account, android.os.Bundle)
         */
        @Override
        public Bundle confirmCredentials(AccountAuthenticatorResponse response, Account account, Bundle options) {
            Log.i(TAG, "confirmCredentials");
            return null;
        }

        /* (non-Javadoc)
         * @see android.accounts.AbstractAccountAuthenticator#editProperties(android.accounts.AccountAuthenticatorResponse, java.lang.String)
         */
        @Override
        public Bundle editProperties(AccountAuthenticatorResponse response, String accountType) {
            Log.i(TAG, "editProperties");
            return null;
        }

        /* (non-Javadoc)
         * @see android.accounts.AbstractAccountAuthenticator#getAuthToken(android.accounts.AccountAuthenticatorResponse, android.accounts.Account, java.lang.String, android.os.Bundle)
         */
        @Override
        public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {
            Log.i(TAG, "getAuthToken");
            
            AccountManager am = AccountManager.get(mContext);
            String memberId   = account.name.toLowerCase().trim();
//            String apiKey     = am.getPassword(account);
            String apiName    = am.getUserData(account, Constants.Account.API_NAME);
            String apiUrl     = am.getUserData(account, Constants.Account.API_URL);
            String sessionKey = am.getUserData(account, Constants.Account.SESSION_KEY);

            Bundle result = new Bundle();
            
            result.putString(Constants.Account.API_NAME, apiName);
            result.putString(Constants.Account.API_URL,  apiUrl);
            result.putString(Constants.Account.MEMBER_ID, memberId);
            result.putString(Constants.Account.SESSION_KEY, sessionKey);

            /*
            Intent i = new Intent(mContext, AccountAccessPrompt.class);
            i.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
            i.putExtra("api_key", api_key);
            i.putExtra("api_secret", api_secret);
            i.putExtra("user", user);
            // i.putExtra("authToken", authToken);
            result.putParcelable(AccountManager.KEY_INTENT, i);
            */
            return result;
        }

        /* (non-Javadoc)
         * @see android.accounts.AbstractAccountAuthenticator#getAuthTokenLabel(java.lang.String)
         */
        @Override
        public String getAuthTokenLabel(String authTokenType) {
            Log.i(TAG, "getAuthTokenLabel");
            return null;
        }

        /* (non-Javadoc)
         * @see android.accounts.AbstractAccountAuthenticator#hasFeatures(android.accounts.AccountAuthenticatorResponse, android.accounts.Account, java.lang.String[])
         */
        @Override
        public Bundle hasFeatures(AccountAuthenticatorResponse response, Account account, String[] features) throws NetworkErrorException {
            Log.i(TAG, "hasFeatures: " + features);
            return null;
        }

        /* (non-Javadoc)
         * @see android.accounts.AbstractAccountAuthenticator#updateCredentials(android.accounts.AccountAuthenticatorResponse, android.accounts.Account, java.lang.String, android.os.Bundle)
         */
        @Override
        public Bundle updateCredentials(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) {
            Log.i(TAG, "updateCredentials");
            return null;
        }
    }

    @Override
    public IBinder onBind(Intent intent) { 
        IBinder ret = null;
        if (intent.getAction().equals(android.accounts.AccountManager.ACTION_AUTHENTICATOR_INTENT)) 
            ret = getAuthenticator().getIBinder();
        return ret;
    }

    public static void addAccount(Context ctx, String username, String password, Bundle userData, Parcelable response) {
        AccountAuthenticatorResponse authResponse = (AccountAuthenticatorResponse)response;
        Bundle result = AccountAuthenticatorImpl.addAccount(ctx, username, password, userData);
        if(authResponse != null)
            authResponse.onResult(result);
    }

    public static Boolean hasAccount(Context ctx) {
        return AccountAuthenticatorImpl.hasLQFBAccount(ctx);
    }

    public static void removeAccount(Context ctx) {
        AccountAuthenticatorImpl.removeLQFBAccount(ctx);
    }

    public static void resyncAccount(Context context) {
        Log.i(TAG, "resyncAccount");
        AccountManager am = AccountManager.get(context);
        Account[] accounts = am.getAccountsByType("liqui.droid.account");
        if (ContentResolver.getSyncAutomatically(accounts[0], DBProvider.AUTHORITY)) {
            //Try turning it off and on again
            ContentResolver.setSyncAutomatically(accounts[0], DBProvider.AUTHORITY, false);
            ContentResolver.setSyncAutomatically(accounts[0], DBProvider.AUTHORITY, true);
        }
    }

    private AccountAuthenticatorImpl getAuthenticator() { 
        if (sAccountAuthenticator == null)
            sAccountAuthenticator = new AccountAuthenticatorImpl(this);
        return sAccountAuthenticator;
    }
}
