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

package liqui.droid.db;

import android.database.Cursor;

import org.joda.time.DateTime;

import lfapi.v2.schema.Area;
import lfapi.v2.schema.Delegation;
import lfapi.v2.schema.Draft;
import lfapi.v2.schema.Event;
import lfapi.v2.schema.Initiative;
import lfapi.v2.schema.Interval;
import lfapi.v2.schema.Issue;
import lfapi.v2.schema.Member;
import lfapi.v2.schema.Opinion;
import lfapi.v2.schema.Policy;
import lfapi.v2.schema.Privilege;
import lfapi.v2.schema.Suggestion;
import lfapi.v2.schema.Unit;

public class DB2Schema {
    
    public static Area fillArea(Cursor c, String prefix) {
        Area a = new Area();
        
        a.id                = getInt(c, prefix + DB.Area.COLUMN_ID);
        a.unitId            = getInt(c, prefix + DB.Area.COLUMN_UNIT_ID);
        a.active            = getBoo(c, prefix + DB.Area.COLUMN_ACTIVE); 
        a.name              = getStr(c, prefix + DB.Area.COLUMN_NAME);
        a.description       = getStr(c, prefix + DB.Area.COLUMN_DESCRIPTION);
        a.directMemberCount = getInt(c, prefix + DB.Area.COLUMN_DIRECT_MEMBER_COUNT);
        a.memberWeight      = getInt(c, prefix + DB.Area.COLUMN_MEMBER_WEIGHT);
        
        return a;
    }
    
    public static Area.Membership fillAreaMembership(Cursor c) {
        Area.Membership am = new Area.Membership();
        
        am.areaId            = getInt(c, DB.Membership.COLUMN_AREA_ID);
        am.memberId          = getInt(c, DB.Membership.COLUMN_MEMBER_ID);
        
        return am;
    }
    
    public static Delegation fillDelegation(Cursor c) {
        Delegation d = new Delegation();
        
        d.unitId =    getInt(c, "unit_id");
        d.areaId =    getInt(c, "area_id");
        d.issueId =   getInt(c, "issue_id");
        d.scope =     Delegation.Scope.valueOf(getStr(c, "scope"));
        d.trusterId = getInt(c, "truster_id");
        d.trusteeId = getInt(c, "trustee_id");
        
        return d;
    }
    
    public static Draft fillDraft(Cursor c) {
        Draft d = new Draft();
        
        d.areaId                  = getInt(c, "area_id");
        d.issueId                 = getInt(c, "issue_id");
        d.initiativeId            = getInt(c, "initiative_id");
        d.policyId                = getInt(c, "policy_id");
        d.initiativeName          = getStr(c, "initiative_name");
        d.initiativeDiscussionUrl = getStr(c, "initiative_discussion_url");
        d.formattingEngine        = getStr(c, "formatting_engine");
        d.content                 = getStr(c, "content");
        
        return d;
    }
    
    public static Event fillEvent(Cursor c) {
        Event e = new Event();
        
        e.id           = getInt(c, "_id");
        e.occurrence   = new DateTime(Long.parseLong(getStr(c, "occurrence")));
        e.event        = Event.Type.valueOf(getStr(c, "event"));
        e.memberId     = getInt(c, "member_id");
        e.issueId      = getInt(c, "issue_id");
        e.draftId      = getInt(c, "draft_id");
                
        return e;
    }
    
    public static Initiative fillInitiative(Cursor c, String prefix) {
        Initiative i = new Initiative();
        
        i.id                         = getInt(c, prefix + "_id");
        i.issueId                    = getInt(c, prefix + "issue_id");
        i.name                       = getStr(c, prefix + "name");
        i.discussionUrl              = getStr(c, prefix + "discussion_url");
        i.created                    = getDat(c, prefix + "created");
        i.revoked                    = getDat(c, prefix + "revoked");
        i.revokedByMemberId          = getInt(c, prefix + "revoked_by_member_id");
        i.suggestedInitiativeId      = getInt(c, prefix + "suggested_initiative_id");
        i.admitted                   = getBoo(c, prefix + "admitted");
        i.supporterCount             = getInt(c, prefix + "supporter_count");
        i.informedSupporterCount     = getInt(c, prefix + "informed_supporter_count");
        i.satisfiedSupporterCount    = getInt(c, prefix + "satisfied_supporter_count");
        i.satisfiedInformedSupporterCount = getInt(c, prefix + "satisfied_informed_supporter_count");
        i.positiveVotes              = getInt(c, prefix + "positive_votes");
        i.negativeVotes              = getInt(c, prefix + "negative_votes");
        i.directMajority             = getBoo(c, prefix + "direct_majority");
        i.indirectMajority           = getBoo(c, prefix + "indirect_majority");
        i.schulzeRank                = getInt(c, prefix + "schulze_rank");
        i.betterThanStatusQuo        = getBoo(c, prefix + "better_than_status_quo");
        i.worseThanStatusQuo         = getBoo(c, prefix + "worse_than_status_quo");
        i.reverseBeatPath            = getBoo(c, prefix + "reverse_beat_path");
        i.multistageMajority         = getBoo(c, prefix + "multistage_majority");
        i.eligible                   = getBoo(c, prefix + "elegible");
        i.winner                     = getBoo(c, prefix + "winner");
        i.rank                       = getInt(c, prefix + "rank");
        
        return i;
    }
    
