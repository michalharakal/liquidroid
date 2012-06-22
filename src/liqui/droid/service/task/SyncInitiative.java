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

public class SyncInitiative extends SyncAbstractTask {

    public SyncInitiative(Context ctx, Intent intent, LiquidFeedbackServiceFactory factory,
            String databaseName) {

        super(ctx, intent, factory, databaseName, DB.Initiative.TABLE, SYNC_TIME_HOUR_1);
    }

    public void sync(Context ctx, String ids) {
        InitiativeService is = mFactory.createInitiativeService();
        
        int page = 0; boolean hasMore = true;
        while(hasMore) {
            Initiative.Options io = new Initiative.Options();
            io.id = ids;
            io.limit = Constants.LIMIT;
            io.offset = ((page++) * io.limit);
            
            List<Initiative> l = is.getInitiative(io);
        
            if (l.size() < io.limit) {
                hasMore = false;
            }

            ContentValues[] v = new ContentValues[l.size()];
            int idx = 0;
            for(Initiative i : l) {
                ContentValues values = new ContentValues();
                values.put(DB.Initiative.COLUMN_ID, i.id);
                values.put(DB.Initiative.COLUMN_ISSUE_ID, i.issueId);
                values.put(DB.Initiative.COLUMN_NAME, i.name);
                values.put(DB.Initiative.COLUMN_DISCUSSION_URL, i.discussionUrl);
                values.put(DB.Initiative.COLUMN_CREATED, i.created.getMillis());
                values.put(DB.Initiative.COLUMN_REVOKED, i.revoked != null ? i.revoked.getMillis() : null);
                values.put(DB.Initiative.COLUMN_REVOKED_BY_MEMBER_ID, i.revokedByMemberId);
                values.put(DB.Initiative.COLUMN_SUGGESTED_INITIATIVE_ID, i.suggestedInitiativeId);
                values.put(DB.Initiative.COLUMN_ADMITTED, i.admitted);
                values.put(DB.Initiative.COLUMN_SUPPORTER_COUNT, i.supporterCount);
                values.put(DB.Initiative.COLUMN_INFORMED_SUPPORTER_COUNT, i.informedSupporterCount);
                values.put(DB.Initiative.COLUMN_SATISFIED_SUPPORTER_COUNT, i.satisfiedSupporterCount);
                values.put(DB.Initiative.COLUMN_SATISFIED_INFORMED_SUPPORTER_COUNT, i.satisfiedInformedSupporterCount);
                values.put(DB.Initiative.COLUMN_POSITIVE_VOTES, i.positiveVotes);
                values.put(DB.Initiative.COLUMN_NEGATIVE_VOTES, i.negativeVotes);
                values.put(DB.Initiative.COLUMN_DIRECT_MAJORITY, i.directMajority);
                values.put(DB.Initiative.COLUMN_INDIRECT_MAJORITY, i.indirectMajority);
                values.put(DB.Initiative.COLUMN_SCHULZE_RANK, i.schulzeRank);
                values.put(DB.Initiative.COLUMN_BETTER_THAN_STATUS_QUO, i.betterThanStatusQuo);
                values.put(DB.Initiative.COLUMN_WORSE_THAN_STATUS_QUO, i.worseThanStatusQuo);
                values.put(DB.Initiative.COLUMN_REVERSE_BEAT_PATH, i.reverseBeatPath);
                values.put(DB.Initiative.COLUMN_MULTISTAGE_MAJORITY, i.multistageMajority);
                values.put(DB.Initiative.COLUMN_ELIGIBLE, i.eligible);
                values.put(DB.Initiative.COLUMN_RANK, i.rank);

                v[idx++] = values;
            }

            ctx.getContentResolver().bulkInsert(dbUri(DBProvider.INITIATIVE_CONTENT_URI), v);
        }
    }
}