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
import liqui.droid.holder.BreadCrumbHolder;
import liqui.droid.R;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * The Class LQFBListCachedActivity.
 */
public class LQFBListCached extends Base
    implements LoaderCallbacks<Cursor>, OnItemClickListener {

    public static final Uri CONTENT_URI = Uri.parse("content://liqui.droid.system/lqfbs");
    
    protected static final int ACTIVITY_CREATE = 2342;
    
    protected static final int ACTIVITY_EDIT = 2343;
    
    protected static final int DELETE_ID = Menu.FIRST + 1;
    
    protected static final int DUPLICATE_ID = Menu.FIRST + 2;
    
    protected LQFBCursorAdapter mAdapter;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.act_lqfb_list);
        setUpActionBar();

        createBreadcrumb(getString(R.string.lqfb_edit_instances), (BreadCrumbHolder[]) null);
        
        getSupportLoaderManager().initLoader(0, null, this);
        mAdapter = new LQFBCursorAdapter(this, null, true);
        
        ListView listView = (ListView) findViewById(R.id.list_view);
        listView.setOnItemClickListener(this);
        listView.setAdapter(mAdapter);
        registerForContextMenu(listView);
    }
    
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        
        Cursor c = (Cursor) adapterView.getAdapter().getItem(position);
        
        Intent intent = new Intent().setClass(LQFBListCached.this, LQFBEdit.class);
        
        Uri uri = Uri.parse(DBSystemProvider.LQFBS_CONTENT_URI + "/" + c.getInt(c.getColumnIndex(DBSystem.TableLQFBs.COLUMN_ID)));
        intent.putExtra(DBSystemProvider.LQFBS_CONTENT_ITEM_TYPE, uri);
        
        startActivityForResult(intent, ACTIVITY_EDIT);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.insert:
            createLQFB();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    public void createLQFB() {
        Intent i = new Intent(this, LQFBEdit.class);
        startActivityForResult(i, ACTIVITY_CREATE);
    }
    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
            ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, DELETE_ID, 0, getString(R.string.delete));
        menu.add(0, DUPLICATE_ID, 1, getString(R.string.duplicate));
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case DELETE_ID:
                AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
                        .getMenuInfo();
                getContentResolver().delete(Uri.parse(DBSystemProvider.LQFBS_CONTENT_URI + "/" + info.id), null, null);
                return true;
            case DUPLICATE_ID:
                AdapterContextMenuInfo info2 = (AdapterContextMenuInfo) item
                        .getMenuInfo();
                Cursor c = getContentResolver().query(Uri.parse(DBSystemProvider.LQFBS_CONTENT_URI + "/" + info2.id), null, null, null, null);
                c.moveToFirst();
                ContentValues values = new ContentValues();
                values.put(DBSystem.TableLQFBs.COLUMN_NAME,    c.getString(c.getColumnIndex(DBSystem.TableLQFBs.COLUMN_NAME)));
                values.put(DBSystem.TableLQFBs.COLUMN_URL,     c.getString(c.getColumnIndex(DBSystem.TableLQFBs.COLUMN_URL)));
                values.put(DBSystem.TableLQFBs.COLUMN_WEB_URL,     c.getString(c.getColumnIndex(DBSystem.TableLQFBs.COLUMN_WEB_URL)));
                values.put(DBSystem.TableLQFBs.COLUMN_API_KEY, c.getString(c.getColumnIndex(DBSystem.TableLQFBs.COLUMN_API_KEY)));
                c.close();
                getContentResolver().insert(DBSystemProvider.LQFBS_CONTENT_URI, values);
                return true;
        }
        return super.onContextItemSelected(item);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode,
            Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
    }
    
    @Override
    public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
        CursorLoader cursorLoader = new CursorLoader(getApplication(), CONTENT_URI, null, null, null, null);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cl, Cursor cursor) {
        mAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursor) {
        mAdapter.swapCursor(null);
    }
    
    /**
     * The LQFB cursor adapter.
     */
    public class LQFBCursorAdapter extends CursorAdapter {

        public LQFBCursorAdapter(Context context, Cursor c, boolean autoRequery) {
            super(context, c, autoRequery);
        }

        public LQFBCursorAdapter(Context context, Cursor c, int flags) {
            super(context, c, flags);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            
            String name     = cursor.getString(cursor.getColumnIndex(DBSystem.TableLQFBs.COLUMN_NAME));
            String web_url  = cursor.getString(cursor.getColumnIndex(DBSystem.TableLQFBs.COLUMN_WEB_URL));
            String url      = cursor.getString(cursor.getColumnIndex(DBSystem.TableLQFBs.COLUMN_URL));
            String api_key  = cursor.getString(cursor.getColumnIndex(DBSystem.TableLQFBs.COLUMN_API_KEY));
            
            TextView tv_summary = (TextView)view.findViewById(R.id.tv_title);
            tv_summary.setText(name);

            TextView tv_desc = (TextView)view.findViewById(R.id.tv_desc);
            tv_desc.setText(web_url);

            TextView tv_desc2 = (TextView)view.findViewById(R.id.tv_desc2);
            tv_desc2.setText(url);

            TextView tv_extra = (TextView)view.findViewById(R.id.tv_extra);
            tv_extra.setText(api_key);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View v = inflater.inflate(R.layout.row_simple_4, viewGroup, false);
            bindView(v, context, cursor);
            return v;
        }

    }
}
