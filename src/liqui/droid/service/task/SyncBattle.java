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
import lfapi.v2.services.LiquidFeedbackService.InitiativeService;
import lfapi.v2.services.LiquidFeedbackServiceFactory;
import liqui.droid.Constants;
import liqui.droid.db.DB;
import liqui.droid.db.DBProvider;

public class SyncBattle extends SyncAbstractTask {
    
    public SyncBattle(Context ctx, Intent intent, LiquidFeedbackServiceFactory factory,
            String databaseName) {
        
        super(ctx, intent, factory, databaseName, DB.Initiative.Battle.TABLE, SYNC_TIME_HOUR_1);
    }
    
    public void sync(Context ctx, String areaIds) {
        InitiativeService is = mFactory.createInitiativeService();
        
        int page = 0; boolean hasMore = true;
        while(hasMore) {
            Initiative.Options io = new Initiative.Options();
            
            io.limit = Constants.LIMIT;
            io.offset = ((page++) * io.limit);
        
            List<Initiative.Battle> l = is.getBattle(io);
            
            if (l.size() < io.limit) {
                hasMore = false;
            }

            ContentValues[] v = new ContentValues[l.size()];
            int idx = 0;
            for(Initiative.Battle b : l) {
                ContentValues values = new ContentValues();
                values.put(DB.Initiative.Battle.COLUMN_ISSUE_ID, b.issueId);
                values.put(DB.Initiative.Battle.COLUMN_WINNING_INITIATIVE_ID, b.winningInitiativeId);
                values.put(DB.Initiative.Battle.COLUMN_LOSING_INITIATIVE_ID, b.losingInitativeId);
                values.put(DB.Initiative.Battle.COLUMN_COUNT, b.count);
                
                v[idx++] = values;
                // println(values.toString());
            }
            
            ctx.getContentResolver().bulkInsert(dbUri(DBProvider.BATTLE_CONTENT_URI), v);
        }
    }
}