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

public class SyncMember extends SyncAbstractTask {

    public SyncMember(Context ctx, Intent intent, LiquidFeedbackServiceFactory factory,
            String databaseName) {

        super(ctx, intent, factory, databaseName, DB.Member.TABLE, SYNC_TIME_HOUR_12);
    }

    public void sync(Context ctx, String ids) {
        MemberService ms = mFactory.createMemberService();

        int page = 0; boolean hasMore = true;
        while (hasMore) {
            Member.Options mo = new Member.Options();
            mo.memberId = ids;
            mo.limit = Constants.LIMIT;
            mo.offset = ((page++) * mo.limit);
            
            List<Member> l = ms.getMember(mo);
        
            if (l.size() < mo.limit) {
                hasMore = false;
            }

            ContentValues[] v = new ContentValues[l.size()];
            int idx = 0;
            for (Member m : l) {
                ContentValues values = new ContentValues();

                values.put(DB.Member.COLUMN_ID,                   m.id);
                values.put(DB.Member.COLUMN_ACTIVATED,            m.activated != null ? m.activated.getMillis() : null);
                values.put(DB.Member.COLUMN_LAST_ACTIVITY,        m.lastActivity != null ? m.lastActivity.getMillis() : null);
                values.put(DB.Member.COLUMN_LAST_LOGIN,           m.lastLogin != null ? m.lastLogin.getMillis() : null);
                values.put(DB.Member.COLUMN_LOCKED,               m.locked);
                values.put(DB.Member.COLUMN_ACTIVE,               m.active);
                values.put(DB.Member.COLUMN_NAME,                 m.name);
                values.put(DB.Member.COLUMN_IDENTIFICATION,       m.identification);
                values.put(DB.Member.COLUMN_ORGANIZATIONAL_UNIT,  m.organizationalUnit);
                values.put(DB.Member.COLUMN_INTERNAL_POSTS,       m.internalPosts);
                values.put(DB.Member.COLUMN_REAL_NAME,            m.realName);
                values.put(DB.Member.COLUMN_BIRTHDAY,             m.birthday != null ? m.birthday.getMillis() : null);
                values.put(DB.Member.COLUMN_ADDRESS,              m.address);
                values.put(DB.Member.COLUMN_EMAIL,                m.eMail);
                values.put(DB.Member.COLUMN_XMPP_ADDRESS,         m.xmppAddress);
                values.put(DB.Member.COLUMN_WEBSITE,              m.website);
                values.put(DB.Member.COLUMN_PHONE,                m.phone);
                values.put(DB.Member.COLUMN_MOBILE_PHONE,         m.mobilePhone);
                values.put(DB.Member.COLUMN_PROFESSION,           m.profession);
                values.put(DB.Member.COLUMN_EXTERNAL_MEMBERSHIPS, m.externalMemberships);
                values.put(DB.Member.COLUMN_EXTERNAL_POSTS,       m.externalPosts);
                values.put(DB.Member.COLUMN_FORMATTING_ENGINE,    m.formattingEngine);
                values.put(DB.Member.COLUMN_STATEMENT,            m.statement);

                v[idx++] = values;
            }

            ctx.getContentResolver().bulkInsert(dbUri(DBProvider.MEMBER_CONTENT_URI), v);
        }
    }
}