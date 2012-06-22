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

import lfapi.v2.schema.Draft;
import lfapi.v2.services.LiquidFeedbackServiceFactory;
import lfapi.v2.services.LiquidFeedbackService.DraftService;
import liqui.droid.Constants;
import liqui.droid.db.DB;
import liqui.droid.db.DBProvider;

public class SyncDraft extends SyncAbstractTask {

    public SyncDraft(Context ctx, Intent intent, LiquidFeedbackServiceFactory factory,
            String databaseName) {

        super(ctx, intent, factory, databaseName, DB.Draft.TABLE, SYNC_TIME_MIN_15);
    }

    public void sync(Context ctx, String ids) {
        DraftService ds = mFactory.createDraftService();
        
        int page = 0; boolean hasMore = true;
        while(hasMore) {
            Draft.Options dlgo = new Draft.Options();
            dlgo.limit = Constants.LIMIT;
            dlgo.offset = ((page++) * dlgo.limit);
            
            List<Draft> l = ds.getDraft(dlgo, null, null, null, null);
        
            if (l.size() < dlgo.limit) {
                hasMore = false;
            }

            ContentValues[] v = new ContentValues[l.size()];
            int idx = 0;
            for(Draft d : l) {
                ContentValues values = new ContentValues();
                values.put(DB.Draft.COLUMN_AREA_ID, d.areaId);
                values.put(DB.Draft.COLUMN_ISSUE_ID, d.issueId);
                values.put(DB.Draft.COLUMN_INITIATIVE_ID, d.initiativeId);
                values.put(DB.Draft.COLUMN_POLICY_ID, d.policyId);
                values.put(DB.Draft.COLUMN_INITIATIVE_NAME, d.initiativeName);
                values.put(DB.Draft.COLUMN_INITIATIVE_DISCUSSION_URL, d.initiativeDiscussionUrl);
                values.put(DB.Draft.COLUMN_FORMATTING_ENGINE, d.formattingEngine);
                values.put(DB.Draft.COLUMN_CONTENT, d.content);

                v[idx++] = values;
            }

            ctx.getContentResolver().bulkInsert(dbUri(DBProvider.DRAFT_CONTENT_URI), v);
        }
    }
}