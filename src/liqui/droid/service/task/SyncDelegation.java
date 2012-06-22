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

import lfapi.v2.schema.Delegation;
import lfapi.v2.services.LiquidFeedbackServiceFactory;
import lfapi.v2.services.LiquidFeedbackService.DelegationService;
import liqui.droid.Constants;
import liqui.droid.db.DB;
import liqui.droid.db.DBProvider;

public class SyncDelegation extends SyncAbstractTask {

    public SyncDelegation(Context ctx, Intent intent, LiquidFeedbackServiceFactory factory,
            String databaseName) {

        super(ctx, intent, factory, databaseName, DB.Delegation.TABLE, SYNC_TIME_HOUR_12);
    }
    
    public void sync(Context ctx, String ids) {
        DelegationService ps = mFactory.createDelegationService();
        
        int page = 0; boolean hasMore = true;
        while(hasMore) {
            Delegation.Options dlgo = new Delegation.Options();
            dlgo.limit = Constants.LIMIT;
            dlgo.offset = ((page++) * dlgo.limit);
        
            List<Delegation> l = ps.getDelegation(dlgo, null, null, null, null);
            
            if (l.size() < dlgo.limit) {
                hasMore = false;
            }

            ContentValues[] v = new ContentValues[l.size()];
            int idx = 0;
            for(Delegation d : l) {
                ContentValues values = new ContentValues();
                values.put(DB.Delegation.COLUMN_UNIT_ID, d.unitId);
                values.put(DB.Delegation.COLUMN_AREA_ID, d.areaId);
                values.put(DB.Delegation.COLUMN_ISSUE_ID, d.issueId);
                values.put(DB.Delegation.COLUMN_SCOPE, d.scope.toString());
                values.put(DB.Delegation.COLUMN_TRUSTER_ID, d.trusterId);
                values.put(DB.Delegation.COLUMN_TRUSTEE_ID, d.trusteeId);
                
                v[idx++] = values;
                // println(values.toString());
            }

            ctx.getContentResolver().bulkInsert(dbUri(DBProvider.DELEGATION_CONTENT_URI), v);
        }
    }
}
