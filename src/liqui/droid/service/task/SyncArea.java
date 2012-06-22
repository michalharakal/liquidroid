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

import lfapi.v2.schema.Area;
import lfapi.v2.services.LiquidFeedbackServiceFactory;
import lfapi.v2.services.LiquidFeedbackService.AreaUnitService;
import liqui.droid.Constants;
import liqui.droid.db.DB;
import liqui.droid.db.DBProvider;

public class SyncArea extends SyncAbstractTask {
    
    public SyncArea(Context ctx, Intent intent, LiquidFeedbackServiceFactory factory,
            String databaseName) {
        
        super(ctx, intent, factory, databaseName, DB.Area.TABLE, SYNC_TIME_HOUR_12);
    }
    
    public void sync(Context ctx, String areaIds) {
        AreaUnitService aus = mFactory.createAreaUnitService();
        
        int page = 0; boolean hasMore = true;
        while(hasMore) {
            Area.Options ao = new Area.Options();
            
            ao.areaId = areaIds;
            ao.limit = Constants.LIMIT;
            ao.offset = ((page++) * ao.limit);
        
            List<Area> l = aus.getArea(ao);
            
            if (l.size() < ao.limit) {
                hasMore = false;
            }

            ContentValues[] v = new ContentValues[l.size()];
            int idx = 0;
            for(Area a : l) {
                ContentValues values = new ContentValues();
                values.put(DB.Area.COLUMN_ID, a.id);
                values.put(DB.Area.COLUMN_UNIT_ID, a.unitId);
                values.put(DB.Area.COLUMN_ACTIVE, a.active);
                values.put(DB.Area.COLUMN_NAME, a.name);
                values.put(DB.Area.COLUMN_DESCRIPTION, a.description);
                values.put(DB.Area.COLUMN_DIRECT_MEMBER_COUNT, a.directMemberCount);
                values.put(DB.Area.COLUMN_MEMBER_WEIGHT, a.memberWeight);
                
                v[idx++] = values;
            }
            
            ctx.getContentResolver().bulkInsert(dbUri(DBProvider.AREA_CONTENT_URI), v);
        }
    }
}