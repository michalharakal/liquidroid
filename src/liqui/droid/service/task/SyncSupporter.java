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

import lfapi.v2.schema.Initiative;
import lfapi.v2.services.LiquidFeedbackServiceFactory;
import lfapi.v2.services.LiquidFeedbackService.InitiativeService;
import liqui.droid.Constants;
import liqui.droid.db.DB;
import liqui.droid.db.DBProvider;

public class SyncSupporter extends SyncAbstractTask {

    public SyncSupporter(Context ctx, Intent intent, LiquidFeedbackServiceFactory factory,
            String databaseName) {

        super(ctx, intent, factory, databaseName, DB.Initiative.Supporter.TABLE, SYNC_TIME_HOUR_1);
    }

    public void sync(Context ctx, String ids) {
        InitiativeService is = mFactory.createInitiativeService();
        
        int page = 0; boolean hasMore = true;
        while (hasMore) {
            Initiative.Options io = new Initiative.Options();
            io.limit = Constants.LIMIT;
            io.offset = ((page++) * io.limit);
            
            List<Initiative.Supporter> l = is.getSupporter(io, null, "latest");
        
            if (l.size() < io.limit) {
                hasMore = false;
            }

            ContentValues[] v = new ContentValues[l.size()];
            int idx = 0;
            for (Initiative.Supporter s : l) {
                ContentValues values = new ContentValues();

                values.put(DB.Initiative.Supporter.COLUMN_INITIATIVE_ID, s.initiativeId);
                values.put(DB.Initiative.Supporter.COLUMN_EVENT, s.event);
                values.put(DB.Initiative.Supporter.COLUMN_MEMBER_ID, s.memberId);
                values.put(DB.Initiative.Supporter.COLUMN_DRAFT_ID, s.draftId);
                values.put(DB.Initiative.Supporter.COLUMN_WEIGHT, s.weight);
                values.put(DB.Initiative.Supporter.COLUMN_SCOPE, s.scope != null ? s.scope.toString() : null);
                values.put(DB.Initiative.Supporter.COLUMN_DELEGATION_MEMBER_IDS, s.delegateMemberIds);
                values.put(DB.Initiative.Supporter.COLUMN_INFORMED, s.informed);
                values.put(DB.Initiative.Supporter.COLUMN_SATISFIED, s.satisfied);

                v[idx++] = values;
            }

            ctx.getContentResolver().bulkInsert(dbUri(DBProvider.SUPPORTER_CONTENT_URI), v);
        }
    }
}