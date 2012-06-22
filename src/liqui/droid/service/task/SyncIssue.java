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

import lfapi.v2.schema.Issue;
import lfapi.v2.services.LiquidFeedbackServiceFactory;
import lfapi.v2.services.LiquidFeedbackService.IssueService;
import liqui.droid.Constants;
import liqui.droid.db.DB;
import liqui.droid.db.DBProvider;

public class SyncIssue extends SyncAbstractTask {

    public SyncIssue(Context ctx, Intent intent, LiquidFeedbackServiceFactory factory,
            String databaseName) {

        super(ctx, intent, factory, databaseName, DB.Issue.TABLE, SYNC_TIME_MIN_15);
    }

    public void sync(Context ctx, String ids) {
        IssueService is = mFactory.createIssueService();
        
        int page = 0; boolean hasMore = true;
        while(hasMore) {
            Issue.Options io = new Issue.Options();
            io.id = ids;
            io.limit = Constants.LIMIT;
            io.offset = ((page++) * io.limit);
            
            List<Issue> l = is.getIssue(io);
        
            if (l.size() < io.limit) {
                hasMore = false;
            }

            ContentValues[] v = new ContentValues[l.size()];
            int idx = 0;
            for(Issue i : l) {
                ContentValues values = new ContentValues();
                values.put(DB.Issue.COLUMN_ID, i.id);
                values.put(DB.Issue.COLUMN_AREA_ID, i.areaId);
                values.put(DB.Issue.COLUMN_POLICY_ID, i.policyId);
                values.put(DB.Issue.COLUMN_STATE, i.state.toString());
                values.put(DB.Issue.COLUMN_CREATED, i.created.getMillis());
                values.put(DB.Issue.COLUMN_ACCEPTED, i.accepted != null ? i.accepted.getMillis() : null);
                values.put(DB.Issue.COLUMN_HALF_FROZEN, i.halfFrozen != null ? i.halfFrozen.getMillis() : null);
                values.put(DB.Issue.COLUMN_FULLY_FROZEN, i.fullyFrozen != null ? i.fullyFrozen.getMillis() : null);
                values.put(DB.Issue.COLUMN_CLOSED, i.closed != null ? i.closed.getMillis() : null);
                values.put(DB.Issue.COLUMN_RANKS_AVAILABLE, i.ranksAvailable);
                values.put(DB.Issue.COLUMN_CLEANED, i.cleaned != null ? i.cleaned.getMillis() : null);
                values.put(DB.Issue.COLUMN_ADMISSION_TIME, i.admissionTime != null ? i.admissionTime.toString() : null);
                values.put(DB.Issue.COLUMN_DISCUSSION_TIME, i.discussionTime != null ? i.discussionTime.toString() : null);
                values.put(DB.Issue.COLUMN_VOTING_TIME, i.votingTime.toString());
                values.put(DB.Issue.COLUMN_SNAPSHOT, i.snapshot.getMillis());
                values.put(DB.Issue.COLUMN_LATEST_SNAPSHOT_EVENT, i.latestSnapshotEvent);
                values.put(DB.Issue.COLUMN_POPULATION, i.population);
                values.put(DB.Issue.COLUMN_VOTER_COUNT, i.voterCount);
                values.put(DB.Issue.COLUMN_STATUS_QUO_SCHULZE_RANK, i.statusQuoSchulzeRank);
                
                v[idx++] = values;
            }

            ctx.getContentResolver().bulkInsert(dbUri(DBProvider.ISSUE_CONTENT_URI), v);
        }
    }
}