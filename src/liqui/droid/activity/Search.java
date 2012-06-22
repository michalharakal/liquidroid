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

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import lfapi.v2.schema.Member;
import lfapi.v2.services.LiquidFeedbackService.MemberService;
import lfapi.v2.services.LiquidFeedbackServiceFactory;
import liqui.droid.Constants;
import liqui.droid.LQFBApplication;
import liqui.droid.util.LoadingDialog;
import liqui.droid.util.MemberImage;
import liqui.droid.util.RootAdapter;
import liqui.droid.util.StringUtils;
import liqui.droid.R;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * The Search activity.
 */
public class Search extends Base {

    protected LoadingDialog mLoadingDialog;

    protected MemberAdapter mMemberAdapter;

    protected ListView mListViewResults;

    protected boolean mLoading;

    protected boolean mReload;

    protected boolean mFirstTimeSearch;

    protected int mPage = 1;

    protected boolean mSearchByUser;// flag to search user or repo

    /*
     * (non-Javadoc)
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.act_search);
        setUpActionBar();

        mListViewResults = (ListView) findViewById(R.id.list_search);
        registerForContextMenu(mListViewResults);

        // final Spinner languageSpinner = (Spinner)
        // findViewById(R.id.spinner_language);
        final Spinner searchTypeSpinner = (Spinner) findViewById(R.id.spinner_search_type);
        final EditText etSearchKey = (EditText) findViewById(R.id.et_search);
        ImageButton btnSearch = (ImageButton) findViewById(R.id.btn_search);

        /** event when user press enter button at soft keyboard */
        etSearchKey.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    mFirstTimeSearch = true;
                    mPage = 1;// reset to 1;
                    String searchKey = etSearchKey.getText().toString();
                    // String selectedLanguage = (String)
                    // languageSpinner.getSelectedItem();
                    if (searchTypeSpinner.getSelectedItemPosition() == 1) {
                        mSearchByUser = true;
                        searchUser(searchKey);
                    } else {
                        mSearchByUser = false;
                        // searchRepository(searchKey, selectedLanguage);
                    }

                    hideKeyboard(etSearchKey.getWindowToken());