    public static Issue fillIssue(Cursor c, String prefix) {
        Issue i = new Issue();
        
        i.id                   = getInt(c, prefix + "_id");
        i.areaId               = getInt(c, prefix + "area_id");
        i.policyId             = getInt(c, prefix + "policy_id");
        i.state                = Issue.State.valueOf(getStr(c, prefix + "state"));
        i.created              = getDat(c, prefix + "created");
        i.accepted             = getDat(c, prefix + "accepted");
        i.halfFrozen           = getDat(c, prefix + "half_frozen");
        i.fullyFrozen          = getDat(c, prefix + "fully_frozen");
        i.closed               = getDat(c, prefix + "closed");
        i.ranksAvailable       = getBoo(c, prefix + "ranks_available");
        i.cleaned              = getDat(c, prefix + "cleaned");
        i.admissionTime        = new Interval(getStr(c, prefix + "admission_time"));
        i.discussionTime       = new Interval(getStr(c, prefix + "discussion_time"));
        i.votingTime           = new Interval(getStr(c, prefix + "voting_time"));
        i.snapshot             = getDat(c, prefix + "snapshot");
        i.latestSnapshotEvent  = getStr(c, prefix + "latest_snapshot_event");
        i.population           = getInt(c, prefix + "population");
        i.voterCount           = getInt(c, prefix + "voter_count");
        i.statusQuoSchulzeRank = getInt(c, prefix + "status_quo_schulze_rank");
        
        return i;
    }
    
    public static Opinion fillOpinion(Cursor c) {
        Opinion o = new Opinion();
        
        o.suggestionId = getInt(c, "suggestion_id");
        o.memberId     = getInt(c, "member_id");
        o.degree       = getInt(c, "degree");
        o.fulfilled    = getBoo(c, "fulfilled");
        
        return o;
    }
    
    public static Policy fillPolicy(Cursor c) {
        Policy p = new Policy();
        
        p.active                   = getBoo(c, "active");
        p.index                    = getStr(c, "idx");
        p.name                     = getStr(c, "name");
        p.description              = getStr(c, "description");
        p.admissionTime            = new Interval(getStr(c, "admission_time"));
        p.discussionTime           = new Interval(getStr(c, "discussion_time"));
        p.verificationTime         = new Interval(getStr(c, "verification_time"));
        p.votingTime               = new Interval(getStr(c, "voting_time"));
        p.issueQuorumNum           = getInt(c, "issue_quorum_num");
        p.issueQuorumDen           = getInt(c, "issue_quorum_den");
        p.initiativeQuorumNum      = getInt(c, "initiative_quorum_num");
        p.initiativeQuorumDen      = getInt(c, "initiative_quorum_den");
        p.directMajorityStrict     = getBoo(c, "direct_majority_strict");
        p.directMajorityPositive   = getInt(c, "direct_majority_strict");
        p.directMajorityNegative   = getInt(c, "direct_majority_negative");
        p.indirectMajorityNum      = getInt(c, "indirect_majority_num");
        p.indirectMajorityDen      = getInt(c, "indirect-majority-den");
        p.indirectMajorityStrict   = getBoo(c, "indirect_majority_strict");
        p.indirectMajorityPositive = getInt(c, "indirect_majority_positive");
        p.indirectMajorityNegative = getInt(c, "indirect_majority_negative");
        p.noReverseBeatpath        = getBoo(c, "no_reverse_beatpath");
        p.noMultistageMajority     = getBoo(c, "no_multistage_majority");
        
        return p;
    }
    
