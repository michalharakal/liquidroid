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

public class SyncIssueComment extends SyncAbstractTask {

    public SyncIssueComment(Context ctx, Intent intent, LiquidFeedbackServiceFactory factory,
            String databaseName) {

        super(ctx, intent, factory, databaseName, DB.Issue.Comment.TABLE, SYNC_TIME_HOUR_1);
    }

    public void sync(Context ctx, String ids) {
         IssueService is = mFactory.createIssueService();
        
        int page = 0; boolean hasMore = true;
        while(hasMore) {
            Issue.Options io = new Issue.Options();
            io.id = ids;
            io.limit = Constants.LIMIT;
            io.offset = ((page++) * io.limit);
            
            List<Issue.Comment> l = is.getIssueComment(io);
        
            if (l.size() < io.limit) {
                hasMore = false;
            }

            ContentValues[] v = new ContentValues[l.size()];
            int idx = 0;
            for(Issue.Comment iss : l) {
                ContentValues values = new ContentValues();
                values.put(DB.Issue.Comment.COLUMN_ISSUE_ID, iss.issueId);
                values.put(DB.Issue.Comment.COLUMN_MEMBER_ID, iss.memberId);
                values.put(DB.Issue.Comment.COLUMN_CHANGED, iss.changed);
                values.put(DB.Issue.Comment.COLUMN_FORMATTING_ENGINE, iss.formattingEngine);
                values.put(DB.Issue.Comment.COLUMN_CONTENT, iss.content);
                
                v[idx++] = values;
            }

            ctx.getContentResolver().bulkInsert(dbUri(DBProvider.ISSUE_COMMENT_CONTENT_URI), v);
        }
    }
}