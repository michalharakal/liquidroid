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
import lfapi.v2.schema.Snapshot;
import lfapi.v2.services.LiquidFeedbackServiceFactory;
import lfapi.v2.services.LiquidFeedbackService.IssueService;
import liqui.droid.Constants;
import liqui.droid.db.DB;
import liqui.droid.db.DBProvider;

public class SyncInterest extends SyncAbstractTask {

    public SyncInterest(Context ctx, Intent intent, LiquidFeedbackServiceFactory factory,
            String databaseName) {

        super(ctx, intent, factory, databaseName, DB.Issue.Interest.TABLE, SYNC_TIME_HOUR_1);
    }

    public void sync(Context ctx, String ids) {
        IssueService is = mFactory.createIssueService();
        
        int page = 0; boolean hasMore = true;
        while(hasMore) {
            Issue.Options io = new Issue.Options();
            io.id = ids;
            io.limit = Constants.LIMIT;
            io.offset = ((page++) * io.limit);
            
            List<Issue.Interest> l = is.getInterest(io, Snapshot.Event.latest);
        
            if (l.size() < io.limit) {
                hasMore = false;
            }

            ContentValues[] v = new ContentValues[l.size()];
            int idx = 0;
            for(Issue.Interest i : l) {
                ContentValues values = new ContentValues();
                values.put(DB.Issue.Interest.COLUMN_ISSUE_ID, i.issueId);
                values.put(DB.Issue.Interest.COLUMN_EVENT, i.event);
                values.put(DB.Issue.Interest.COLUMN_MEMBER_ID, i.memberId);
                values.put(DB.Issue.Interest.COLUMN_WEIGHT, i.weight);
                values.put(DB.Issue.Interest.COLUMN_DELEGATE_MEMBER_IDS, i.delegateMemberIds);
                
                v[idx++] = values;
            }

            ctx.getContentResolver().bulkInsert(dbUri(DBProvider.INTEREST_CONTENT_URI), v);
        }
    }
}