                    return true;
                }
                return false;
            }
        });

        /** Event when user press button image */
        btnSearch.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mFirstTimeSearch = true;
                mPage = 1;// reset to 1;
                String searchKey = etSearchKey.getText().toString();
                // String selectedLanguage = (String)
                // languageSpinner.getSelectedItem();
                if (searchTypeSpinner.getSelectedItemPosition() == 1) {
                    mSearchByUser = true;
                    searchUser(searchKey);
                } else {
                    mSearchByUser = false;
                    // searchRepository(searchKey, selectedLanguage);
                }

                hideKeyboard(etSearchKey.getWindowToken());
            }
        });
    }

    /**
     * Search user.
     * 
     * @param searchKey the search key
     */
    protected void searchUser(final String searchKey) {
        mListViewResults.setOnItemClickListener(new OnUserClickListener(this));
        mListViewResults.setOnScrollListener(null);// reset listener as the API
                                                   // doesn't have the
                                                   // pagination

        mMemberAdapter = new MemberAdapter(this, new ArrayList<Member>(), R.layout.row_gravatar_1, true);
        mListViewResults.setAdapter(mMemberAdapter);

        new LoadUserTask(this).execute(new String[] {
            searchKey
        });
    }

    /**
     * Gets the users.
     * 
     * @param searchKey the search key
     * @return the users
     */
    protected List<Member> getUsers(String searchKey) {
        LiquidFeedbackServiceFactory factory = LiquidFeedbackServiceFactory
                .newInstance(Constants.API_TEST);
        MemberService service = factory.createMemberService();
        List<Member> users = new ArrayList<Member>();
        if (!StringUtils.isBlank(searchKey)) {

            Member.Options mo = new Member.Options();
            mo.memberSearch = searchKey;

            users = service.getMember(mo, "html");
        } else {
            // show dialog
        }
        return users;
    }

    /**
     * Callback to be invoked when user in the AdapterView has been clicked.
     *
     * @see OnUserClickEvent
     */
    private static class OnUserClickListener implements OnItemClickListener {

        /** The target. */
        private WeakReference<Search> mTarget;

        /**
         * Instantiates a new on user click listener.
         * 
         * @param activity the activity
         */
        public OnUserClickListener(Search activity) {
            mTarget = new WeakReference<Search>(activity);
        }

        /*
         * (non-Javadoc)
         * @see
         * android.widget.AdapterView.OnItemClickListener#onItemClick(android
         * .widget.AdapterView, android.view.View, int, long)
         */
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            if (mTarget.get() != null) {
                Member user = (Member) adapterView.getAdapter().getItem(position);
                Intent intent = new Intent().setClass(mTarget.get(), MemberActivity.class);
                intent.putExtra(Constants.Member.LOGIN, (String) user.name);
                intent.putExtra(Constants.Member.NAME, (String) user.name);
                mTarget.get().startActivity(intent);
            }
        }
    }

    /**
     * Fill users data to UI.
     * 
     * @param users the users
     */
    protected void fillUsersData(List<Member> users) {
        if (users != null && users.size() > 0) {
            for (Member user : users) {
                mMemberAdapter.add(user);
            }
        }
        mMemberAdapter.notifyDataSetChanged();
    }

    /**
     * An asynchronous task that runs on a background thread to load user.
     */
    private static class LoadUserTask extends AsyncTask<String, Integer, List<Member>> {

        /** The target. */
        private WeakReference<Search> mTarget;

        /** The exception. */
        private boolean mException;

        /**
         * Instantiates a new load user task.
         * 
         * @param activity the activity
         */
        public LoadUserTask(Search activity) {
            mTarget = new WeakReference<Search>(activity);
        }

        /*
         * (non-Javadoc)
         * @see android.os.AsyncTask#doInBackground(Params[])
         */
        @Override
        protected List<Member> doInBackground(String... params) {
            if (mTarget.get() != null) {
                try {
                    return mTarget.get().getUsers(params[0]);
                } catch (Exception e) {
                    Log.e(Constants.LOG_TAG, e.getMessage(), e);
                    mException = true;
                    return null;
                }
            } else {
                return null;
            }
        }

        /*
         * (non-Javadoc)
         * @see android.os.AsyncTask#onPreExecute()
         */
        @Override
        protected void onPreExecute() {
            if (mTarget.get() != null) {
                mTarget.get().mLoadingDialog = LoadingDialog.show(mTarget.get(), true, true);
            }
        }

        /*
         * (non-Javadoc)
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        protected void onPostExecute(List<Member> result) {
            if (mTarget.get() != null) {
                Search activity = mTarget.get();
                if (mException) {
                    mTarget.get().showError(false);
                } else {
                    activity.fillUsersData(result);
                }

                if (activity.mLoadingDialog != null && activity.mLoadingDialog.isShowing()) {
                    activity.mLoadingDialog.dismiss();
                }

                activity.mFirstTimeSearch = false;
            }
        }
    }

    /*
     * (non-Javadoc)
     * @see android.app.Activity#onCreateContextMenu(android.view.ContextMenu,
     * android.view.View, android.view.ContextMenu.ContextMenuInfo)
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        menu.clear();// clear items

        if (v.getId() == R.id.list_search) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            menu.setHeaderTitle("Go to");

            /** Menu for user */
            if (mSearchByUser) {
                Member user = (Member) mMemberAdapter.getItem(info.position);
                menu.add("User " + user.name
                        + (!StringUtils.isBlank(user.name) ? " - " + user.name : ""));
            }

            /** Menu for repository */
            /*
             * else { Repository repository = (Repository)
             * repositoryAdapter.getItem(info.position); menu.add("User " +
             * repository.getOwner()); menu.add("Repo " + repository.getName());
             * }
             */
        }
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    /*
     * (non-Javadoc)
     * @see android.app.Activity#onContextItemSelected(android.view.MenuItem)
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
                .getMenuInfo();

        ListAdapter listAdapter = null;
        if (mSearchByUser) {
            listAdapter = mMemberAdapter;
        } else {
            // listAdapter = repositoryAdapter;
        }

        Object object = (Object) listAdapter.getItem(info.position);

        String title = item.getTitle().toString();

        /** User item */
        if (title.startsWith("User")) {
            Intent intent = new Intent().setClass(Search.this, MemberActivity.class);

            String username = null;
            if (mSearchByUser) {
                Member user = (Member) object;
                username = user.name;
            }

            intent.putExtra(Constants.Member.LOGIN, username);
            startActivity(intent);
        }
        /** Repo item */

        return true;
    }

    /**
     * The Member adapter.
     */
    public class MemberAdapter extends RootAdapter<Member> {

       MemberImage mImageDownloader;

        /** The row layout. */
        private int mRowLayout;

        /**
         * The show more data.
         *
         * @param context the context
         * @param objects the objects
         */
        // private boolean mShowMoreData;

        /**
         * Instantiates a new user adapter.
         * 
         * @param context the context
         * @param objects the objects
         */
        public MemberAdapter(Context context, List<Member> objects) {
            super(context, objects);
            mImageDownloader = new MemberImage(context, getAPIDB());
        }

        /**
         * Instantiates a new user adapter.
         * 
         * @param context the context
         * @param objects the objects
         * @param rowLayout the row layout
         * @param showMoreData the show more data
         */
        public MemberAdapter(Context context, List<Member> objects, int rowLayout, boolean showMoreData) {
            super(context, objects);
            mRowLayout = rowLayout;
            // mShowMoreData = showMoreData;
        }

        /* (non-Javadoc)
         * @see liqui.droid.adapter.RootAdapter#doGetView(int, android.view.View, android.view.ViewGroup)
         */
        @Override
        public View doGetView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            ViewHolder viewHolder = null;
            
            if (v == null) {
                LayoutInflater vi = (LayoutInflater) LayoutInflater.from(mContext);
                v = vi.inflate(mRowLayout, null);

                viewHolder = new ViewHolder();
                viewHolder.ivGravatar = (ImageView) v.findViewById(R.id.iv_gravatar);
                viewHolder.tvTitle = (TextView) v.findViewById(R.id.tv_title);
                // viewHolder.tvDesc = (TextView) v.findViewById(R.id.tv_desc);
                // viewHolder.tvExtra = (TextView) v.findViewById(R.id.tv_extra);
                v.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) v.getTag();
            }

            final Member user = mObjects.get(position);

            if (user != null) {

                if (viewHolder.ivGravatar != null) {
                    
                    mImageDownloader.download("http://dummy?" + user.id + ";avatar", viewHolder.ivGravatar, 80, 80);
                    
                    // if (user.eMail != null)
                    //    GravatarDownloader.getInstance().download(user.eMail, viewHolder.ivGravatar);

                    viewHolder.ivGravatar.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            /** Open user activity */
                            LQFBApplication context = (LQFBApplication) v.getContext()
                                    .getApplicationContext();
                            context.openUserInfoActivity(v.getContext(), user.name, user.name);
                        }
                    });
                }

                if (viewHolder.tvTitle != null) {
                    viewHolder.tvTitle.setText(StringUtils.formatName(user.name, user.name));
                }

                if (viewHolder.tvDesc != null) {
                    viewHolder.tvDesc.setText(StringUtils.formatName(user.name, user.name));
                }

                /*
                 * if (mShowMoreData && viewHolder.tvExtra != null) { Resources res
                 * = v.getResources(); String extraData =
                 * String.format(res.getString(R.string.user_extra_data), user
                 * .getFollowersCount(), user.getPublicRepoCount());
                 * viewHolder.tvExtra.setText(extraData); }
                 */
            }
            return v;
        }

        /**
         * The Class ViewHolder.
         */
        private class ViewHolder {

            public TextView tvTitle;

            public ImageView ivGravatar;

            public TextView tvDesc;
        }

    }
}
