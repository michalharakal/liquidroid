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
import lfapi.v2.services.LiquidFeedbackServiceFactory;
import lfapi.v2.services.LiquidFeedbackService.MemberService;
import liqui.droid.Constants;
import liqui.droid.db.DB;
import liqui.droid.db.DBProvider;

public class SyncMemberImage extends SyncAbstractTask {
    
    public SyncMemberImage(Context ctx, Intent intent, LiquidFeedbackServiceFactory factory,
            String databaseName) {

        super(ctx, intent, factory, databaseName, DB.Member.Image.TABLE, SYNC_TIME_HOUR_12);
    }

    public void sync(Context ctx, String ids) {
        MemberService ms = mFactory.createMemberService();

        int page = 0; boolean hasMore = true;
        while (hasMore) {
            Member.Image.Options mo = new Member.Image.Options();
            mo.memberId = ids;
            mo.limit = Constants.LIMIT;
            mo.offset = ((page++) * mo.limit);
            
            List<Member.Image> l = ms.getMemberImage("avatar", mo);
        
            if (l.size() < mo.limit) {
                hasMore = false;
            }

            ContentValues[] v = new ContentValues[l.size()];
            int idx = 0;
            for (Member.Image m : l) {
                ContentValues values = new ContentValues();

                values.put(DB.Member.Image.COLUMN_MEMBER_ID, m.memberId);
                values.put(DB.Member.Image.COLUMN_IMAGE_TYPE, m.imageType);
                values.put(DB.Member.Image.COLUMN_SCALED, m.scaled);
                values.put(DB.Member.Image.COLUMN_CONTENT_TYPE, m.contentType);
                values.put(DB.Member.Image.COLUMN_DATA, m.data);

                v[idx++] = values;
            }
            
            ctx.getContentResolver().bulkInsert(dbUri(DBProvider.MEMBER_IMAGE_CONTENT_URI), v);
        }
    }
}