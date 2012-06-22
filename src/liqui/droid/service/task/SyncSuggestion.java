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

import lfapi.v2.schema.Suggestion;
import lfapi.v2.services.LiquidFeedbackServiceFactory;
import lfapi.v2.services.LiquidFeedbackService.SuggestionService;
import liqui.droid.Constants;
import liqui.droid.db.DB;
import liqui.droid.db.DBProvider;

public class SyncSuggestion extends SyncAbstractTask {

    public SyncSuggestion(Context ctx, Intent intent, LiquidFeedbackServiceFactory factory,
            String databaseName) {

        super(ctx, intent, factory, databaseName, DB.Suggestion.TABLE, SYNC_TIME_MIN_15);
    }

    public void sync(Context ctx, String ids) {
        SuggestionService ss = mFactory.createSuggestionService();
        int page = 0; boolean hasMore = true;
        while (hasMore) {
            Suggestion.Options so = new Suggestion.Options();
            so.limit = Constants.LIMIT;
            so.offset = ((page++) * so.limit);
            
            List<Suggestion> l = ss.getSuggestion(so, null);
        
            if (l.size() < so.limit) {
                hasMore = false;
            }

            ContentValues[] v = new ContentValues[l.size()];
            int idx = 0;
            for (Suggestion s : l) {
                ContentValues values = new ContentValues();

                values.put(DB.Suggestion.COLUMN_ID, s.id);
                values.put(DB.Suggestion.COLUMN_CREATED, s.created.getMillis());
                values.put(DB.Suggestion.COLUMN_AUTHOR_ID, s.authorId);
                values.put(DB.Suggestion.COLUMN_NAME, s.name);
                values.put(DB.Suggestion.COLUMN_FORMATTING_ENGINE, s.formattingEngine);
                values.put(DB.Suggestion.COLUMN_CONTENT, s.content);
                values.put(DB.Suggestion.COLUMN_MINUS2_UNFULFILLED_COUNT, s.minus2UnfulfilledCount);
                values.put(DB.Suggestion.COLUMN_MINUS1_UNFULFILLED_COUNT, s.minus1UnfulfilledCount);
                values.put(DB.Suggestion.COLUMN_MINUS1_FULFILLED_COUNT, s.minus1FulfilledCount);
                values.put(DB.Suggestion.COLUMN_PLUS1_UNFULFILLED_COUNT, s.plus1UnfulfilledCount);
                values.put(DB.Suggestion.COLUMN_PLUS1_FULFILLED_COUNT, s.plus1FulfilledCount);
                values.put(DB.Suggestion.COLUMN_PLUS2_UNFULFILLED_COUNT, s.plus2UnfulfilledCount);
                values.put(DB.Suggestion.COLUMN_PLUS2_FULFILLED_COUNT, s.plus2FulfilledCount);
                
                v[idx++] = values;
            }

            ctx.getContentResolver().bulkInsert(dbUri(DBProvider.SUGGESTION_CONTENT_URI), v);
        }
    }
}