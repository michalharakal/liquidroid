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

package liqui.droid.service.task;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import java.util.List;

import lfapi.v2.schema.Area;
import lfapi.v2.schema.Member;
import lfapi.v2.services.LiquidFeedbackService.AreaUnitService;
import lfapi.v2.services.LiquidFeedbackServiceFactory;
import liqui.droid.Constants;
import liqui.droid.db.DB;
import liqui.droid.db.DBProvider;

public class SyncMembership extends SyncAbstractTask {

    public SyncMembership(Context ctx, Intent intent, LiquidFeedbackServiceFactory factory,
            String databaseName) {

        super(ctx, intent, factory, databaseName, DB.Membership.TABLE, SYNC_TIME_HOUR_1);
    }

    public void sync(Context ctx, String ids) {
        AreaUnitService aus = mFactory.createAreaUnitService();

        int page = 0; boolean hasMore = true;
        while (hasMore) {
            Member.Options mo = new Member.Options();
            mo.limit = Constants.LIMIT;
            mo.offset = ((page++) * mo.limit);
            
            List<Area.Membership> l = aus.getMembership(null, mo, null);
        
            if (l.size() < mo.limit) {
                hasMore = false;
            }

            ContentValues[] v = new ContentValues[l.size()];
            int idx = 0;
            for (Area.Membership m : l) {
                ContentValues values = new ContentValues();

                values.put(DB.Membership.COLUMN_AREA_ID, m.areaId);
                values.put(DB.Membership.COLUMN_MEMBER_ID, m.memberId);

                v[idx++] = values;
            }

            ctx.getContentResolver().bulkInsert(dbUri(DBProvider.MEMBERSHIP_CONTENT_URI), v);
        }
    }
}