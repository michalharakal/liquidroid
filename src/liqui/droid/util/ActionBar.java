/*
 * Copyright (C) 2010 Johan Nilsson <http://markupartist.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package liqui.droid.util;

import java.util.LinkedList;

import liqui.droid.R;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * The Class ActionBar.
 */
public class ActionBar extends RelativeLayout implements OnClickListener {

    /** The m inflater. */
    private LayoutInflater mInflater;
    
    /** The m bar view. */
    private RelativeLayout mBarView;
    
    /** The m logo view. */
    private ImageView mLogoView;
    // private View mHomeView;
    /** The m title view. */
    private TextView mTitleView;
    
    /** The m actions view. */
    private LinearLayout mActionsView;
    
    /** The m home btn. */
    private ImageButton mHomeBtn;
    
    /** The m home layout. */
    private RelativeLayout mHomeLayout;
    
    private ProgressBar mProgress;

    /**
     * Instantiates a new action bar.
     *
     * @param context the context
     * @param attrs the attrs
     */
    public ActionBar(Context context, AttributeSet attrs) {
        super(context, attrs);

        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mBarView = (RelativeLayout) mInflater.inflate(R.layout.inc_actionbar, null);
        addView(mBarView);

        mLogoView = (ImageView) mBarView.findViewById(R.id.actionbar_home_logo);
        mHomeLayout = (RelativeLayout) mBarView.findViewById(R.id.actionbar_home_bg);
        mHomeBtn = (ImageButton) mBarView.findViewById(R.id.actionbar_home_btn);

        mTitleView = (TextView) mBarView.findViewById(R.id.actionbar_title);
        mActionsView = (LinearLayout) mBarView.findViewById(R.id.actionbar_actions);

        mProgress = (ProgressBar) mBarView.findViewById(R.id.actionbar_progress);
        
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ActionBar);
        CharSequence title = a.getString(R.styleable.ActionBar_title);
        if (title != null) {
            setTitle(title);
        }
        a.recycle();
    }
    
    public void setProgressVisibile(boolean isVisible) {
        mProgress.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    /**
     * Sets the home action.
     *
     * @param action the new home action
     */
    public void setHomeAction(Action action) {
        mHomeBtn.setOnClickListener(this);
        mHomeBtn.setTag(action);
        mHomeBtn.setImageResource(action.getDrawable());
        mHomeLayout.setVisibility(View.VISIBLE);
    }

    /**
     * Shows the provided logo to the left in the action bar. This is ment to be
     * used instead of the setHomeAction and does not draw a divider to the left
     * of the provided logo.
     * 
     * @param resId The drawable resource id
     */
    public void setHomeLogo(int resId) {
        // fixme Add possibility to add an IntentAction as well.
        mLogoView.setImageResource(resId);
        mLogoView.setVisibility(View.VISIBLE);
        mHomeLayout.setVisibility(View.GONE);
    }

    /**
     * Sets the title.
     *
     * @param title the new title
     */
    public void setTitle(CharSequence title) {
        mTitleView.setText(title);
    }

    /**
     * Sets the title.
     *
     * @param resid the new title
     */
    public void setTitle(int resid) {
        mTitleView.setText(resid);
    }

    /* (non-Javadoc)
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(View view) {
        final Object tag = view.getTag();
        if (tag instanceof Action) {
            final Action action = (Action) tag;
            action.performAction(view);
        }
    }

    /**
     * Adds a list of {@link Action}s.
     * 
     * @param actionList the actions to add
     */
    public void addActions(ActionList actionList) {
        int actions = actionList.size();
        for (int i = 0; i < actions; i++) {
            addAction(actionList.get(i));
        }
    }

    /**
     * Adds a new {@link Action}.
     * 
     * @param action the action to add
     */
    public void addAction(Action action) {
        final int index = mActionsView.getChildCount();
        addAction(action, index);
    }

    /**
     * Adds a new {@link Action} at the specified index.
     * 
     * @param action the action to add
     * @param index the position at which to add the action
     */
    public void addAction(Action action, int index) {
        mActionsView.addView(inflateAction(action), index);
    }

    /**
     * Inflates a {@link View} with the given {@link Action}.
     * 
     * @param action the action to inflate
     * @return a view
     */
    private View inflateAction(Action action) {
        View view = mInflater.inflate(R.layout.inc_actionbar_item, mActionsView, false);

        ImageButton labelView = (ImageButton) view.findViewById(R.id.actionbar_item);
        labelView.setImageResource(action.getDrawable());

        view.setTag(action);
        view.setOnClickListener(this);
        return view;
    }

    /**
     * A {@link LinkedList} that holds a list of {@link Action}s.
     */
    public static class ActionList extends LinkedList<Action> {

        /** The Constant serialVersionUID. */
        private static final long serialVersionUID = -7639253919045641775L;
    }

    /**
     * Definition of an action that could be performed, along with a icon to
     * show.
     */
    public interface Action {
        
        /**
         * Gets the drawable.
         *
         * @return the drawable
         */
        public int getDrawable();

        /**
         * Perform action.
         *
         * @param view the view
         */
        public void performAction(View view);
    }

    /**
     * The Class AbstractAction.
     */
    public static abstract class AbstractAction implements Action {
        
        /** The m drawable. */
        final private int mDrawable;

        /**
         * Instantiates a new abstract action.
         *
         * @param drawable the drawable
         */
        public AbstractAction(int drawable) {
            mDrawable = drawable;
        }

        /* (non-Javadoc)
         * @see com.markupartist.android.widget.ActionBar.Action#getDrawable()
         */
        @Override
        public int getDrawable() {
            return mDrawable;
        }
    }

    /**
     * The Class IntentAction.
     */
    public static class IntentAction extends AbstractAction {
        
        /** The m context. */
        private Context mContext;
        
        /** The m intent. */
        private Intent mIntent;

        /**
         * Instantiates a new intent action.
         *
         * @param context the context
         * @param intent the intent
         * @param drawable the drawable
         */
        public IntentAction(Context context, Intent intent, int drawable) {
            super(drawable);
            mContext = context;
            mIntent = intent;
        }

        /* (non-Javadoc)
         * @see com.markupartist.android.widget.ActionBar.Action#performAction(android.view.View)
         */
        @Override
        public void performAction(View view) {
            try {
                mContext.startActivity(mIntent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(mContext, mContext.getText(R.string.actionbar_activity_not_found),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    /*
     * public static abstract class SearchAction extends AbstractAction { public
     * SearchAction() { super(R.drawable.actionbar_search); } }
     */
}
