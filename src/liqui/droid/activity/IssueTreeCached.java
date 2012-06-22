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

import lfapi.v2.schema.Interval;
import lfapi.v2.services.LiquidFeedbackService.AreaUnitService;
import lfapi.v2.services.LiquidFeedbackServiceFactory;
import lfapi.v2.services.auth.SessionKeyAuthentication;
import liqui.droid.Constants;
import liqui.droid.db.DB;
import liqui.droid.db.DBProvider;
import liqui.droid.holder.BreadCrumbHolder;
import liqui.droid.util.BarGraphView;
import liqui.droid.util.LoadingDialog;
import liqui.droid.R;

import android.content.AsyncQueryHandler;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CursorTreeAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.SimpleCursorTreeAdapter;

import org.joda.time.DateTime;
import org.ocpsoft.pretty.time.PrettyTime;

import java.lang.ref.WeakReference;
import java.util.Date;
import java.util.Locale;

/**
 * The Class IssueTreeCachedActivity.
 */
public class IssueTreeCached extends Base implements OnClickListener, OnChildClickListener {

    protected LoadingDialog mLoadingDialog;

    protected CursorTreeAdapter mAadapter;
    
    protected QueryHandler mQueryHandler;
    
    protected Uri mContentIssues;
    
    protected Uri mContentInitiatives;
    
    protected Button b0, b1, b2, b3, b4;
    
    protected String mTab = null;
    
    protected String mAreaId = null; 
    
    protected String mUnitName = null;
    