    public static Privilege fillPrivilege(Cursor c) {
        Privilege p = new Privilege();
        
        p.unitId             = getInt(c, "unit_id");
        p.memberId           = getInt(c, "member_id");
        p.adminManager       = getBoo(c, "admin_manager");
        p.unitManager        = getBoo(c, "unit_manager");
        p.areaManager        = getBoo(c, "area_manager");
        p.votingRightManager = getBoo(c, "voting_right_manager");
        p.votingRight        = getBoo(c, "voing_right");
        
        return p;
    }
    
    public static Suggestion fillSuggestion(Cursor c) {
        Suggestion s = new Suggestion();
        
        s.initiativeId           = getInt(c, "initiative_id");
        s.id                     = getInt(c, "_id");
        s.created                = getDat(c, "created");
        s.authorId               = getInt(c, "author_id");
        s.name                   = getStr(c, "name");
        s.formattingEngine       = getStr(c, "formatting_engine");
        s.content                = getStr(c, "content");
        s.minus2UnfulfilledCount = getInt(c, "minus2_unfulfilled_count");
        s.minus1UnfulfilledCount = getInt(c, "minus1_unfulfilled_count");
        s.minus1FulfilledCount   = getInt(c, "minus1_fulfilled_count");
        s.plus1UnfulfilledCount  = getInt(c, "plus1_unfulfilled_count");
        s.plus1FulfilledCount    = getInt(c, "plus1_fulfilled_count");
        s.plus2UnfulfilledCount  = getInt(c, "plus2_unfulfilled_count");
        s.plus2FulfilledCount    = getInt(c, "plus2_fulfilled_count");
        
        return s;
    }
    
    public static Member fillMember(Cursor c) {
        Member m = new Member();
        
        m.id                    = getInt(c, "_id");
        m.name                  = getStr(c, "name");
        m.identification        = getStr(c, "identification");
        m.organizationalUnit    = getStr(c, "organizational_unit");
        m.internalPosts         = getStr(c, "internal_posts");
        m.realName              = getStr(c, "real_name");
        m.birthday              = getDat(c, "birthday");
        m.address               = getStr(c, "address");
        m.eMail                 = getStr(c, "email");
        m.xmppAddress           = getStr(c, "xmpp_address");
        m.website               = getStr(c, "website");
        m.phone                 = getStr(c, "phone");
        m.mobilePhone           = getStr(c, "mobile_phone");
        m.profession            = getStr(c, "profession");
        m.externalMemberships   = getStr(c, "external_memberships");
        m.externalPosts         = getStr(c, "external_posts");
        m.formattingEngine      = getStr(c, "formatting_engine");
        m.statement             = getStr(c, "statement");
        m.active                = getBoo(c, "active");
        m.locked                = getBoo(c, "locked");
        
        return m;
    }
    
    public static Member.Image fillMemberImage(Cursor c) {
        Member.Image m = new Member.Image();
        
        m.memberId              = getInt(c, "member_id");
        m.imageType             = getStr(c, "image_type");
        m.scaled                = getBoo(c, "scaled");
        m.contentType           = getStr(c, "content_type");
        m.data                  = getStr(c, "data");
        
        return m;
    }
    
    public static Unit fillUnit(Cursor c) {
        Unit u = new Unit();
        
        u.id           = getInt(c, "_id");
        u.parentId     = getInt(c, "parent_id");
        u.active       = getBoo(c, "active");
        u.name         = getStr(c, "name");
        u.description  = getStr(c, "description");
        u.memberCount  = getInt(c, "member_count");
        
        return u;
    }
    
    public static int getInt(Cursor c, String columnName) {
        int idx = c.getColumnIndex(columnName);
        return idx != -1 ? (int) c.getInt(idx) : null;
    }
    
    public static String getStr(Cursor c, String columnName) {
        int idx = c.getColumnIndex(columnName);
        return c.getString(idx);
    }
    
    public static Boolean getBoo(Cursor c, String columnName) {
        int idx = c.getColumnIndex(columnName);
        return idx != -1 ? (getInt(c, columnName) == 1 ? true : false) : null; 
    }
    
    public static DateTime getDat(Cursor c, String columnName) {
        String str = getStr(c, columnName);
        return str != null ? new DateTime(Long.parseLong(str)) : null;
    }
}
