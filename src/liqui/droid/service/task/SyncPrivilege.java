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

import lfapi.v2.schema.Privilege;
import lfapi.v2.services.LiquidFeedbackServiceFactory;
import lfapi.v2.services.LiquidFeedbackService.MemberService;
import liqui.droid.db.DB;
import liqui.droid.db.DBProvider;

public class SyncPrivilege extends SyncAbstractTask {

    public SyncPrivilege(Context ctx, Intent intent, LiquidFeedbackServiceFactory factory,
            String databaseName) {

        super(ctx, intent, factory, databaseName, DB.Privilege.TABLE, SYNC_TIME_HOUR_12);
    }

    public void sync(Context ctx, String ids) {
        MemberService ms = mFactory.createMemberService();

        // int page = 0;
        boolean hasMore = true;
        while (hasMore) {
            /* lfapi not working
            Member.Options po = new Member.Options();
            po.limit = LIMIT;
            po.offset = ((page++) * po.limit); */
            
            hasMore = false;
            
            List<Privilege> l = ms.getPrivilege(null);
            
            ContentValues[] v = new ContentValues[l.size()];
            int idx = 0;
            for (Privilege p : l) {
                ContentValues values = new ContentValues();
                
                values.put(DB.Privilege.COLUMN_UNIT_ID, p.unitId);
                values.put(DB.Privilege.COLUMN_MEMBER_ID, p.memberId);
                values.put(DB.Privilege.COLUMN_ADMIN_MANAGER, p.adminManager);
                values.put(DB.Privilege.COLUMN_UNIT_MANAGER, p.unitManager);
                values.put(DB.Privilege.COLUMN_AREA_MANAGER, p.areaManager);
                values.put(DB.Privilege.COLUMN_VOTING_RIGHT_MANAGER, p.votingRightManager);
                values.put(DB.Privilege.COLUMN_VOTING_RIGHT, p.votingRight);
                
                v[idx++] = values;
            }

            ctx.getContentResolver().bulkInsert(dbUri(DBProvider.PRIVILEGE_CONTENT_URI), v);
        }
    }
}