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

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

public class DBSystemProvider extends ContentProvider {
    
    private DBSystem db;
    
    private static final String AUTHORITY = "liqui.droid.system";
    
    // lqfbs
    private static final int LQFBS    = 1;
    private static final int LQFBS_ID = 2;
    private static final String LQFBS_PATH = "lqfbs";

    public static final Uri     LQFBS_CONTENT_URI  = Uri.parse("content://" + AUTHORITY + "/" + LQFBS_PATH);
    public static final String  LQFBS_CONTENT_TYPE =      ContentResolver.CURSOR_DIR_BASE_TYPE  + "/lqfbs";
    public static final String  LQFBS_CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/lqfb";

    private static final UriMatcher URIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    
    static {
        URIMatcher.addURI(AUTHORITY, LQFBS_PATH,        LQFBS);
        URIMatcher.addURI(AUTHORITY, LQFBS_PATH + "/#", LQFBS_ID);
    }

    @Override
    public boolean onCreate() {
        
        db = new DBSystem(getContext());
        return false;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int uriType = URIMatcher.match(uri);
        
        SQLiteDatabase sqlDB = db.getWritableDatabase();
        long id = 0;
        
        switch (uriType) {
            case LQFBS:
                id = sqlDB.insertWithOnConflict("lqfb", null, values, SQLiteDatabase.CONFLICT_REPLACE);
                getContext().getContentResolver().notifyChange(uri, null);
                return Uri.parse(LQFBS_PATH + "/" + id);
        default:
            throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int uriType = URIMatcher.match(uri);
        
        SQLiteDatabase sqlDB = db.getWritableDatabase();
        
        int rowsUpdated = 0;
        
        switch (uriType) {
        case LQFBS:
            rowsUpdated = sqlDB.update(DBSystem.TableLQFBs.TABLE, 
                    values, 
                    selection,
                    selectionArgs);
            break;
        case LQFBS_ID:
            String id = uri.getLastPathSegment();
            if (TextUtils.isEmpty(selection)) {
                rowsUpdated = sqlDB.update(DBSystem.TableLQFBs.TABLE, 
                        values,
                        DBSystem.TableLQFBs.COLUMN_ID + "=" + id, 
                        null);
            } else {
                rowsUpdated = sqlDB.update(DBSystem.TableLQFBs.TABLE, 
                        values,
                        DBSystem.TableLQFBs.COLUMN_ID  + "=" + id 
                        + " AND " 
                        + selection,
                        selectionArgs);
            }
            break;
        default:
            throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        // Make sure that potential listeners are getting notified
        getContext().getContentResolver().notifyChange(uri, null);
        
        return rowsUpdated;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int uriType = URIMatcher.match(uri);
        SQLiteDatabase sqlDB = db.getWritableDatabase();
        
        int rowsDeleted = 0;
        
        switch (uriType) {
        case LQFBS:
            rowsDeleted = sqlDB.delete(DBSystem.TableLQFBs.TABLE, selection, selectionArgs);
            break;
        case LQFBS_ID:
            String id = uri.getLastPathSegment();

            if (TextUtils.isEmpty(selection)) {
                rowsDeleted = sqlDB.delete(DBSystem.TableLQFBs.TABLE, DBSystem.TableLQFBs.COLUMN_ID + "=" + id,  null);
            } else {
                rowsDeleted = sqlDB.delete(DBSystem.TableLQFBs.TABLE,
                        DBSystem.TableLQFBs.COLUMN_ID + "=" + id 
                        + " and " + selection,
                        selectionArgs);
            }
            break;
        default:
            throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        
        // Make sure that potential listeners are getting notified
        getContext().getContentResolver().notifyChange(uri, null);

        return rowsDeleted;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
            String sortOrder) {
        
        SQLiteDatabase sdb = db.getWritableDatabase();

        // Using SQLiteQueryBuilder instead of query() method
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        // Check if the caller has requested a column which does not exists
        // checkColumns(projection);

        Cursor cursor;
        
        switch (URIMatcher.match(uri)) {
            case LQFBS:
                queryBuilder.setTables("lqfb");
                
                cursor = queryBuilder.query(sdb, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case LQFBS_ID:
                queryBuilder.setTables("lqfb");
                
                // Adding the ID to the original query
                queryBuilder.appendWhere("_id" + "=" + uri.getLastPathSegment());

                cursor = queryBuilder.query(sdb, projection, selection, selectionArgs, null, null, sortOrder);
                break;
        default:
            throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        // Make sure that potential listeners are getting notified
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

}
