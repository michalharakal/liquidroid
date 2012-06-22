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

import lfapi.v2.schema.Opinion;
import lfapi.v2.services.LiquidFeedbackServiceFactory;
import lfapi.v2.services.LiquidFeedbackService.SuggestionService;
import liqui.droid.Constants;
import liqui.droid.db.DB;
import liqui.droid.db.DBProvider;

public class SyncOpinion extends SyncAbstractTask {

    public SyncOpinion(Context ctx, Intent intent, LiquidFeedbackServiceFactory factory,
            String databaseName) {

        super(ctx, intent, factory, databaseName, DB.Opinion.TABLE, SYNC_TIME_MIN_15);
    }

    public void sync(Context ctx, String ids) {
        SuggestionService ss = mFactory.createSuggestionService();
        
        int page = 0; boolean hasMore = true;
        while(hasMore) {
            Opinion.Options oo = new Opinion.Options();
            oo.limit = Constants.LIMIT;
            oo.offset = ((page++) * oo.limit);
            
            List<Opinion> l = ss.getOpinion(oo, null, null, null, null);
        
            if (l.size() < oo.limit) {
                hasMore = false;
            }

            ContentValues[] v = new ContentValues[l.size()];
            int idx = 0;
            for (Opinion o : l) {
                ContentValues values = new ContentValues();
                values.put(DB.Opinion.COLUMN_SUGGESTION_ID, o.suggestionId);
                values.put(DB.Opinion.COLUMN_MEMBER_ID, o.memberId);
                values.put(DB.Opinion.COLUMN_DEGREE, o.degree);
                values.put(DB.Opinion.COLUMN_FULFILLED, o.fulfilled);
                
                v[idx++] = values;
            }

            ctx.getContentResolver().bulkInsert(dbUri(DBProvider.OPINION_CONTENT_URI), v);
        }
    }
}