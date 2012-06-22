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

import lfapi.v2.schema.Policy;
import lfapi.v2.services.LiquidFeedbackServiceFactory;
import lfapi.v2.services.LiquidFeedbackService.PolicyService;
import liqui.droid.db.DB;
import liqui.droid.db.DBProvider;

public class SyncPolicy extends SyncAbstractTask {

    public SyncPolicy(Context ctx, Intent intent, LiquidFeedbackServiceFactory factory,
            String databaseName) {

        super(ctx, intent, factory, databaseName, DB.Policy.TABLE, SYNC_TIME_HOUR_12);
    }

    public void sync(Context ctx, String ids) {
        PolicyService ps = mFactory.createPolicyService();
        
        // int page = 0;
        boolean hasMore = true;
        while(hasMore) {
            Policy.Options po = new Policy.Options();
            // po.limit = LIMIT;
            // po.offset = ((page++) * po.limit);
            hasMore = false;
            
            List<Policy> l = ps.getPolicy(po);

            ContentValues[] v = new ContentValues[l.size()];
            int idx = 0;
            for (Policy p : l) {
                ContentValues values = new ContentValues();
                values.put(DB.Policy.COLUMN_ID, p.id);
                values.put(DB.Policy.COLUMN_ACTIVE, p.active);
                values.put(DB.Policy.COLUMN_INDEX, p.index);
                values.put(DB.Policy.COLUMN_NAME, p.name);
                values.put(DB.Policy.COLUMN_DESCRIPTION, p.description);
                values.put(DB.Policy.COLUMN_ADMISSION_TIME, p.admissionTime != null ? p.admissionTime.toString() : null);
                values.put(DB.Policy.COLUMN_DISCUSSION_TIME, p.discussionTime != null ? p.discussionTime.toString() : null);
                values.put(DB.Policy.COLUMN_VERIFICATION_TIME, p.verificationTime != null ? p.verificationTime.toString() : null);
                values.put(DB.Policy.COLUMN_VOTING_TIME, p.votingTime != null ? p.votingTime.toString() : null);
                values.put(DB.Policy.COLUMN_ISSUE_QUORUM_NUM, p.issueQuorumNum);
                values.put(DB.Policy.COLUMN_ISSUE_QUORUM_DEN, p.issueQuorumDen);
                values.put(DB.Policy.COLUMN_INITIATIVE_QUORUM_NUM, p.initiativeQuorumNum);
                values.put(DB.Policy.COLUMN_INITIATIVE_QUORUM_DEN, p.initiativeQuorumDen);
                values.put(DB.Policy.COLUMN_DIRECT_MAJORITY_STRICT, p.directMajorityStrict);
                values.put(DB.Policy.COLUMN_DIRECT_MAJORITY_POSITIVE, p.directMajorityPositive);
                values.put(DB.Policy.COLUMN_DIRECT_MAJORITY_NEGATIVE, p.directMajorityNegative);
                values.put(DB.Policy.COLUMN_INDIRECT_MAJORITY_NUM, p.indirectMajorityNum);
                values.put(DB.Policy.COLUMN_INDIRECT_MAJORITY_DEN, p.indirectMajorityDen);
                values.put(DB.Policy.COLUMN_INDIRECT_MAJORITY_STRICT, p.indirectMajorityStrict);
                values.put(DB.Policy.COLUMN_INDIRECT_MAJORITY_POSITIVE, p.indirectMajorityPositive);
                values.put(DB.Policy.COLUMN_INDIRECT_MAJORITY_NEGATIVE, p.indirectMajorityNegative);
                values.put(DB.Policy.COLUMN_NO_REVERSE_BEATPATH, p.noReverseBeatpath);
                values.put(DB.Policy.COLUMN_NO_MULTISTAGE_MAJORITY, p.noMultistageMajority);

                // System.out.println("admissionTime " + p.admissionTime);

                v[idx++] = values;
            }
            
            ctx.getContentResolver().bulkInsert(dbUri(DBProvider.POLICY_CONTENT_URI), v);
        }
    }
}