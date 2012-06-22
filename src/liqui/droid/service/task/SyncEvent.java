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

import lfapi.v2.schema.Event;
import lfapi.v2.services.LiquidFeedbackServiceFactory;
import lfapi.v2.services.LiquidFeedbackService.EventService;
import liqui.droid.Constants;
import liqui.droid.db.DB;
import liqui.droid.db.DBProvider;

public class SyncEvent extends SyncAbstractTask {

    
    public SyncEvent(Context ctx, Intent intent, LiquidFeedbackServiceFactory factory,
            String databaseName) {
        
        super(ctx, intent, factory, databaseName, DB.Event.TABLE, SYNC_TIME_MIN_15);
    }
    
    public void sync(Context ctx, String ids) {
        EventService es = mFactory.createEventService();
        
        int page = 0; boolean hasMore = true;
        while(hasMore) {
            Event.Options eo = new Event.Options();
            
            eo.limit = Constants.LIMIT;
            eo.offset = ((page++) * eo.limit);
            
            List<Event> l = es.getEvent(eo);
        
            if (l.size() < eo.limit) {
                hasMore = false;
            }
            
            ContentValues[] v = new ContentValues[l.size()];
            int idx = 0;
            for (Event e : l) {
                ContentValues values = new ContentValues();
                values.put(DB.Event.COLUMN_ID, e.id);
                values.put(DB.Event.COLUMN_OCCURRENCE, (e.occurrence != null) ? e.occurrence.getMillis() + "" : "null");
                values.put(DB.Event.COLUMN_EVENT, e.event.toString());
                values.put(DB.Event.COLUMN_MEMBER_ID, (e.memberId != null) ? e.memberId.toString() : "null");
                values.put(DB.Event.COLUMN_ISSUE_ID, (e.issueId != null) ? e.issueId.toString() : "null");
                values.put(DB.Event.COLUMN_DRAFT_ID, (e.draftId != null) ? e.draftId.toString() : "null");
                values.put(DB.Event.COLUMN_SUGGESTION_ID, (e.suggestionId != null) ? e.suggestionId.toString() : "null");
                
                v[idx++] = values;
            }

            ctx.getContentResolver().bulkInsert(dbUri(DBProvider.EVENT_CONTENT_URI), v);
        }
    }
}