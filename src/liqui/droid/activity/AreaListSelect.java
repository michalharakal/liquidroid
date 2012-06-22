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

import liqui.droid.db.DB;
import liqui.droid.holder.AreaUnitIssueHolder;
import liqui.droid.holder.BreadCrumbHolder;
import liqui.droid.util.RootAdapter;
import liqui.droid.util.StringUtils;
import liqui.droid.R;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton.OnCheckedChangeListener;

import java.util.ArrayList;
import java.util.List;

/**
 * The RepositoryList activity.
 */
public class AreaListSelect extends Base implements LoaderCallbacks<Cursor>, OnItemClickListener  {

    protected MembershipCursorAdapter mAdapter;
    
    protected Uri mContentUri;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.act_generic_list);
        setUpActionBar();

        createBreadcrumb("Area Membership", (BreadCrumbHolder[]) null);
        
        mContentUri = Uri.parse("content://liqui.droid.db/areas").buildUpon().appendQueryParameter("db", getAPIDB()).build();
        
        getSupportLoaderManager().initLoader(0, null, this);
        mAdapter = new MembershipCursorAdapter(this, null, true);
        
        ListView listView = (ListView) findViewById(R.id.list_view);
        listView.setOnItemClickListener(this);
        listView.setAdapter(mAdapter);
    }
    
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        // Cursor c = (Cursor) adapterView.getAdapter().getItem(position);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
        CursorLoader cursorLoader = new CursorLoader(getApplication(), mContentUri, null, null, null, "name");
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

    public class MembershipCursorAdapter extends CursorAdapter implements OnCheckedChangeListener {
        
        Context mContext;

        public MembershipCursorAdapter(Context context, Cursor c, boolean autoRequery) {
            super(context, c, autoRequery);
            this.mContext = context;
        }

        public MembershipCursorAdapter(Context context, Cursor c, int flags) {
            super(context, c, flags);
            this.mContext = context;
        }

        @Override
        public void bindView(View view, Context context, Cursor c) {
            Integer areaId  = c.getInt(c.getColumnIndex(DB.Area.COLUMN_ID));
            String areaName = c.getString(c.getColumnIndex(DB.Area.COLUMN_NAME));
            
            TextView tvSummary = (TextView)view.findViewById(R.id.tv_title);
            CheckBox cbSelected = (CheckBox)view.findViewById(R.id.cb_selected);
            
            Uri uri = Uri.parse("content://liqui.droid.db/memberships").buildUpon().appendQueryParameter("db", getAPIDB()).build();
            Cursor cursor = context.getContentResolver().query(uri, null, "member_id = ? AND area_id = ?",
                    new String[] { getMemberId(), String.valueOf(areaId) }, null);
            
            boolean isMember = false;
            
            // Log.d("XXXX", "cursor.getCount() " + cursor.getCount());
            
            if (cursor.getCount() == 1) {
                isMember = true;
            }
            
            cursor.close();

            tvSummary.setText(areaName);
            
            cbSelected.setOnCheckedChangeListener(null);
            cbSelected.setChecked(isMember);
            cbSelected.setOnCheckedChangeListener(this);
            cbSelected.setTag(areaId);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View v = inflater.inflate(R.layout.row_membership, viewGroup, false);
            bindView(v, context, cursor);
            return v;
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            Uri MEMBERSHIP_URI = dbUri("content://liqui.droid.db/memberships");
            if (isChecked) {
                ContentValues values = new ContentValues();
                values.put(DB.Membership.COLUMN_MEMBER_ID, getMemberId());
                values.put(DB.Membership.COLUMN_AREA_ID, String.valueOf(buttonView.getTag()));

                Uri uri = mContext.getContentResolver().insert(MEMBERSHIP_URI, values);
                
                Log.d("XXXX", "Joined area " + buttonView.getTag() + " " + uri);
                
            } else {
                Uri.Builder ub = MEMBERSHIP_URI.buildUpon();
                ub = ub.appendQueryParameter("member_id", getMemberId());
                ub = ub.appendQueryParameter("area_id", String.valueOf(buttonView.getTag()));
                int nr = getContentResolver().delete(ub.build(), null, null);
                
                Log.d("XXXX","Unjoined area " + buttonView.getTag() + " " + nr);
            }
        }
    }
    
    /**
     * The Area checkbox adapter.
     */
    public class AreaCheckboxAdapter extends RootAdapter<AreaUnitIssueHolder> {

        /** The row layout. */
        protected int mRowLayout;

        /**
         * Instantiates a new repository adapter.
         * 
         * @param context the context
         * @param objects the objects
         */
        public AreaCheckboxAdapter(Context context, List<AreaUnitIssueHolder> objects) {
            super(context, objects);
        }

        /**
         * Instantiates a new repository adapter.
         * 
         * @param context the context
         * @param arrayList the objects
         * @param rowLayout the row layout
         */
        public AreaCheckboxAdapter(Context context, ArrayList<AreaUnitIssueHolder> arrayList, int rowLayout) {
            super(context, arrayList);
            mRowLayout = rowLayout;
        }

        /* (non-Javadoc)
         * @see liqui.droid.adapter.RootAdapter#doGetView(int, android.view.View, android.view.ViewGroup)
         */
        @Override
        public View doGetView(int position, View convertView, ViewGroup parent) {
            View v = null;

            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater) LayoutInflater.from(mContext);
                v = vi.inflate(mRowLayout, null);

                final ViewHolder viewHolder = new ViewHolder();

                viewHolder.tvTitle = (TextView) v.findViewById(R.id.tv_title);
                viewHolder.tvDesc = (TextView) v.findViewById(R.id.tv_desc);
                // viewHolder.tvExtra = (TextView) v.findViewById(R.id.tv_extra);

                viewHolder.cbSelected = (CheckBox) v.findViewById(R.id.cb_selected);
                viewHolder.cbSelected
                        .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                // ah.selected = isChecked;
                                AreaUnitIssueHolder ah = (AreaUnitIssueHolder) viewHolder.cbSelected.getTag();
                                ah.selected = isChecked;
                            }
                        });
                viewHolder.cbSelected.setTag(mObjects.get(position));
                v.setTag(viewHolder);
            } else {
                v = convertView;
                ((ViewHolder) v.getTag()).cbSelected.setTag(mObjects.get(position));
            }

            ViewHolder holder = (ViewHolder) v.getTag();

            AreaUnitIssueHolder ah = mObjects.get(position);
            if (ah != null) {

                if (holder.tvTitle != null) {
                    holder.tvTitle.setText(ah.area.name + " " + ah.area.description);
                }

                if (holder.tvDesc != null) {
                    holder.tvDesc.setText(StringUtils.doTeaser("mc " + ah.area.directMemberCount
                            + " mw: " + ah.area.memberWeight));
                }

                if (holder.cbSelected != null) {
                    holder.cbSelected.setChecked(ah.selected);
                }

            }
            return v;
        }

        /**
         * The Class ViewHolder.
         */
        private class ViewHolder {

            /** The tv title. */
            public TextView tvTitle;

            /** The tv desc. */
            public TextView tvDesc;

            /** The tv extra. */
            // public TextView tvExtra;

            public CheckBox cbSelected;
        }

    }

}