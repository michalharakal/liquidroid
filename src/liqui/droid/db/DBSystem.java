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

package liqui.droid.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import liqui.droid.Constants;

public class DBSystem extends SQLiteOpenHelper {
    
    private static final String DATABASE = "system.db";

    private static final int VERSION = 10;
    
    public static class TableLQFBs {
        public static final String TABLE = "lqfb";
        public static final String COLUMN_ID          = "_id";
        public static final String COLUMN_NAME        = "name";
        public static final String COLUMN_URL         = "api_url";
        public static final String COLUMN_WEB_URL     = "web_url";
        public static final String COLUMN_API_KEY     = "api_key";

        public static final String COLUMN_SESSION_KEY = "session_key";
        public static final String COLUMN_MEMBER_ID   = "member_id";
        public static final String COLUMN_LAST_ACTIVE = "last_active";
        
        public static final String COLUMN_META_CACHED = "meta_cached";
        
        public static String[] COLUMNS = new String[] {
            COLUMN_ID,
            COLUMN_NAME,
            COLUMN_URL,
            COLUMN_WEB_URL,
            COLUMN_API_KEY,
            COLUMN_SESSION_KEY,
            COLUMN_MEMBER_ID,
            COLUMN_LAST_ACTIVE,
            COLUMN_META_CACHED
        };
        
        public static void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE lqfb(                  " +
                    "_id  INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name TEXT                             , " +
                    "api_url TEXT                          , " +
                    "web_url TEXT                          , " +
                    "api_key TEXT                          , " +
                    "session_key TEXT                      , " +
                    "member_id INTEGER                     , " +
                    "last_active INTEGER                   , " +
                    "meta_cached TIMESTAMP                  " +
                    ");");
            
            db.execSQL("INSERT INTO lqfb VALUES(1          , " +
                    "'local-test'                          , " +
                    "'http://192.168.42.171:25520/'        , " +
                    "'http://192.168.42.171/lf2/'          , " +
                    "'1234'                                , " +
                    "null                                  , " +
                    "null                                  , " +
                    "0                                     , " +
                    System.currentTimeMillis()                 +
                    ");");

            db.execSQL("INSERT INTO lqfb VALUES(2          , " +
                    "'apitest.liquidfeedback.org'          , " +
                    "'" + Constants.API.apiUrl + "'        , " +
                    "'" + Constants.API.webUrl + "'        , " +
                    "'WCMN2bxlyd7StUDGBtXFzIB'             , " +
                    "null                                  , " +
                    "null                                  , " +
                    "0                                     , " +
                    System.currentTimeMillis()                 +
                    ");");
            
        }
        
        public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE); onCreate(db);
        }
    }
    
    public DBSystem(Context connection) {
        super(connection, DATABASE, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        
        // system tables
        TableLQFBs.onCreate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int ov, int nv) {
        // system tables
        TableLQFBs.onUpgrade(db, ov, nv);
    }
}
