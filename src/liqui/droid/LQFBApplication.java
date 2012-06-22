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

package liqui.droid;

import lfapi.v2.schema.Area;
import liqui.droid.activity.MemberActivity;
import liqui.droid.R;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.Toast;

/**
 * The Class LQFBApplication.
 */
public class LQFBApplication extends Application {

    /*
     * (non-Javadoc)
     * @see android.app.Application#onCreate()
     */
    @Override
    public void onCreate() {
        super.onCreate();
    }

    /**
     * Populate Repository into Bundle.
     * 
     * @param area the repository
     * @return the bundle
     */
    public Bundle populateArea(Area area) {
        Bundle data = new Bundle();

        if (area == null)
            return data;

        data.putInt(Constants.Area.ID,             area.id);
        data.putInt(Constants.Area.UNIT_ID,        area.unitId);
        data.putBoolean(Constants.Area.ACTIVE,     area.active);
        data.putString(Constants.Area.NAME,        area.name);
        data.putString(Constants.Area.DESCRIPTION, area.description);
        data.putInt(Constants.Area.DIRECT_MEMBER_COUNT, area.directMemberCount);
        data.putInt(Constants.Area.MEMBER_WEIGHT, area.memberWeight);

        return data;
    }

    /**
     * Open user activity.
     * 
     * @param context the context
     * @param login the login
     * @param name the name
     */
    public void openUserInfoActivity(Context context, String login, String name) {
        Intent intent = new Intent().setClass(context, MemberActivity.class);
        intent.putExtra(Constants.Member.LOGIN, login);
        intent.putExtra(Constants.Member.NAME, name);
        context.startActivity(intent);
    }

    /**
     * Not found message.
     * 
     * @param context the context
     * @param pluralsId the plurals id
     */
    public void notFoundMessage(Context context, int pluralsId) {
        Resources res = context.getResources();
        Toast.makeText(
                context,
                String.format(res.getString(R.string.record_not_found),
                        res.getQuantityString(pluralsId, 1)), Toast.LENGTH_SHORT).show();
    }

    /**
     * Not found message.
     * 
     * @param context the context
     * @param object the object
     */
    public void notFoundMessage(Context context, String object) {
        Resources res = context.getResources();
        Toast.makeText(context, String.format(res.getString(R.string.record_not_found), object),
                Toast.LENGTH_SHORT).show();
    }
}
