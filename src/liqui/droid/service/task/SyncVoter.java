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

import lfapi.v2.schema.Member;
import lfapi.v2.schema.Vote;
import lfapi.v2.services.LiquidFeedbackService.VotingService;
import lfapi.v2.services.LiquidFeedbackServiceFactory;
import liqui.droid.Constants;
import liqui.droid.db.DB;
import liqui.droid.db.DBProvider;

public class SyncVoter extends SyncAbstractTask {

    public SyncVoter(Context ctx, Intent intent, LiquidFeedbackServiceFactory factory,
            String databaseName) {

        super(ctx, intent, factory, databaseName, DB.Vote.Voter.TABLE, SYNC_TIME_MIN_15);
    }

    public void sync(Context ctx, String ids) {
        VotingService vs = mFactory.createVoteService();

        int page = 0; boolean hasMore = true;
        while (hasMore) {
            Member.Options mo = new Member.Options();
            mo.memberId = ids;
            mo.limit = Constants.LIMIT;
            mo.offset = ((page++) * mo.limit);
            
            List<Vote.Voter> l = vs.getVoter(null, mo);
        
            if (l.size() < mo.limit) {
                hasMore = false;
            }

            ContentValues[] v = new ContentValues[l.size()];
            int idx = 0;
            for (Vote.Voter vo : l) {
                ContentValues values = new ContentValues();

                values.put(DB.Vote.Voter.COLUMN_ISSUE_ID, vo.issueId);
                values.put(DB.Vote.Voter.COLUMN_MEMBER_ID, vo.memberId);
                values.put(DB.Vote.Voter.COLUMN_WEIGHT, vo.weight);

                v[idx++] = values;
            }

            ctx.getContentResolver().bulkInsert(dbUri(DBProvider.VOTER_CONTENT_URI), v);
        }
    }
}