    protected String mState = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.act_issue_tree);
        
        b0 = (Button) findViewById(R.id.btn_0);
        b1 = (Button) findViewById(R.id.btn_1);
        b2 = (Button) findViewById(R.id.btn_2);
        b3 = (Button) findViewById(R.id.btn_3);
        b4 = (Button) findViewById(R.id.btn_4);

        b0.setTypeface(null, 1);

        ExpandableListView listView = (ExpandableListView) findViewById(R.id.list_view);
        listView.setOnChildClickListener(this);
        
        mAadapter = new IssueTreeCursorAdapter(this);
        listView.setAdapter(mAadapter);
        
        mQueryHandler = new QueryHandler(this, mAadapter);
        
        if (getIntent().getExtras() != null) {
            Bundle bundle = getIntent().getExtras().getBundle(Constants.DATA_BUNDLE);
            mAreaId = bundle.getString("AREA_ID");
            mUnitName = bundle.getString("UNIT_NAME");
            mTab = bundle.getString("tab");
        }
        
        setUpActionBar();
        setBreadCrumbs();
        
        mContentIssues      = dbUri("content://liqui.droid.db/issues");
        mContentInitiatives = dbUri("content://liqui.droid.db/initiatives/by_issue");
        
        bottomButtonsListener();

        buildQuery();
    }
    
    protected void buildQuery() {
        if (mTab != null) {
            if (DB.Issue.STATE_META_OPEN.equals(mTab)) {
                bottomButtonsOpen();
            } else if (DB.Issue.STATE_META_CLOSED.equals(mTab)) {
                bottomButtonsClosed();
            } else if ("area".equals(mTab)) {
                bottomButtonsArea();
            }
        } else {
            bottomButtonsHide();
        }
            
        String[] projection = new String[] {
                " issue._id                   AS _id                         ",
                " issue.population            AS issue_population            ",
                " area.name                   AS area_name                   ",
                
                " issue.policy_id             AS policy_id                   ",
                
                " issue.created               AS issue_created               ",
                " issue.accepted              AS issue_accepted              ",
                " issue.half_frozen           AS issue_half_frozen           ",
                " issue.fully_frozen          AS issue_fully_frozen          ",
                " issue.closed                AS issue_closed                ",
                " issue.cleaned               AS issue_cleaned               ",
                " issue.admission_time        AS issue_admission_time        ",
                " issue.discussion_time       AS issue_discussion_time       ",
                " issue.voting_time           AS issue_voting_time           ",
                " issue.snapshot              AS issue_snapshot              ",
                " issue.latest_snapshot_event AS issue_latest_snapshot_event ",
                    
                " issue.state                 AS issue_state                 ",
                
                " policy.name                 AS policy_name                 "
        };
            
        String   selection  = "area._id = issue.area_id AND issue.policy_id = policy._id";
        String[] selectionArgs = null;
            
        if (DB.Issue.STATE_META_OPEN.equals(mTab)) {
            selection = "area._id = issue.area_id AND issue.policy_id = policy._id AND " +
                    "(issue_state = 'admission' OR          " +
                    " issue_state = 'discussion' OR         " +
                    " issue_state = 'verification' OR       " +
                    " issue_state = 'voting')               ";      
        } else if (DB.Issue.STATE_META_CLOSED.equals(mTab)) {
            selection = "area._id = issue.area_id AND issue.policy_id = policy._id AND " +
                    "(issue_state = 'finished_without_winner' OR                       " +
                    " issue_state = 'finished_with_winner' OR                          " +
                    " issue_state = 'canceled_no_initiative_admitted' OR               " +
                    " issue_state = 'canceled_after_revocation_during_verification' OR " +
                    " issue_state = 'canceled_after_revocation_during_discussion' OR   " +
                    " issue_state = 'canceled_issue_not_accepted' OR                   " +
                    " issue_state = 'canceled_revoked_before_accepted')                ";
        }
        if (mAreaId != null) {
            selection = "area._id = issue.area_id AND issue.policy_id = policy._id AND issue.area_id = ?";
            selectionArgs = new String[] { mAreaId.toString() };
        }
        
        if (mState.length() > 0) {
            if (DB.Issue.STATE_CANCELLED.equals(mState)) {
                selection += " AND (issue_state = 'canceled_no_initiative_admitted' OR     " +
                        " issue_state = 'canceled_after_revocation_during_verification' OR " +
                        " issue_state = 'canceled_after_revocation_during_discussion' OR   " +
                        " issue_state = 'canceled_issue_not_accepted' OR                   " +
                        " issue_state = 'canceled_revoked_before_accepted') ";
            } else if (DB.Issue.STATE_FINISHED.equals(mState)) {
                selection += " AND (issue_state = 'finished_without_winner' OR              " +
                        " issue_state = 'finished_with_winner')                            ";
            } else {
                selection += " AND issue_state = '" + mState + "'";
            }
        }
        
        String   sortOrder = "_id DESC";
            
        mQueryHandler.startQuery(TOKEN_GROUP, null, mContentIssues, projection, selection, selectionArgs, sortOrder);
    }
    
    /**
     * Sets the bread crumbs.
     */
    protected void setBreadCrumbs() {
        BreadCrumbHolder[] breadCrumbHolders = new BreadCrumbHolder[1];

        BreadCrumbHolder b = new BreadCrumbHolder();
        b.setLabel(getString(R.string.title_explore));
        b.setTag(Constants.EXPLORE);
        breadCrumbHolders[0] = b;
        
        String issues = "";
        
        issues += ("open".equals(mTab) ? getString(R.string.issues_open) + " " : "");
        issues += ("closed".equals(mTab) ? getString(R.string.issues_closed) + " " : "");
               
        issues += getString(R.string.issues) + (mUnitName != null ? " - " + mUnitName : "");
        
        if (mAreaId != null) {
            String areaName = queryString(dbUri(DBProvider.AREA_CONTENT_URI),
                    DB.Area.COLUMN_NAME, DB.Area.COLUMN_ID + " = ?", new String[] { mAreaId }, null);

            if (areaName != null) {
                issues += ": " + areaName;
            }
        }
        
        createBreadcrumb(issues, breadCrumbHolders);
        
        updateIVMembership();
        
    }
    
    public void updateIVMembership() {
        ImageView iv = (ImageView) findViewById(R.id.iv_breadcrumb_right);   

        if (mAreaId != null && getMemberId() != null) {
            if (!isResultEmpty(dbUri(DBProvider.MEMBERSHIP_CONTENT_URI),
                    "area_id = ? AND member_id = ?",
                    new String[] { mAreaId, getMemberId() }, "area_id LIMIT 1")) {
                iv.setVisibility(View.VISIBLE);
            } else {
                iv.setVisibility(View.INVISIBLE);
            }
        } else {
            iv.setVisibility(View.INVISIBLE);
        }
    }
    
    public void bottomButtonsListener() {
        b0.setOnClickListener(this);
        b1.setOnClickListener(this);
        b2.setOnClickListener(this);
        b3.setOnClickListener(this);
        b4.setOnClickListener(this);
    }

    public void bottomButtonsOpen() {
        b0.setText(R.string.issue_state_any_phase);
        b1.setText(R.string.issue_state_new);
        b2.setText(R.string.issue_state_discussion);
        b3.setText(R.string.issue_state_frozen);
        b4.setText(R.string.issue_state_voting);
    }
    
    public void bottomButtonsClosed() {
        b0.setText(R.string.issue_state_any_state);
        b1.setText(R.string.issue_state_finished);
        b2.setText(R.string.issue_state_with_winner);
        b3.setText(R.string.issue_state_without_winner);
        b4.setText(R.string.issue_state_cancelled);
    }
    
    public void bottomButtonsArea() {
        b0.setText(R.string.area_events);
        b1.setText(R.string.area_issues_open);
        b2.setText(R.string.area_issues_closed);
        b3.setText(R.string.area_participants);
        b4.setText(R.string.area_delegations);
    }
    
    public void bottomButtonsHide() {
        b0.setVisibility(View.GONE);
        b1.setVisibility(View.GONE);
        b2.setVisibility(View.GONE);
        b3.setVisibility(View.GONE);
        b4.setVisibility(View.GONE);
    }
    
    private static final int TOKEN_GROUP = 0;
    private static final int TOKEN_CHILD = 1;
    
    private static final class QueryHandler extends AsyncQueryHandler {
        private CursorTreeAdapter mAdapter;

        public QueryHandler(Context context, CursorTreeAdapter adapter) {
            super(context.getContentResolver());
            this.mAdapter = adapter;
        }

        @Override
        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
            switch (token) {
            case TOKEN_GROUP:
                if (mAdapter != null && cursor != null)
                mAdapter.setGroupCursor(cursor);
                break;

            case TOKEN_CHILD:
                int groupPosition = (Integer) cookie;
                try {
                    mAdapter.setChildrenCursor(groupPosition, cursor);
                } catch (NullPointerException e) {
                    Log.w("DEBUG","Adapter expired, try again on the next query: " + e.getMessage());
                }
                break;
            }
        }
    }
    
    public class IssueTreeCursorAdapter extends SimpleCursorTreeAdapter {

        public IssueTreeCursorAdapter(Context context) {

            super(context, null,
                    R.layout.row_issue,
                    new String[] { "_id", "population" },
                    new int[] { R.id.tv_title, R.id.tv_desc },
                    R.layout.row_initiative,
                    new String[] { "initiative_name"},
                    new int[] { R.id.tv_title });
        }

        @Override
        protected void bindGroupView(View view, Context context, Cursor cursor, boolean isExpanded) {
            // super.bindGroupView(view, context, cursor, isExpanded);
            
            String issue_id         = cursor.getString(cursor.getColumnIndex("_id"));
            // String issue_population = cursor.getString(cursor.getColumnIndex("issue_population"));
            // String area_name        = cursor.getString(cursor.getColumnIndex("area_name"));
            String issue_state      = cursor.getString(cursor.getColumnIndex("issue_state"));
            Integer policy_id       = cursor.getInt(cursor.getColumnIndex("policy_id"));
            
            // Long created         = cursor.getLong(cursor.getColumnIndex("issue_created"));
            Long accepted        = cursor.getLong(cursor.getColumnIndex("issue_accepted"));
            Long half_frozen     = cursor.getLong(cursor.getColumnIndex("issue_half_frozen"));
            Long fully_frozen    = cursor.getLong(cursor.getColumnIndex("issue_fully_frozen"));
            Long closed          = cursor.getLong(cursor.getColumnIndex("issue_closed"));
            // Long cleaned         = cursor.getLong(cursor.getColumnIndex("issue_cleaned"));
            Long admission_time  = cursor.getLong(cursor.getColumnIndex("issue_admission_time"));
            // Long discussion_time = cursor.getLong(cursor.getColumnIndex("issue_discussion_time"));
            // Long voting_time     = cursor.getLong(cursor.getColumnIndex("issue_voting_time"));
            // Long snapshot        = cursor.getLong(cursor.getColumnIndex("issue_snapshot"));
            // Long latest_snapshot_event = cursor.getLong(cursor.getColumnIndex("issue_latest_snapshot_event"));

            String policy_name     = cursor.getString(cursor.getColumnIndex("policy_name"));
            
            /*
            Log.d("XXXXXXXX", "issue id " + issue_id);
            Log.d("XXXXXXXXX", "issue_state " + issue_state);
            Log.d("XXXXXXXXX created ", issue_id + " " + created);
            Log.d("XXXXXXXXX accepted ", issue_id + " " + accepted);
            Log.d("XXXXXXXXX half_frozen ", issue_id + " " + half_frozen);
            Log.d("XXXXXXXXX fully_frozen ", issue_id + " " + fully_frozen);
            Log.d("XXXXXXXXX closed ", issue_id + " " + closed);
            Log.d("XXXXXXXXX cleaned ", issue_id + " " + cleaned);
            Log.d("XXXXXXXXX admission_time ", issue_id + " " + admission_time);
            Log.d("XXXXXXXXX discussion_time ", issue_id + " " + discussion_time);
            Log.d("XXXXXXXXX voting_time ", issue_id + " " + voting_time);
            Log.d("XXXXXXXXX snapshot ", issue_id + " " + snapshot);
            Log.d("XXXXXXXXX latest_snapshot_event ", issue_id + " " + latest_snapshot_event);
            */
            
            TextView summary = (TextView)view.findViewById(R.id.tv_title);
            summary.setText("#" + issue_id + " · " + policy_name);
            
            ImageView ivState = (ImageView)view.findViewById(R.id.iv_state);
            
            // state: open
            
            String state = "";
            
            PrettyTime pt = new PrettyTime(Locale.getDefault());
            
            // Log.d("XXXX", "policyId " + policy_id);
            
            Uri policyUri = DBProvider.POLICY_CONTENT_URI.buildUpon().appendQueryParameter("db", getAPIDB()).build();

            Cursor c = getContentResolver().query(policyUri, null, "_id = ?",
                    new String[] { String.valueOf(policy_id) }, null);
            c.moveToFirst();
            
            String policy_admission_time = c.getString(c.getColumnIndex("admission_time"));
            String policy_discussion_time = c.getString(c.getColumnIndex("discussion_time"));
            String policy_verification_time = c.getString(c.getColumnIndex("verification_time"));
            String policy_voting_time = c.getString(c.getColumnIndex("voting_time"));
            
            c.close();
            
            if (DB.Issue.STATE_ADMISSION.equals(issue_state)) {
                state += getString(R.string.issue_state_new);
                ivState.setImageResource(R.drawable.ic_script);
                state += " " + pt.format(new DateTime(admission_time).plus(
                        new Interval(policy_admission_time).getPeriod()).toDate());
            } else if (DB.Issue.STATE_DISCUSSION.equals(issue_state)) {
                ivState.setImageResource(R.drawable.ic_script);
                state += getString(R.string.issue_state_discussion);
                state += " " + pt.format(new DateTime(accepted).plus(
                        new Interval(policy_discussion_time).getPeriod()).toDate());
            } else if (DB.Issue.STATE_VERIFICATION.equals(issue_state)) {
                ivState.setImageResource(R.drawable.ic_script);
                state += getString(R.string.issue_state_frozen);
                state += " " + pt.format(new DateTime(half_frozen).plus(
                        new Interval(policy_verification_time).getPeriod()).toDate());
            } else if (DB.Issue.STATE_VOTING.equals(issue_state)) {
                ivState.setImageResource(R.drawable.ic_script);
                state += getString(R.string.issue_state_voting);
                state += " " + pt.format(new DateTime(fully_frozen).plus(
                        new Interval(policy_voting_time).getPeriod()).toDate());
                
                // state += " voting_time = " + voting_time;
            }
            
            // state: closed
            
             else if (DB.Issue.STATE_WITHOUT_WINNER.equals(issue_state)) {
                state += getString(R.string.issue_state_without_winner);
                state += " " + pt.format(new Date((long)closed));
                ivState.setImageResource(R.drawable.ic_award_star_silver_2);
            } else if (DB.Issue.STATE_WITH_WINNER.equals(issue_state)) {
                state += getString(R.string.issue_state_with_winner);
                ivState.setImageResource(R.drawable.ic_award_star_gold_2);
                state += " " + pt.format(new Date((long)closed));
            } else if ("canceled_no_initiative_admitted".equals(issue_state)) {
                state += getString(R.string.issue_state_cancelled);
                ivState.setImageResource(R.drawable.ic_cancel);
                state += " " + pt.format(new Date((long)closed));
            } else if ("canceled_after_revocation_during_verification".equals(issue_state)) {
                state += getString(R.string.issue_state_cancelled);
                state += " " + pt.format(new Date((long)closed));
                ivState.setImageResource(R.drawable.ic_cancel);
            } else if ("canceled_after_revocation_during_discussion".equals(issue_state)) {
                state += getString(R.string.issue_state_cancelled);
                state += " " + pt.format(new Date((long)closed));
                ivState.setImageResource(R.drawable.ic_cancel);
            } else if ("canceled_issue_not_accepted".equals(issue_state)) {
                state += getString(R.string.issue_state_cancelled);
                state += " " + pt.format(new Date((long)closed));
                ivState.setImageResource(R.drawable.ic_cancel);
            } else if ("canceled_revoked_before_accepted".equals(issue_state)) {
                state += " " + pt.format(new Date((long)closed));
                state += getString(R.string.issue_state_cancelled);
                ivState.setImageResource(R.drawable.ic_cancel);
            } else {
                state += "UNKNOWN: " + issue_state;
            }
            
            TextView desc = (TextView)view.findViewById(R.id.tv_desc);
            // desc.setVisibility(View.GONE);
            desc.setText(state);
        }
        

        @Override
        protected void bindChildView(View view, Context context, Cursor cursor, boolean isLastChild) {
            // super.bindChildView(view, context, cursor, isLastChild);
            
            String initiative_name = cursor.getString(cursor.getColumnIndex("initiative_name"));
            Integer initiative_id   = cursor.getInt(cursor.getColumnIndex("initiative_id"));
            // Integer initiative_winner = cursor.getInt(cursor.getColumnIndex("initiative_winner"));
            
            Integer ini_negative_votes = cursor.getInt(cursor.getColumnIndex("negative_votes"));
            Integer ini_positive_votes = cursor.getInt(cursor.getColumnIndex("positive_votes"));
            
            String ini_rank           = cursor.getString(cursor.getColumnIndex("initiative_rank"));
            String ini_eligible       = cursor.getString(cursor.getColumnIndex("initiative_eligible"));

            // Long    initiative_revoked = cursor.getLong(cursor.getColumnIndex("initiative_revoked"));
            String initiative_admitted = cursor.getString(cursor.getColumnIndex("initiative_admitted"));
            
            String issue_closed    = cursor.getString(cursor.getColumnIndex("issue_closed"));
            String issue_accepted  = cursor.getString(cursor.getColumnIndex("issue_accepted"));
            String issue_fully_frozen = cursor.getString(cursor.getColumnIndex("issue_fully_frozen"));
            String ranks_available = cursor.getString(cursor.getColumnIndex("ranks_available"));
            Float issue_voter_count  = cursor.getFloat(cursor.getColumnIndex("voter_count"));
            Float issue_population   = cursor.getFloat(cursor.getColumnIndex("population"));
            
            BarGraphView bgCount = (BarGraphView) view.findViewById(R.id.bg_count);
            ImageView ivInitiative  = (ImageView) view.findViewById(R.id.iv_initiative);
            TextView tvInitiativeName = (TextView) view.findViewById(R.id.tv_initiative_name);
            
            tvInitiativeName.setText("i" + initiative_id + ": " + initiative_name);
            
            // Log.d("XXXX", issue_accepted + " " + issue_closed + " " + ranks_available + " " + initiative_admitted);
            
            if (issue_accepted != null && !issue_accepted.equals("false") && issue_closed != null && !issue_closed.equals("false") &&
                    ranks_available != null && ranks_available.equals("true") &&
                    initiative_admitted != null && initiative_admitted.equals("true")) {
                
                // Log.d("XXXX", ini_eligible + " " + ini_rank);
                
                // ranks
                if (ini_eligible != null && ini_eligible.equals("true") && ini_rank != null && ini_rank.equals("1")) {
                    ivInitiative.setImageResource(R.drawable.ic_award_star_gold_2);
                } else if (ini_eligible != null && ini_eligible.equals("true") && ini_rank != null) {
                    ivInitiative.setImageResource(R.drawable.ic_award_star_silver_2);
                } else {
                    ivInitiative.setImageResource(R.drawable.ic_cross);
                }
                
            } else if (issue_closed == null) {
                ivInitiative.setImageResource(R.drawable.ic_script);
            } else {
                ivInitiative.setImageResource(R.drawable.ic_cross);
            }

            view.setTag(initiative_id);
            
            // Log.d("XXXX", issue_closed + " " + issue_fully_frozen + " " + ini_negative_votes + " " + ini_positive_votes + " " + ranks_available);

            if (issue_closed != null && issue_fully_frozen != null) {
                if (ranks_available != null && ranks_available.equals("true")) {
                    // if (ini_negative_votes != null && ini_positive_votes != null) {
                        double max = issue_voter_count;
                        bgCount.setValues(ini_positive_votes.intValue(),
                                0,
                                (int)(max - ini_negative_votes - ini_positive_votes),
                                ini_negative_votes.intValue(), (int)max);
                        bgCount.setQuorum(0.0f);
                        
                    // } else {
                    //    bgCount.setVisibility(View.INVISIBLE);
                    // }
                } else {
                    bgCount.setVisibility(View.INVISIBLE); // backend is counting votes
                }
            } else if (issue_closed == null) {
                double max = issue_population;
                double quorum;
                
                if (issue_accepted != null && issue_accepted.equals("1")) {
                    Float initiative_quorum_num = cursor.getFloat(cursor.getColumnIndex("initiative_quorum_num"));
                    Float initiative_quorum_den = cursor.getFloat(cursor.getColumnIndex("initiative_quorum_den"));
                    quorum = initiative_quorum_num / initiative_quorum_den;
                } else {
                    Float issue_quorum_num = cursor.getFloat(cursor.getColumnIndex("issue_quorum_num"));
                    Float issue_quroum_den = cursor.getFloat(cursor.getColumnIndex("issue_quorum_den"));
                    quorum = issue_quorum_num / issue_quroum_den;
                }
                
                Float initiative_satisfied_supporter_count = cursor.getFloat(cursor.getColumnIndex("satisfied_supporter_count"));
                Float initiative_supporter_count = cursor.getFloat(cursor.getColumnIndex("supporter_count"));
                
                // Log.d("XXXX", "Quorum " + max * quorum);
                
                bgCount.setQuorum(max * quorum);
                bgCount.setValues(
                        initiative_satisfied_supporter_count.floatValue(),
                        initiative_supporter_count - initiative_satisfied_supporter_count,
                        max - initiative_supporter_count,
                        0f, max);
            } else {
                bgCount.setVisibility(View.GONE);
            }
            
            ImageView ivSupport = (ImageView) view.findViewById(R.id.iv_support);
            
            ivSupport.setVisibility(View.INVISIBLE); // FIXME
        }

        @Override
        public Cursor getChildrenCursor(Cursor groupCursor) {
            
            Uri.Builder builder = mContentInitiatives.buildUpon().appendPath(String.valueOf(groupCursor.getLong(0)));
            mQueryHandler.startQuery(TOKEN_CHILD, groupCursor.getPosition(), builder.build(), null, null, null, null);
            
            return null;
        }

        @Override
        public void onGroupCollapsed(int groupPosition) {
            super.onGroupCollapsed(groupPosition);
            
        }
        
        
    }

    @Override
    public void onClick(View v) {
        
        b0.setTypeface(null, 0);
        b1.setTypeface(null, 0);
        b2.setTypeface(null, 0);
        b3.setTypeface(null, 0);
        b4.setTypeface(null, 0);
        
        if ("open".equals(mTab)) {
            if (v == b0) { // any phase
                mState = DB.Issue.STATE_ANY_PHASE;
                b0.setTypeface(null, 1);
            } else if (v == b1) { // new
                mState = DB.Issue.STATE_ADMISSION;
                b1.setTypeface(null, 1);
            } else if (v == b2) { // discussion
                mState = DB.Issue.STATE_DISCUSSION;
                b2.setTypeface(null, 1);
            } else if (v == b3) { // frozen
                mState = DB.Issue.STATE_VERIFICATION;
                b3.setTypeface(null, 1);
            } else if (v == b4) { // voting
                mState = DB.Issue.STATE_VOTING;
                b4.setTypeface(null, 1);
            }
        } else if ("closed".equals(mTab)) {
            if (v == b0) { // any state
                mState = DB.Issue.STATE_ANY_STATE;
                b0.setTypeface(null, 1);
            } else if (v == b1) { // finished
                mState = DB.Issue.STATE_FINISHED;
                b1.setTypeface(null, 1);
            } else if (v == b2) { // with winner
                mState = DB.Issue.STATE_WITH_WINNER;
                b2.setTypeface(null, 1);
            } else if (v == b3) { // without winner
                mState = DB.Issue.STATE_WITHOUT_WINNER;
                b3.setTypeface(null, 1);
            } else if (v == b4) { // cancelled
                mState = DB.Issue.STATE_CANCELLED;
                b4.setTypeface(null, 1);
            }
        } else if ("area".equals(mTab)) {
            if (v == b0) { // events
                mState = "events";
                b0.setTypeface(null, 1);
            } else if (v == b1) { // open
                mState = DB.Issue.STATE_META_OPEN;
                b1.setTypeface(null, 1);
            } else if (v == b2) { // closed
                mState = DB.Issue.STATE_META_CLOSED;
                b2.setTypeface(null, 1);
            } else if (v == b3) { // participants
                mState = "participants";
                b3.setTypeface(null, 1);
            } else if (v == b4) { // delegations
                mState = "delegations";
                b4.setTypeface(null, 1);
            }
        }
        
        buildQuery();
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition,
            int childPosition, long id) {
        Intent intent = new Intent().setClass(IssueTreeCached.this, Initiative.class);

        Bundle extras = new Bundle();
        extras.putString(Constants.Account.API_NAME,    getAPIName());
        extras.putString(Constants.Account.API_URL,     getAPIUrl());
        extras.putString(Constants.Account.MEMBER_ID,   getMemberId());
        extras.putString(Constants.Account.SESSION_KEY, getSessionKey());
        intent.putExtras(extras);

        Bundle data = new Bundle();
        data.putInt("_id",  (Integer)v.getTag());
        intent.putExtra(Constants.DATA_BUNDLE, data);

        startActivity(intent);
        
        return false;
    }  
    
    /* (non-Javadoc)
     * @see android.app.Activity#onPrepareOptionsMenu(android.view.Menu)
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        MenuInflater inflater = getMenuInflater();
        if (isAuthenticated()) {
            
            if (mAreaId != null) {
                if (!isResultEmpty(dbUri(DBProvider.MEMBERSHIP_CONTENT_URI),
                        "area_id = ? AND member_id = ?",
                        new String[] { mAreaId, getMemberId() }, "area_id LIMIT 1")) {
                    inflater.inflate(R.menu.area_withdraw, menu);
                } else {
                    inflater.inflate(R.menu.area_participate, menu);
                }

                inflater.inflate(R.menu.area_delegation_change, menu);
                inflater.inflate(R.menu.issue_create, menu);
            }
        }
        return true;
    }

    /* (non-Javadoc)
     * @see liqui.droid.BaseActivity#setMenuOptionItemSelected(android.view.MenuItem)
     */
    @Override
    public boolean setMenuOptionItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_area_participate:
                new AreaParticipateTask(this).execute(true);
                return true;
            case R.id.menu_area_withdraw:
                new AreaParticipateTask(this).execute(false);
                return true;
            default:
                return true;
        }
    }

    private class AreaParticipateTask extends AsyncTask<Boolean, Void, Boolean> {

        /** The target. */
        private WeakReference<IssueTreeCached> mTarget;
        
        /** The exception. */
        private Exception mException = null;
        
        private boolean mIsParticipateAction;

        /**
         * Instantiates a new load area participate task.
         *
         * @param activity the activity
         */
        public AreaParticipateTask(IssueTreeCached activity) {
            mTarget = new WeakReference<IssueTreeCached>(activity);
        }

        /*
         * (non-Javadoc)
         * @see android.os.AsyncTask#doInBackground(Params[])
         */
        @Override
        protected Boolean doInBackground(Boolean... arg0) {
            if (mTarget.get() != null) {
                mIsParticipateAction = arg0[0];
                try {
                    LiquidFeedbackServiceFactory f = LiquidFeedbackServiceFactory.newInstance(getAPIUrl());
                    AreaUnitService ms = f.createAreaUnitService();
                    
                    lfapi.v2.services.auth.Authentication auth = new SessionKeyAuthentication(getSessionKey());
                    ms.setAuthentication(auth);
                    
                    Integer areaId = Integer.parseInt(mAreaId);
                    
                    if (mIsParticipateAction) {
                        ms.postMembership(areaId, null);
                    } else {
                        ms.postMembership(areaId, Boolean.TRUE);
                    }
                    return true;
                } catch (Exception e) {
                    Log.e(Constants.LOG_TAG, e.getMessage(), e);
                    mException = e;
                    return null;
                }
            }
            else {
                return null;
            }
        }

        @Override
        protected void onPreExecute() {
            if (mTarget.get() != null) {
                mTarget.get().mLoadingDialog = LoadingDialog.show(mTarget.get(), true, true, false);
            }
        }
        
        /*
         * (non-Javadoc)
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        protected void onPostExecute(Boolean result) {
            if (mTarget.get() != null) {
                if (mTarget.get().mLoadingDialog.isShowing()) {
                    mTarget.get().mLoadingDialog.dismiss();
                }
                if (mException != null) {
                    if (mIsParticipateAction) {
                        mTarget.get().showMessage(getResources().getString(R.string.area_join_error, mAreaId, mException.getLocalizedMessage()), false);
                    } else {
                        mTarget.get().showMessage(getResources().getString(R.string.area_leave_error, mAreaId, mException.getLocalizedMessage()), false);
                    }
                } else {
                    Uri CONTACT_URI = dbUri(DBProvider.MEMBERSHIP_CONTENT_URI);
                    if (mIsParticipateAction) {
                        ContentValues values = new ContentValues();
                        values.put(DB.Membership.COLUMN_AREA_ID, mAreaId);
                        values.put(DB.Membership.COLUMN_MEMBER_ID, getMemberId());
                        getContentResolver().insert(CONTACT_URI, values);
                        
                        mTarget.get().showMessage(getResources().getString(R.string.area_join_success, mAreaId), false);
                        
                    } else {
                        Uri.Builder ub = dbUri(DBProvider.MEMBERSHIP_CONTENT_URI).buildUpon();
                        ub = ub.appendQueryParameter(DB.Membership.COLUMN_AREA_ID, mAreaId);
                        ub = ub.appendQueryParameter(DB.Membership.COLUMN_MEMBER_ID, getMemberId());
                        getContentResolver().delete(ub.build(), null, null);
                        
                        mTarget.get().showMessage(getResources().getString(R.string.area_leave_success, mAreaId), false);
                    }
                }
                
                updateIVMembership(); // update member icon
            }
        }
    }
}
