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

import lfapi.v2.schema.Unit;
import lfapi.v2.services.LiquidFeedbackServiceFactory;
import lfapi.v2.services.LiquidFeedbackService.AreaUnitService;
import liqui.droid.Constants;
import liqui.droid.db.DB;
import liqui.droid.db.DBProvider;

public class SyncUnit extends SyncAbstractTask {

    public SyncUnit(Context ctx, Intent intent, LiquidFeedbackServiceFactory factory,
            String databaseName) {

        super(ctx, intent, factory, databaseName, DB.Unit.TABLE, SYNC_TIME_HOUR_12);
    }

    public void sync(Context ctx, String ids) {
        AreaUnitService aus = mFactory.createAreaUnitService();

        int page = 0; boolean hasMore = true;
        while (hasMore) {
            Unit.Options uo = new Unit.Options();
            uo.unitId = ids;
            uo.limit = Constants.LIMIT;
            uo.offset = ((page++) * uo.limit);
            
            List<Unit> l = aus.getUnit(uo);
        
            if (l.size() < uo.limit) {
                hasMore = false;
            }

            ContentValues[] v = new ContentValues[l.size()];
            int idx = 0;
            for (Unit u : l) {
                ContentValues values = new ContentValues();

                values.put(DB.Unit.COLUMN_ID, u.id);
                values.put(DB.Unit.COLUMN_PARENT_ID, u.parentId);
                values.put(DB.Unit.COLUMN_ACTIVE, u.active);
                values.put(DB.Unit.COLUMN_NAME, u.name);
                values.put(DB.Unit.COLUMN_DESCRIPTION, u.description);
                values.put(DB.Unit.COLUMN_MEMBER_COUNT, u.memberCount);

                v[idx++] = values;
            }

            ctx.getContentResolver().bulkInsert(dbUri(DBProvider.UNIT_CONTENT_URI), v);
        }
    }
}