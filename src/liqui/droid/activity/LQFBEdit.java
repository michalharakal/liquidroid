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


import liqui.droid.db.DBSystem;
import liqui.droid.db.DBSystemProvider;
import liqui.droid.R;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.Button;
import android.widget.Toast;
import android.widget.EditText;

/**
 * The LQFB edit activity.
 */
public class LQFBEdit extends Base {

    public static final Uri CONTENT_URI = Uri.parse("content://liqui.droid.system/lqfbs");
    
    protected Uri mLQFBUri;

    /**
     * Called when the activity is first created.
     * 
     * @param bundle the saved instance state
     */
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        setContentView(R.layout.act_lqfb_edit);
        setUpActionBar();
        
        Button confirmButton = (Button) findViewById(R.id.btn_lqfb_save);
        Bundle extras = getIntent().getExtras();

        // Check from the saved Instance
        mLQFBUri = (bundle == null) ? null : (Uri) bundle
                .getParcelable(DBSystemProvider.LQFBS_CONTENT_ITEM_TYPE);

        // Or passed from the other activity
        if (extras != null) {
            mLQFBUri = extras
                    .getParcelable(DBSystemProvider.LQFBS_CONTENT_ITEM_TYPE);

            fillData(mLQFBUri);
        }
        
        confirmButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                setResult(RESULT_OK);
                finish();
            }

        });
    }

    /**
     * Fill data into UI components.
     * 
     * @param user the user
     */
    protected void fillData(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        
        if (cursor != null) {
            cursor.moveToFirst();
        
            EditText et_name = (EditText) findViewById(R.id.et_lqfb_name);
            EditText et_url = (EditText) findViewById(R.id.et_lqfb_url);
            EditText et_web_url = (EditText) findViewById(R.id.et_lqfb_web_url);
            EditText et_key = (EditText) findViewById(R.id.et_lqfb_key);
            
            et_name.setText(cursor.getString(cursor.getColumnIndex(DBSystem.TableLQFBs.COLUMN_NAME)));
            et_url.setText(cursor.getString(cursor.getColumnIndex(DBSystem.TableLQFBs.COLUMN_URL)));
            et_web_url.setText(cursor.getString(cursor.getColumnIndex(DBSystem.TableLQFBs.COLUMN_WEB_URL)));
            et_key.setText(cursor.getString(cursor.getColumnIndex(DBSystem.TableLQFBs.COLUMN_API_KEY)));

            cursor.close();
        }
    }
    
    protected void onSavedInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveState();
        outState.putParcelable(DBSystemProvider.LQFBS_CONTENT_ITEM_TYPE, mLQFBUri);
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        saveState();
    }

    /**
     * Save state.
     */
    public void saveState() {
        EditText et_name = (EditText) findViewById(R.id.et_lqfb_name);
        EditText et_url = (EditText) findViewById(R.id.et_lqfb_url);
        EditText et_web_url = (EditText) findViewById(R.id.et_lqfb_web_url);
        EditText et_key = (EditText) findViewById(R.id.et_lqfb_key);
        
        if (et_name.getText().length() == 0 &&
                et_url.getText().length() == 0 && et_key.getText().length() == 0) {
            return;
        }

        ContentValues values = new ContentValues();
        
        values.put(DBSystem.TableLQFBs.COLUMN_NAME, et_name.getText().toString().trim());
        values.put(DBSystem.TableLQFBs.COLUMN_WEB_URL, et_web_url.getText().toString().trim());
        values.put(DBSystem.TableLQFBs.COLUMN_URL, et_url.getText().toString().trim());
        values.put(DBSystem.TableLQFBs.COLUMN_API_KEY, et_key.getText().toString().trim());
        
        if (mLQFBUri == null) {
            getContentResolver().insert(CONTENT_URI, values);
        } else {
            getContentResolver().update(mLQFBUri, values, null, null);
        }
        
        Toast.makeText(this, getString(R.string.saved_changes), Toast.LENGTH_SHORT).show();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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
}
