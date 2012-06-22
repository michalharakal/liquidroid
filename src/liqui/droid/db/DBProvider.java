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

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.database.sqlite.SQLiteStatement;
import android.net.Uri;

import java.util.HashMap;

import liqui.droid.util.StringUtils;

public class DBProvider extends ContentProvider {
    
    public static final String TAG = "DBProvider";
    
    protected Context mContext;
    
    protected HashMap<String,DB> mDb;
    
    public static final String AUTHORITY = "liqui.droid.db";
    
    public DBProvider() {
        super();
        
        mDb = new HashMap<String, DB>();
    }
    
    // updated
    private static final int UPDATED = 30000;
    private static final int UPDATED_NAME = 40000;
    private static final String UPDATED_PATH = "updateds";
    
    public static final Uri    UPDATED_CONTENT_URI  = Uri.parse("content://" + AUTHORITY + "/" + UPDATED_PATH);
    public static final String UPDATED_CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/updateds";
    public static final String UPDATED_CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/updated";

    // area
    private static final int AREA = 50000;
    private static final int AREA_ACTIVE = 60000;
    private static final int AREA_LIST_BY_UNIT = 70000;
    private static final int AREA_ID = 80000;
    private static final String AREA_PATH = "areas";
    
    public static final Uri    AREA_CONTENT_URI  = Uri.parse("content://" + AUTHORITY + "/" + AREA_PATH);
    public static final String AREA_CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/areas";
    public static final String AREA_CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/area";

    // battle
    private static final int BATTLE = 90000;
    private static final int BATTLE_ID = 100000;
    private static final String BATTLE_PATH = "battles";
    
    public static final Uri    BATTLE_CONTENT_URI  = Uri.parse("content://" + AUTHORITY + "/" + BATTLE_PATH);
    public static final String BATTLE_CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/battles";
    public static final String BATTLE_CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/battle";

    // issue comment
    private static final int ISSUE_COMMENT = 110000;
    private static final int ISSUE_COMMENT_ID = 120000;
    private static final String ISSUE_COMMENT_PATH = "issue_comments";
    
    public static final Uri    ISSUE_COMMENT_CONTENT_URI  = Uri.parse("content://" + AUTHORITY + "/" + ISSUE_COMMENT_PATH);
    public static final String ISSUE_COMMENT_CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/issue_comments";
    public static final String ISSUE_COMMENT_CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/issue_comment";

    // contact
    private static final int CONTACT = 1300000;
    private static final int CONTACT_ID = 140000;
    private static final String CONTACT_PATH = "contacts";
    
    public static final Uri    CONTACT_CONTENT_URI  = Uri.parse("content://" + AUTHORITY + "/" + CONTACT_PATH);
    public static final String CONTACT_CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/contacts";
    public static final String CONTACT_CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/contact";

    // delegation
    private static final int DELEGATION = 150000;
    private static final int DELEGATION_ID = 160000;
    private static final String DELEGATION_PATH = "delegations";
    
    public static final Uri    DELEGATION_CONTENT_URI  = Uri.parse("content://" + AUTHORITY + "/" + DELEGATION_PATH);
    public static final String DELEGATION_CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/delegations";
    public static final String DELEGATION_CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/delegation";

    // draft
    private static final int DRAFT = 170000;
    private static final int DRAFT_AREA_ID = 180000;
    private static final String DRAFT_PATH = "drafts";
    
    public static final Uri    DRAFT_CONTENT_URI  = Uri.parse("content://" + AUTHORITY + "/" + DRAFT_PATH);
    public static final String DRAFT_CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/drafts";
    public static final String DRAFT_CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/draft";

    // event
    private static final int EVENT = 190000;
    private static final int EVENT_ID = 200000;
    private static final String EVENT_PATH = "events";
    
    public static final Uri    EVENT_CONTENT_URI  = Uri.parse("content://" + AUTHORITY + "/" + EVENT_PATH);
    public static final String EVENT_CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/events";
    public static final String EVENT_CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/event";

    // initiative
    private static final int INITIATIVE = 210000;
    private static final int INIS = 220000;
    private static final int INITIATIVE_ID = 230000;
    private static final String INITIATIVE_PATH = "initiatives";
    
    public static final Uri    INITIATIVE_CONTENT_URI  = Uri.parse("content://" + AUTHORITY + "/" + INITIATIVE_PATH);
    public static final String INITIATIVE_CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/initiatives";
    public static final String INITIATIVE_CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/initiative";
    
    // initiator
    private static final int INITIATOR = 240000;
    private static final int INITIATOR_ID = 250000;
    private static final String INITIATOR_PATH = "initiators";
    
    public static final Uri    INITIATOR_CONTENT_URI  = Uri.parse("content://" + AUTHORITY + "/" + INITIATOR_PATH);
    public static final String INITIATOR_CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/initiators";
    public static final String INITIATOR_CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/initiator";
    
    // interest
    private static final int INTEREST = 260000;
    private static final int INTEREST_ID = 270000;
    private static final String INTEREST_PATH = "interests";

    public static final Uri    INTEREST_CONTENT_URI  = Uri.parse("content://" + AUTHORITY + "/" + INTEREST_PATH);
    public static final String INTEREST_CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/interests";
    public static final String INTEREST_CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/interest";

    // issue
    private static final int ISSUE = 280000;
    private static final int ISSUE_PURE = 281000;
    private static final int ISSUE_LATEST         = 290000;
    private static final int ISSUE_LATEST_OPEN    = 300000;
    private static final int ISSUE_LATEST_CLOSED  = 310000;
    private static final int ISSUE_BY_AREA        = 320000;
    private static final int ISSUE_BY_AREA_OPEN   = 330000;
    private static final int ISSUE_BY_AREA_CLOSED = 340000;
    private static final int ISSUE_ID = 350000;
    private static final String ISSUE_PATH = "issues";
    private static final String ISSUE_PURE_PATH = "issues_pure";
    
    public static final Uri    ISSUE_CONTENT_URI  = Uri.parse("content://" + AUTHORITY + "/" + ISSUE_PATH);
    public static final Uri    ISSUE_PURE_CONTENT_URI  = Uri.parse("content://" + AUTHORITY + "/" + "issues_pure");
    public static final String ISSUE_CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/issues";
    public static final String ISSUE_CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/issue";

    // opinion
    private static final int OPINION = 360000;
    private static final int OPINION_ID = 370000;
    private static final String OPINION_PATH = "opinions";
    
    public static final Uri    OPINION_CONTENT_URI  = Uri.parse("content://" + AUTHORITY + "/" + OPINION_PATH);
    public static final String OPINION_CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/opinions";
    public static final String OPINION_CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/opinion";

    // policy
    private static final int POLICY = 380000;
    private static final int POLICY_ID = 390000;
    private static final String POLICY_PATH = "policys";
    
    public static final Uri    POLICY_CONTENT_URI  = Uri.parse("content://" + AUTHORITY + "/" + POLICY_PATH);
    public static final String POLICY_CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/policys";
    public static final String POLICY_CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/policy";

    // privilege
    private static final int PRIVILEGE = 400000;
    private static final int PRIVILEGE_ID = 410000;
    private static final String PRIVILEGE_PATH = "privileges";
    
    public static final Uri    PRIVILEGE_CONTENT_URI  = Uri.parse("content://" + AUTHORITY + "/" + PRIVILEGE_PATH);
    public static final String PRIVILEGE_CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/privileges";
    public static final String PRIVILEGE_CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/privilege";

    // suggestion
    private static final int SUGGESTION = 420000;
    private static final int SUGGESTION_ID = 430000;
    private static final String SUGGESTION_PATH = "suggestions";
    
    public static final Uri    SUGGESTION_CONTENT_URI  = Uri.parse("content://" + AUTHORITY + "/" + SUGGESTION_PATH);
    public static final String SUGGESTION_CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/suggestions";
    public static final String SUGGESTION_CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/suggestion";

    // supporter
    private static final int SUPPORTER = 440000;
    private static final int SUPPORTER_ID = 450000;
    private static final String SUPPORTER_PATH = "supporters";
    
    public static final Uri    SUPPORTER_CONTENT_URI  = Uri.parse("content://" + AUTHORITY + "/" + SUPPORTER_PATH);
    public static final String SUPPORTER_CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/supporters";
    public static final String SUPPORTER_CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/supporter";

    // member
    private static final int MEMBER = 460000;
    private static final int MEMBER_ID = 470000;
    private static final String MEMBER_PATH = "members";
    
    public static final Uri    MEMBER_CONTENT_URI  = Uri.parse("content://" + AUTHORITY + "/" + MEMBER_PATH);
    public static final String MEMBER_CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/members";
    public static final String MEMBER_CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/member";

    // member image
    private static final int MEMBER_IMAGE = 480000;
    private static final int MEMBER_IMAGE_ID = 490000;
    private static final String MEMBER_IMAGE_PATH = "member_images";
    
    public static final Uri    MEMBER_IMAGE_CONTENT_URI  = Uri.parse("content://" + AUTHORITY + "/" + MEMBER_IMAGE_PATH);
    public static final String MEMBER_IMAGE_CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/member_images";
    public static final String MEMBER_IMAGE_CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/member_image";

    // membership
    private static final int MEMBERSHIP = 500000;
    private static final int MEMBERSHIP_ID = 510000;
    private static final String MEMBERSHIP_PATH = "memberships";
    
    public static final Uri    MEMBERSHIP_CONTENT_URI  = Uri.parse("content://" + AUTHORITY + "/" + MEMBERSHIP_PATH);
    public static final String MEMBERSHIP_CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/memberships";
    public static final String MEMBERSHIP_CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/membership";

    // unit
    private static final int UNIT = 520000;
    private static final int UNIT_LIST = 530000;
    private static final int UNIT_ID = 540000;
    private static final String UNIT_PATH = "units";
    
    public static final Uri    UNIT_CONTENT_URI  = Uri.parse("content://" + AUTHORITY + "/" + UNIT_PATH);
    public static final String UNIT_CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/units";
    public static final String UNIT_CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/unit";

    // vote
    private static final int VOTE = 550000;
    private static final int VOTE_ID = 560000;
    private static final String VOTE_PATH = "votes";

    public static final Uri    VOTE_CONTENT_URI  = Uri.parse("content://" + AUTHORITY + "/" + VOTE_PATH);
    public static final String VOTE_CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/votes";
    public static final String VOTE_CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vote";

    // vote comment
    private static final int VOTE_COMMENT = 565000;
    private static final int VOTE_COMMENT_ID = 567000;
    private static final String VOTE_COMMENT_PATH = "vote_comments";
    
    public static final Uri    VOTE_COMMENT_CONTENT_URI  = Uri.parse("content://" + AUTHORITY + "/" + VOTE_COMMENT_PATH);
    public static final String VOTE_COMMENT_CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vote_comments";
    public static final String VOTE_COMMENT_CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vote_comment";

    // voter
    private static final int VOTER = 570000;
    private static final int VOTER_ID = 580000;
    private static final String VOTER_PATH = "voters";

    public static final Uri    VOTER_CONTENT_URI  = Uri.parse("content://" + AUTHORITY + "/" + VOTER_PATH);
    public static final String VOTER_CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/voters";
    public static final String VOTER_CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/voter";

    // delegating voter
    private static final int DELEGATING_VOTER = 590000;
    private static final int DELEGATING_VOTER_ID = 600000;
    private static final String DELEGATING_VOTER_PATH = "delegating_voters";

    public static final Uri    DELEGATING_VOTER_CONTENT_URI  = Uri.parse("content://" + AUTHORITY + "/" + DELEGATING_VOTER_PATH);
    public static final String DELEGATING_VOTER_CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/delegating_voters";
    public static final String DELEGATING_VOTER_CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/delegating_voter";

    private static final UriMatcher URIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    
    static {
        URIMatcher.addURI(AUTHORITY, UPDATED_PATH,        UPDATED);
        URIMatcher.addURI(AUTHORITY, UPDATED_PATH + "/#", UPDATED_NAME);
        
        URIMatcher.addURI(AUTHORITY, BATTLE_PATH,            BATTLE);
        URIMatcher.addURI(AUTHORITY, BATTLE_PATH + "/#",     BATTLE_ID);

        URIMatcher.addURI(AUTHORITY, ISSUE_COMMENT_PATH,            ISSUE_COMMENT);
        URIMatcher.addURI(AUTHORITY, ISSUE_COMMENT_PATH + "/#",     ISSUE_COMMENT_ID);

        URIMatcher.addURI(AUTHORITY, CONTACT_PATH,            CONTACT);
        URIMatcher.addURI(AUTHORITY, CONTACT_PATH + "/#",     CONTACT_ID);

        URIMatcher.addURI(AUTHORITY, AREA_PATH,              AREA);
        URIMatcher.addURI(AUTHORITY, AREA_PATH + "/active",  AREA_ACTIVE );
        URIMatcher.addURI(AUTHORITY, AREA_PATH + "/active/by_unit/#",  AREA_LIST_BY_UNIT );
        URIMatcher.addURI(AUTHORITY, AREA_PATH + "/#",       AREA_ID);

        URIMatcher.addURI(AUTHORITY, DELEGATION_PATH,        DELEGATION);
        URIMatcher.addURI(AUTHORITY, DELEGATION_PATH + "/#", DELEGATION_ID);

        URIMatcher.addURI(AUTHORITY, DRAFT_PATH,        DRAFT);
        URIMatcher.addURI(AUTHORITY, DRAFT_PATH + "/#", DRAFT_AREA_ID);

        URIMatcher.addURI(AUTHORITY, EVENT_PATH,        EVENT);
        URIMatcher.addURI(AUTHORITY, EVENT_PATH + "/#", EVENT_ID);

        URIMatcher.addURI(AUTHORITY, INITIATIVE_PATH,        INITIATIVE);
        URIMatcher.addURI(AUTHORITY, INITIATIVE_PATH + "/by_issue/#", INIS);
        URIMatcher.addURI(AUTHORITY, INITIATIVE_PATH + "/#", INITIATIVE_ID);

        URIMatcher.addURI(AUTHORITY, INITIATOR_PATH,        INITIATOR);
        URIMatcher.addURI(AUTHORITY, INITIATOR_PATH + "/#", INITIATOR_ID);

        URIMatcher.addURI(AUTHORITY, INTEREST_PATH,        INTEREST);
        URIMatcher.addURI(AUTHORITY, INTEREST_PATH + "/#", INTEREST_ID);

        URIMatcher.addURI(AUTHORITY, ISSUE_PATH,                       ISSUE);
        URIMatcher.addURI(AUTHORITY, ISSUE_PURE_PATH,                  ISSUE_PURE);
        URIMatcher.addURI(AUTHORITY, ISSUE_PATH + "/latest",           ISSUE_LATEST);
        URIMatcher.addURI(AUTHORITY, ISSUE_PATH + "/latest/open"  ,    ISSUE_LATEST_OPEN);
        URIMatcher.addURI(AUTHORITY, ISSUE_PATH + "/latest/closed",    ISSUE_LATEST_CLOSED);
        URIMatcher.addURI(AUTHORITY, ISSUE_PATH + "/by_area/#",        ISSUE_BY_AREA);
        URIMatcher.addURI(AUTHORITY, ISSUE_PATH + "/by_area/open/#"  , ISSUE_BY_AREA_OPEN);
        URIMatcher.addURI(AUTHORITY, ISSUE_PATH + "/by_area/closed/#", ISSUE_BY_AREA_CLOSED);
        URIMatcher.addURI(AUTHORITY, ISSUE_PATH + "/#", ISSUE_ID);

        URIMatcher.addURI(AUTHORITY, OPINION_PATH,        OPINION);
        URIMatcher.addURI(AUTHORITY, OPINION_PATH + "/#", OPINION_ID);

        URIMatcher.addURI(AUTHORITY, POLICY_PATH,        POLICY);
        URIMatcher.addURI(AUTHORITY, POLICY_PATH + "/#", POLICY_ID);

        URIMatcher.addURI(AUTHORITY, PRIVILEGE_PATH,        PRIVILEGE);
        URIMatcher.addURI(AUTHORITY, PRIVILEGE_PATH + "/#", PRIVILEGE_ID);

        URIMatcher.addURI(AUTHORITY, SUGGESTION_PATH,        SUGGESTION);
        URIMatcher.addURI(AUTHORITY, SUGGESTION_PATH + "/#", SUGGESTION_ID);

        URIMatcher.addURI(AUTHORITY, SUPPORTER_PATH,        SUPPORTER);
        URIMatcher.addURI(AUTHORITY, SUPPORTER_PATH + "/#", SUPPORTER_ID);

        URIMatcher.addURI(AUTHORITY, MEMBER_PATH,        MEMBER);
        URIMatcher.addURI(AUTHORITY, MEMBER_PATH + "/#", MEMBER_ID);

        URIMatcher.addURI(AUTHORITY, MEMBER_IMAGE_PATH,        MEMBER_IMAGE);
        URIMatcher.addURI(AUTHORITY, MEMBER_IMAGE_PATH + "/#", MEMBER_IMAGE_ID);

        URIMatcher.addURI(AUTHORITY, MEMBERSHIP_PATH,        MEMBERSHIP);
        URIMatcher.addURI(AUTHORITY, MEMBERSHIP_PATH + "/#", MEMBERSHIP_ID);

        URIMatcher.addURI(AUTHORITY, UNIT_PATH,        UNIT);
        URIMatcher.addURI(AUTHORITY, UNIT_PATH   + "/active", UNIT_LIST);
        URIMatcher.addURI(AUTHORITY, UNIT_PATH + "/#", UNIT_ID);

        URIMatcher.addURI(AUTHORITY, VOTE_PATH,        VOTE);
        URIMatcher.addURI(AUTHORITY, VOTE_PATH + "/#", VOTE_ID);

        URIMatcher.addURI(AUTHORITY, VOTE_COMMENT_PATH,        VOTE_COMMENT);
        URIMatcher.addURI(AUTHORITY, VOTE_COMMENT_PATH + "/#", VOTE_COMMENT_ID);

        URIMatcher.addURI(AUTHORITY, VOTER_PATH,        VOTER);
        URIMatcher.addURI(AUTHORITY, VOTER_PATH + "/#", VOTER_ID);

        URIMatcher.addURI(AUTHORITY, DELEGATING_VOTER_PATH,        DELEGATING_VOTER);
        URIMatcher.addURI(AUTHORITY, DELEGATING_VOTER_PATH + "/#", DELEGATING_VOTER_ID);
    }

    @Override
    public int delete(Uri uri, String whereClause, String[] whereArgs) {
        int uriType = URIMatcher.match(uri);
        
        String database = uri.getQueryParameter("db");
        
        // Log.d("XXXXXXXXXX", "delete from database: " + uri);
        
        if (database == null || database.length() == 0) {
            throw new IllegalArgumentException("Unknown Database: " + uri);
        }
        
        if (!mDb.containsKey(database)) {
            mDb.put(database, new DB(mContext, database));
        }
        
        SQLiteDatabase sqlDB = mDb.get(database).getWritableDatabase();

        int nr = 0;
        switch (uriType) {
            case AREA:
                nr = sqlDB.delete(DB.Area.TABLE, whereClause, whereArgs);
                break;
            case BATTLE:
                nr = sqlDB.delete(DB.Initiative.Battle.TABLE, whereClause, whereArgs);
                break;
            case CONTACT:
                String memberId = uri.getQueryParameter("member_id");
                String otherMemberId = uri.getQueryParameter("other_member_id");

                if (memberId == null && otherMemberId == null) {
                    nr = sqlDB.delete("contact", whereClause, whereArgs);
                } else {
                    nr = sqlDB.delete("contact", "member_id = ? AND other_member_id = ?",
                            new String[] { memberId, otherMemberId });
                }
                break;
            case ISSUE_COMMENT:
                nr = sqlDB.delete(DB.Issue.Comment.TABLE, whereClause, whereArgs);
                break;
            case DELEGATION:
                nr = sqlDB.delete(DB.Delegation.TABLE, whereClause, whereArgs);
                break;
            case DRAFT:
                nr = sqlDB.delete(DB.Draft.TABLE, whereClause, whereArgs);
                break;
            case EVENT:
                nr = sqlDB.delete(DB.Event.TABLE, whereClause, whereArgs);
                break;
            case INITIATIVE:
                nr = sqlDB.delete(DB.Initiative.TABLE, whereClause, whereArgs);
                break;
            case INITIATOR:
                nr = sqlDB.delete(DB.Initiative.Initiator.TABLE, whereClause, whereArgs);
                break;
            case INTEREST:
                nr = sqlDB.delete(DB.Issue.Interest.TABLE, whereClause, whereArgs);
                break;
            case ISSUE:
                nr = sqlDB.delete(DB.Issue.TABLE, whereClause, whereArgs);
                break;
            case OPINION:
                nr = sqlDB.delete(DB.Opinion.TABLE, whereClause, whereArgs);
                break;
            case POLICY:
                nr = sqlDB.delete(DB.Policy.TABLE, whereClause, whereArgs);
                break;
            case PRIVILEGE:
                nr = sqlDB.delete(DB.Privilege.TABLE, whereClause, whereArgs);
                break;
            case SUGGESTION:
                nr = sqlDB.delete(DB.Suggestion.TABLE, whereClause, whereArgs);
                break;
            case SUPPORTER:
                nr = sqlDB.delete(DB.Initiative.Supporter.TABLE, whereClause, whereArgs);
                break;
            case MEMBER:
                nr = sqlDB.delete(DB.Member.TABLE, whereClause, whereArgs);
                break;
            case MEMBER_IMAGE:
                nr = sqlDB.delete(DB.Member.Image.TABLE, whereClause, whereArgs);
                break;
            case MEMBERSHIP:
                memberId = uri.getQueryParameter("member_id");
                otherMemberId = uri.getQueryParameter("other_member_id");
                
                if (memberId == null && otherMemberId == null) {
                    nr = sqlDB.delete("contact", whereClause, whereArgs);
                } else {
                    nr = sqlDB.delete("membership", "member_id = ? AND area_id = ?",
                            new String[] { memberId, otherMemberId });
                }
                break;
            case UNIT:
                nr = sqlDB.delete(DB.Unit.TABLE, whereClause, whereArgs);
                break;
            case VOTE:
                nr = sqlDB.delete(DB.Vote.TABLE, whereClause, whereArgs);
                break;
            case VOTE_COMMENT:
                nr = sqlDB.delete(DB.Vote.Comment.TABLE, whereClause, whereArgs);
                break;
            case VOTER:
                nr = sqlDB.delete(DB.Vote.Voter.TABLE, whereClause, whereArgs);
                break;
            case DELEGATING_VOTER:
                nr = sqlDB.delete(DB.Vote.DelegatingVoter.TABLE, whereClause, whereArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        
        getContext().getContentResolver().notifyChange(uri, null, false);

        return nr;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int uriType = URIMatcher.match(uri);
        
        String database = uri.getQueryParameter("db");
        
        // Log.d("XXXXXXXXXX", "insert into database: " + uri);
        
        if (database == null || database.length() == 0) {
            throw new IllegalArgumentException("Unknown Database: " + uri);
        }
        
        if (!mDb.containsKey(database)) {
            mDb.put(database, new DB(mContext, database));
        }
        
        SQLiteDatabase sqlDB = mDb.get(database).getWritableDatabase();
        
        // update meta_cached
        if (uriType != UPDATED && !values.containsKey("meta_cached")) { 
            values.put("meta_cached", System.currentTimeMillis());
        }
        
        long id = 0;
        switch (uriType) {
            case UPDATED:
                id = sqlDB.insertWithOnConflict(DB.Updated.TABLE, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                getContext().getContentResolver().notifyChange(uri, null, false);
                return Uri.parse(UPDATED_PATH + "/" + id);
            case AREA:
                id = sqlDB.insertWithOnConflict(DB.Area.TABLE, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                getContext().getContentResolver().notifyChange(uri, null, false);
                return Uri.parse(AREA_PATH + "/" + id);
            case BATTLE:
                id = sqlDB.insertWithOnConflict(DB.Initiative.Battle.TABLE, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                getContext().getContentResolver().notifyChange(uri, null, false);
                return Uri.parse(BATTLE_PATH + "/" + id);
            case ISSUE_COMMENT:
                id = sqlDB.insertWithOnConflict(DB.Issue.Comment.TABLE, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                getContext().getContentResolver().notifyChange(uri, null, false);
                return Uri.parse(ISSUE_COMMENT_PATH + "/" + id);
            case DELEGATION:
                id = sqlDB.insertWithOnConflict(DB.Delegation.TABLE, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                getContext().getContentResolver().notifyChange(uri, null, false);
                return Uri.parse(DELEGATION_PATH + "/" + id);
            case DRAFT:
                id = sqlDB.insertWithOnConflict(DB.Draft.TABLE, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                getContext().getContentResolver().notifyChange(uri, null, false);
                return Uri.parse(DRAFT_PATH + "/" + id);
            case EVENT:
                id = sqlDB.insertWithOnConflict(DB.Event.TABLE, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                getContext().getContentResolver().notifyChange(uri, null, false);
                return Uri.parse(EVENT_PATH + "/" + id);
            case INITIATIVE:
                id = sqlDB.insertWithOnConflict(DB.Initiative.TABLE, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                getContext().getContentResolver().notifyChange(uri, null, false);
                return Uri.parse(INITIATIVE_PATH + "/" + id);
            case INITIATOR:
                id = sqlDB.insertWithOnConflict(DB.Initiative.Initiator.TABLE, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                getContext().getContentResolver().notifyChange(uri, null, false);
                return Uri.parse(INITIATOR_PATH + "/" + id);
            case INTEREST:
                id = sqlDB.insertWithOnConflict(DB.Issue.Interest.TABLE, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                getContext().getContentResolver().notifyChange(uri, null, false);
                return Uri.parse(ISSUE_PATH + "/" + id);
            case ISSUE:
                id = sqlDB.insertWithOnConflict(DB.Issue.TABLE, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                getContext().getContentResolver().notifyChange(uri, null, false);
                return Uri.parse(ISSUE_PATH + "/" + id);
            case OPINION:
                id = sqlDB.insertWithOnConflict(DB.Opinion.TABLE, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                getContext().getContentResolver().notifyChange(uri, null, false);
                return Uri.parse(OPINION_PATH + "/" + id);
            case POLICY:
                id = sqlDB.insertWithOnConflict(DB.Policy.TABLE, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                getContext().getContentResolver().notifyChange(uri, null, false);
                return Uri.parse(OPINION_PATH + "/" + id);
            case PRIVILEGE:
                id = sqlDB.insertWithOnConflict(DB.Privilege.TABLE, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                getContext().getContentResolver().notifyChange(uri, null, false);
                return Uri.parse(PRIVILEGE_PATH + "/" + id);
            case SUGGESTION:
                id = sqlDB.insertWithOnConflict(DB.Suggestion.TABLE, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                getContext().getContentResolver().notifyChange(uri, null, false);
                return Uri.parse(SUGGESTION_PATH + "/" + id);
            case SUPPORTER:
                id = sqlDB.insertWithOnConflict(DB.Initiative.Supporter.TABLE, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                getContext().getContentResolver().notifyChange(uri, null, false);
                return Uri.parse(SUPPORTER_PATH + "/" + id);
            case MEMBER:
                id = sqlDB.insertWithOnConflict(DB.Member.TABLE, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                getContext().getContentResolver().notifyChange(uri, null, false);
                return Uri.parse(MEMBER_PATH + "/" + id);
            case MEMBER_IMAGE:
                id = sqlDB.insertWithOnConflict(DB.Member.Image.TABLE, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                getContext().getContentResolver().notifyChange(uri, null, false);
                return Uri.parse(MEMBER_IMAGE_PATH + "/" + id);
            case MEMBERSHIP:
                id = sqlDB.insertWithOnConflict(DB.Membership.TABLE, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                getContext().getContentResolver().notifyChange(uri, null, false);
                return Uri.parse(MEMBERSHIP_PATH + "/" + id);
            case UNIT:
                id = sqlDB.insertWithOnConflict(DB.Unit.TABLE, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                getContext().getContentResolver().notifyChange(uri, null, false);
                return Uri.parse(UNIT_PATH + "/" + id);
            case VOTE:
                id = sqlDB.insertWithOnConflict(DB.Vote.TABLE, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                getContext().getContentResolver().notifyChange(uri, null, false);
                return Uri.parse(VOTE_PATH + "/" + id);
            case VOTE_COMMENT:
                id = sqlDB.insertWithOnConflict(DB.Vote.Comment.TABLE, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                getContext().getContentResolver().notifyChange(uri, null, false);
                return Uri.parse(VOTE_COMMENT_PATH + "/" + id);
            case VOTER:
                id = sqlDB.insertWithOnConflict(DB.Vote.Voter.TABLE, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                getContext().getContentResolver().notifyChange(uri, null, false);
                return Uri.parse(VOTER_PATH + "/" + id);
            case DELEGATING_VOTER:
                id = sqlDB.insertWithOnConflict(DB.Vote.DelegatingVoter.TABLE, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                getContext().getContentResolver().notifyChange(uri, null, false);
                return Uri.parse(DELEGATING_VOTER_PATH + "/" + id);
        default:
            throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }
    
    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        int uriType = URIMatcher.match(uri);
        
        String database = uri.getQueryParameter("db");
        
        // Log.d("XXXXXXXXXX", "insert into database: " + uri);
        
        if (database == null || database.length() == 0) {
            throw new IllegalArgumentException("Unknown Database: " + uri);
        }
        
        if (!mDb.containsKey(database)) {
            mDb.put(database, new DB(mContext, database));
        }
        
        SQLiteDatabase sqlDB = mDb.get(database).getWritableDatabase();
        
        int numInserted = 0;

        switch(uriType) {
            case AREA:
                numInserted = bulkInsert(uri, sqlDB, DB.Area.TABLE, DB.Area.COLUMNS, values);
                break;
            case BATTLE:
                numInserted = bulkInsert(uri, sqlDB, DB.Initiative.Battle.TABLE, DB.Initiative.Battle.COLUMNS, values);
                break;
            case ISSUE_COMMENT:
                numInserted = bulkInsert(uri, sqlDB, DB.Issue.Comment.TABLE, DB.Issue.Comment.COLUMNS, values);
                break;
            case DELEGATION:
                numInserted = bulkInsert(uri, sqlDB, DB.Delegation.TABLE, DB.Delegation.COLUMNS, values);
                break;
            case DRAFT:
                numInserted = bulkInsert(uri, sqlDB, DB.Draft.TABLE, DB.Draft.COLUMNS, values);
                break;
            case EVENT:
                numInserted = bulkInsert(uri, sqlDB, DB.Event.TABLE, DB.Event.COLUMNS, values);
                break;
            case INITIATIVE:
                numInserted = bulkInsert(uri, sqlDB, DB.Initiative.TABLE, DB.Initiative.COLUMNS, values);
                break;
            case INITIATOR:
                numInserted = bulkInsert(uri, sqlDB, DB.Initiative.Initiator.TABLE, DB.Initiative.Initiator.COLUMNS, values);
                break;
            case INTEREST:
                numInserted = bulkInsert(uri, sqlDB, DB.Issue.Interest.TABLE, DB.Issue.Interest.COLUMNS, values);
                break;
            case ISSUE:
                numInserted = bulkInsert(uri, sqlDB, DB.Issue.TABLE, DB.Issue.COLUMNS, values);
                break;
            case OPINION:
                numInserted = bulkInsert(uri, sqlDB, DB.Opinion.TABLE, DB.Opinion.COLUMNS, values);
                break;
            case POLICY:
                numInserted = bulkInsert(uri, sqlDB, DB.Policy.TABLE, DB.Policy.COLUMNS, values);
                break;
            case PRIVILEGE:
                numInserted = bulkInsert(uri, sqlDB, DB.Privilege.TABLE, DB.Privilege.COLUMNS, values);
                break;
            case SUGGESTION:
                numInserted = bulkInsert(uri, sqlDB, DB.Suggestion.TABLE, DB.Suggestion.COLUMNS, values);
                break;
            case SUPPORTER:
                numInserted = bulkInsert(uri, sqlDB, DB.Initiative.Supporter.TABLE, DB.Initiative.Supporter.COLUMNS, values);
                break;
            case MEMBER:
                numInserted = bulkInsert(uri, sqlDB, DB.Member.TABLE, DB.Member.COLUMNS, values);
                break;
            case MEMBERSHIP:
                numInserted = bulkInsert(uri, sqlDB, DB.Membership.TABLE, DB.Membership.COLUMNS, values);
                break;
            case MEMBER_IMAGE:
                numInserted = bulkInsert(uri, sqlDB, DB.Member.Image.TABLE, DB.Member.Image.COLUMNS, values);
                break;
            case UNIT:
                numInserted = bulkInsert(uri, sqlDB, DB.Unit.TABLE, DB.Unit.COLUMNS, values);
                break;
            case VOTE:
                numInserted = bulkInsert(uri, sqlDB, DB.Vote.TABLE, DB.Vote.COLUMNS, values);
                break;
            case VOTE_COMMENT:
                numInserted = bulkInsert(uri, sqlDB, DB.Vote.Comment.TABLE, DB.Issue.Comment.COLUMNS, values);
                break;
            case VOTER:
                numInserted = bulkInsert(uri, sqlDB, DB.Vote.Voter.TABLE, DB.Vote.Voter.COLUMNS, values);
                break;
            case DELEGATING_VOTER:
                numInserted = bulkInsert(uri, sqlDB, DB.Vote.DelegatingVoter.TABLE, DB.Vote.DelegatingVoter.COLUMNS, values);
                break;
        default:
            throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }

        return numInserted;
    }
    
    protected int bulkInsert(Uri uri, SQLiteDatabase sqlDB, String table, String[] columns, ContentValues[] values) {

        sqlDB.beginTransaction();
        try {
            String questionMarks = StringUtils.repeat("?,", columns.length - 1) + "?";
        
            SQLiteStatement insert = 
                    sqlDB.compileStatement("INSERT OR REPLACE INTO " + table + "(" + cols(columns) +
                            ") VALUES (" + questionMarks + ");");
        

            for (ContentValues value : values) {
                
                for (int i = 0; i < columns.length - 1; i++) {
                    bind(insert, i + 1, value.getAsString(columns[i]));
                }

                bind(insert, columns.length,   String.valueOf(System.currentTimeMillis()));
            
                insert.execute();
                // Log.d("XXXX", "inserted " +  value.getAsInteger(DB.Area.COLUMN_ID));
            }
        
            sqlDB.setTransactionSuccessful();
        } finally {
            sqlDB.endTransaction();
            getContext().getContentResolver().notifyChange(uri, null, false);
        }

        return values.length;
    }

    @Override
    public boolean onCreate() {
        
        mContext = getContext();
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
            String sortOrder) {
        
        String database = uri.getQueryParameter("db");
        
        // Log.d("XXXXXXXXXX", "select from database: " + uri);
        
        if (!mDb.containsKey(database)) {
            mDb.put(database, new DB(mContext, database));
        }
        
        SQLiteDatabase sdb = mDb.get(database).getReadableDatabase();
        
        // Using SQLiteQueryBuilder instead of query() method
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        // Check if the caller has requested a column which does not exists
        // checkColumns(projection);
        
        Cursor cursor;
        
        switch (URIMatcher.match(uri)) {
            case UPDATED:
                queryBuilder.setTables(DB.Updated.TABLE);
                cursor = queryBuilder.query(sdb, projection, selection, selectionArgs, null, null, sortOrder);

                break;
            case UPDATED_NAME:
                queryBuilder.setTables(DB.Updated.TABLE);
                // cursor = queryBuilder.query(sdb, projection, selection, selectionArgs, null, null, sortOrder);
                
                cursor = sdb.rawQuery("SELECT name, updated FROM updated WHERE name = ?;",
                        new String[] { uri.getLastPathSegment()});

                // Adding the ID to the original query
                // queryBuilder.appendWhere("_id" + "=" + uri.getLastPathSegment());
                break;
            case AREA:
                queryBuilder.setTables(DB.Area.TABLE);
                cursor = queryBuilder.query(sdb, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case AREA_ACTIVE:
                cursor = sdb.rawQuery("SELECT * FROM area WHERE active = 1;", null);
                break;
            case AREA_LIST_BY_UNIT:
                cursor = sdb.rawQuery("SELECT area._id, area.name, member_weight, direct_member_count, " +
                        "(SELECT COUNT(*) FROM issue WHERE issue.area_id = area._id AND issue.accepted ISNULL AND issue.closed ISNULL) AS issues_new_count, " +
                        "(SELECT COUNT(*) FROM issue WHERE issue.area_id = area._id AND issue.accepted NOTNULL AND issue.half_frozen ISNULL AND issue.closed ISNULL) AS issues_discussion_count, " +
                        "(SELECT COUNT(*) FROM issue WHERE issue.area_id = area._id AND issue.half_frozen NOTNULL AND issue.fully_frozen ISNULL AND issue.closed ISNULL) AS issues_frozen_count, " +
                        "(SELECT COUNT(*) FROM issue WHERE issue.area_id = area._id AND issue.fully_frozen NOTNULL AND issue.closed ISNULL) AS issues_voting_count, " +
                        "(SELECT COUNT(*) FROM issue WHERE issue.area_id = area._id AND issue.fully_frozen NOTNULL AND issue.closed NOTNULL) AS issues_finished_count," +
                        "(SELECT COUNT(*) FROM issue WHERE issue.area_id = area._id AND issue.fully_frozen ISNULL AND issue.closed NOTNULL) AS issues_cancelled_count, " +
                        "unit.name AS unit_name  " +
                        "FROM area, unit WHERE area.unit_id = unit._id AND unit_id = ? " +
                        "GROUP BY area._id, area.name, member_weight, direct_member_count " +
                        "ORDER BY area.member_weight DESC",
                        new String[] { uri.getLastPathSegment()});
                // Adding the ID to the original query
                // queryBuilder.appendWhere("_id" + "=" + uri.getLastPathSegment());
                break;
            case AREA_ID:
                queryBuilder.setTables(DB.Area.TABLE);
                // cursor = queryBuilder.query(sdb, projection, selection, selectionArgs, null, null, sortOrder);
                cursor = sdb.rawQuery("SELECT * FROM area WHERE _id = ?;",
                        new String[] { uri.getLastPathSegment()});
                // Adding the ID to the original query
                // queryBuilder.appendWhere("_id" + "=" + uri.getLastPathSegment());
                break;
            case BATTLE:
                queryBuilder.setTables(DB.Initiative.Battle.TABLE);
                cursor = queryBuilder.query(sdb, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case BATTLE_ID:
                queryBuilder.setTables(DB.Initiative.Battle.TABLE);
                cursor = sdb.rawQuery("SELECT * FROM battle WHERE issue_id = ?;",
                        new String[] { uri.getLastPathSegment()});
                break;
            case ISSUE_COMMENT:
                queryBuilder.setTables(DB.Issue.Comment.TABLE); // FIXME
                cursor = queryBuilder.query(sdb, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case ISSUE_COMMENT_ID:
                queryBuilder.setTables(DB.Issue.Comment.TABLE);
                cursor = sdb.rawQuery("SELECT * FROM issue_comment WHERE issue_id = ? AND member_id = ?;",
                        new String[] { uri.getQueryParameter("issue_id"), uri.getQueryParameter("member_id")});
                break;
            case CONTACT:
                queryBuilder.setTables(DB.Member.TABLE + ", " + DB.Member.Contact.TABLE);
                cursor = queryBuilder.query(sdb, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case CONTACT_ID:
                queryBuilder.setTables(DB.Member.TABLE + ", " + DB.Member.Contact.TABLE);
                cursor = queryBuilder.query(sdb, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case DELEGATION:
                String unitId = uri.getQueryParameter("unit_id");
                String memberId = uri.getQueryParameter("member_id");
                
                if (unitId != null && memberId != null) {
                    cursor = sdb.rawQuery("SELECT           " +
                            "member.* " +
                            "FROM member INNER JOIN delegation " +
                            "ON delegation.scope = 'unit' AND delegation.unit_id = ? AND " +
                            "   delegation.trustee_id = member._id AND " +
                            "   delegation.truster_id = ? " +
                            "LIMIT 1;", new String[] { unitId, memberId });
                } else {
                    queryBuilder.setTables(DB.Delegation.TABLE + ", " + DB.Member.TABLE);
                    cursor = queryBuilder.query(sdb, projection, selection, selectionArgs, null, null, sortOrder);
                }
                break;
            case DELEGATION_ID:
                queryBuilder.setTables(DB.Delegation.TABLE + ", " + DB.Member.TABLE);
                // cursor = queryBuilder.query(sdb, projection, selection, selectionArgs, null, null, sortOrder);
                cursor = sdb.rawQuery("SELECT * FROM delegation WHERE _id = ?;",
                        new String[] { uri.getLastPathSegment()});
                // Adding the ID to the original query
                // queryBuilder.appendWhere("_id" + "=" + uri.getLastPathSegment());
                break;
            case DRAFT:
                queryBuilder.setTables(DB.Draft.TABLE);
                cursor = queryBuilder.query(sdb, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case DRAFT_AREA_ID:
                queryBuilder.setTables(DB.Draft.TABLE);
                // cursor = queryBuilder.query(sdb, projection, selection, selectionArgs, null, null, sortOrder);
                cursor = sdb.rawQuery("SELECT * FROM draft WHERE area_id = ?;",
                        new String[] { uri.getLastPathSegment()});
                // Adding the ID to the original query
                // queryBuilder.appendWhere("_id" + "=" + uri.getLastPathSegment());
                break;
            case EVENT:
                queryBuilder.setTables(DB.Event.TABLE);
                cursor = queryBuilder.query(sdb, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case EVENT_ID:
                queryBuilder.setTables(DB.Event.TABLE);
                // cursor = queryBuilder.query(sdb, projection, selection, selectionArgs, null, null, sortOrder);
                cursor = sdb.rawQuery("SELECT * FROM event WHERE _id = ?;",
                        new String[] { uri.getLastPathSegment()});
                // Adding the ID to the original query
                // queryBuilder.appendWhere("_id" + "=" + uri.getLastPathSegment());
                break;
            case INITIATIVE:
                queryBuilder.setTables(DB.Initiative.TABLE);
                cursor = queryBuilder.query(sdb, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case INITIATIVE_ID:
                queryBuilder.setTables(DB.Initiative.TABLE);
                // cursor = queryBuilder.query(sdb, projection, selection, selectionArgs, null, null, sortOrder);
                cursor = sdb.rawQuery("SELECT * FROM initiative WHERE _id = ?;",
                        new String[] { uri.getLastPathSegment()});
                // Adding the ID to the original query
                // queryBuilder.appendWhere("_id" + "=" + uri.getLastPathSegment());
                break;
            case INITIATOR:
                queryBuilder.setTables(DB.Initiative.Initiator.TABLE);
                cursor = queryBuilder.query(sdb, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case INITIATOR_ID:
                queryBuilder.setTables(DB.Initiative.Initiator.TABLE);
                // cursor = queryBuilder.query(sdb, projection, selection, selectionArgs, null, null, sortOrder);
                cursor = sdb.rawQuery("SELECT * FROM initiator WHERE initiative_id = ? AND member_id = ?;",
                        new String[] { uri.getQueryParameter("initiative_id"), uri.getQueryParameter("member_id")});
                // Adding the ID to the original query
                // queryBuilder.appendWhere("_id" + "=" + uri.getLastPathSegment());
                break;
            case INTEREST:
                queryBuilder.setTables(DB.Issue.Interest.TABLE);
                cursor = queryBuilder.query(sdb, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case ISSUE:
                queryBuilder.setTables("issue, area, policy");
                cursor = queryBuilder.query(sdb, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case ISSUE_PURE:
                queryBuilder.setTables(DB.Issue.TABLE);
                cursor = queryBuilder.query(sdb, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case INIS:
                String query2 =
                        "SELECT " +
                        " issue._id       AS _id,                        " +
                        " area.name       AS area_name,                  " +
                        " issue.population,                              " + 
                        " issue.state     AS issue_state,                " +
                        " issue.voter_count,                             " +
                        
                        " issue.accepted     AS issue_accepted,          " +
                        " issue.closed       AS issue_closed,            " +
                        " issue.fully_frozen AS issue_fully_frozen,      " +
                        " issue.ranks_available AS ranks_available,      " +
                        
                        " initiative.negative_votes,                     " +
                        " initiative.positive_votes,                     " +
                        " initiative.satisfied_supporter_count,          " +
                        " initiative.supporter_count,                    " +
                        
                        " policy.initiative_quorum_num,                  " +
                        " policy.initiative_quorum_den,                  " +
                        " policy.issue_quorum_num,                       " +
                        " policy.issue_quorum_den,                       " +
                        
                        " initiative._id  AS initiative_id,              " +
                        " initiative.name AS initiative_name,            " +
                        " initiative.rank AS initiative_rank,            " +
                        " initiative.eligible AS initiative_eligible,    " +
                        " initiative.winner AS initiative_winner,        " +
                        " initiative.revoked AS initiative_revoked,      " +
                        " initiative.admitted AS initiative_admitted     " +
                        "FROM    area, issue, initiative, policy         " +
                        "WHERE   area._id            = issue.area_id AND " +
                        "        initiative.issue_id = issue._id AND     " +
                        "        issue.policy_id     = policy._id AND    " +
                        "        issue._id           = ?                 " +
                        "ORDER BY initiative_rank, initiative.supporter_count DESC, _id;";
                cursor = sdb.rawQuery(query2, new String[] { uri.getLastPathSegment() });
                break;
            case ISSUE_LATEST:
                String query3 =
                        "SELECT                                                       " + 
                        " issue._id                   AS _id,                         " +
                        " issue.population            AS issue_population,            " +
                        " area.name                   AS area_name,                   " +
                        
                        " issue.created               AS issue_created,               " +
                        " issue.accepted              AS issue_accepted,              " +
                        " issue.half_frozen           AS issue_half_frozen,           " +
                        " issue.fully_frozen          AS issue_fully_frozen,          " +
                        " issue.closed                AS issue_closed,                " +
                        " issue.cleaned               AS issue_cleaned,               " +
                        " issue.admission_time        AS issue_admission_time,        " +
                        " issue.discussion_time       AS issue_discussion_time,       " +
                        " issue.voting_time           AS issue_voting_time,           " +
                        " issue.snapshot              AS issue_snapshot,              " +
                        " issue.latest_snapshot_event AS issue_latest_snapshot_event, " +
                        
                        " issue.state                 AS issue_state                  " +
                        "FROM     area, issue                                         " +
                        "WHERE    area._id = issue.area_id                            " +
                        // "GROUP BY _id                                                 " +
                        "ORDER BY _id DESC;                                           ";
                cursor = sdb.rawQuery(query3, new String[] {});
                break;
            case ISSUE_LATEST_OPEN:
                String query6 =
                        "SELECT                                 " +
                        " issue._id AS _id,                     " +
                        " issue.population AS issue_population, " +
                        " area.name AS area_name,               " +

                        " issue.created               AS issue_created,               " +
                        " issue.accepted              AS issue_accepted,              " +
                        " issue.half_frozen           AS issue_half_frozen,           " +
                        " issue.fully_frozen          AS issue_fully_frozen,          " +
                        " issue.closed                AS issue_closed,                " +
                        " issue.cleaned               AS issue_cleaned,               " +
                        " issue.admission_time        AS issue_admission_time,        " +
                        " issue.discussion_time       AS issue_discussion_time,       " +
                        " issue.voting_time           AS issue_voting_time,           " +
                        " issue.snapshot              AS issue_snapshot,              " +
                        " issue.latest_snapshot_event AS issue_latest_snapshot_event, " +
                        
                        " issue.state AS issue_state            " +
                        "FROM  area, issue                      " +
                        "WHERE area._id = issue.area_id AND     " +
                        "(issue_state = 'admission' OR          " +
                        " issue_state = 'discussion' OR         " +
                        " issue_state = 'verification' OR       " +
                        " issue_state = 'voting')               " +
                        // "GROUP BY _id                           " +
                        "ORDER BY _id DESC;                     ";
                cursor = sdb.rawQuery(query6, new String[] {});
                break;
            case ISSUE_LATEST_CLOSED:
                //String query3 = "select issue._id AS _id, initiative.name AS initiative_name from area, issue, initiative where area._id = issue.area_id AND initiative.issue_id = issue._id GROUP BY issue_id ORDER BY _id DESC;";
                String query7 =
                        "SELECT                                                            " +
                        " issue._id AS _id,                                                " +
                        " issue.population AS issue_population,                            " +
                        " area.name AS area_name,                                          " +
                        
                        " issue.created               AS issue_created,               " +
                        " issue.accepted              AS issue_accepted,              " +
                        " issue.half_frozen           AS issue_half_frozen,           " +
                        " issue.fully_frozen          AS issue_fully_frozen,          " +
                        " issue.closed                AS issue_closed,                " +
                        " issue.cleaned               AS issue_cleaned,               " +
                        " issue.admission_time        AS issue_admission_time,        " +
                        " issue.discussion_time       AS issue_discussion_time,       " +
                        " issue.voting_time           AS issue_voting_time,           " +
                        " issue.snapshot              AS issue_snapshot,              " +
                        " issue.latest_snapshot_event AS issue_latest_snapshot_event, " +
                        
                        " issue.state AS issue_state                                       " +
                        "FROM area, issue                                                  " +
                        "WHERE area._id = issue.area_id AND                                " +
                        "(issue_state = 'finished_without_winner' OR                       " +
                        " issue_state = 'finished_with_winner' OR                          " +
                        " issue_state = 'canceled_no_initiative_admitted' OR               " +
                        " issue_state = 'canceled_after_revocation_during_verification' OR " +
                        " issue_state = 'canceled_after_revocation_during_discussion' OR   " +
                        " issue_state = 'canceled_issue_not_accepted' OR                   " +
                        " issue_state = 'canceled_revoked_before_accepted')                " +
                        // "GROUP BY _id                                                      " +
                        "ORDER BY _id DESC;                                                ";
                cursor = sdb.rawQuery(query7, new String[] {});
                break;
            case ISSUE_BY_AREA:
                String query4 =
                        "SELECT                                 " +
                        " issue._id AS _id,                     " +
                        " issue.population AS issue_population, " +
                        " area.name AS area_name,               " +

                        " issue.created               AS issue_created,               " +
                        " issue.accepted              AS issue_accepted,              " +
                        " issue.half_frozen           AS issue_half_frozen,           " +
                        " issue.fully_frozen          AS issue_fully_frozen,          " +
                        " issue.closed                AS issue_closed,                " +
                        " issue.cleaned               AS issue_cleaned,               " +
                        " issue.admission_time        AS issue_admission_time,        " +
                        " issue.discussion_time       AS issue_discussion_time,       " +
                        " issue.voting_time           AS issue_voting_time,           " +
                        " issue.snapshot              AS issue_snapshot,              " +
                        " issue.latest_snapshot_event AS issue_latest_snapshot_event, " +
                        
                        " issue.state AS issue_state            " +
                        "FROM  area, issue                      " +
                        "WHERE area._id = issue.area_id AND     " +
                        "      issue.area_id = ?                " +
                        // "GROUP BY _id                           " +
                        "ORDER BY _id DESC;                     ";
                cursor = sdb.rawQuery(query4, new String[] { uri.getLastPathSegment() });
                break;
            case ISSUE_BY_AREA_OPEN:
                String query5 =
                        "SELECT                                 " +
                        " issue._id AS _id,                     " +
                        " issue.population AS issue_population, " +
                        " area.name AS area_name,               " +
                        
                        " issue.created               AS issue_created,               " +
                        " issue.accepted              AS issue_accepted,              " +
                        " issue.half_frozen           AS issue_half_frozen,           " +
                        " issue.fully_frozen          AS issue_fully_frozen,          " +
                        " issue.closed                AS issue_closed,                " +
                        " issue.cleaned               AS issue_cleaned,               " +
                        " issue.admission_time        AS issue_admission_time,        " +
                        " issue.discussion_time       AS issue_discussion_time,       " +
                        " issue.voting_time           AS issue_voting_time,           " +
                        " issue.snapshot              AS issue_snapshot,              " +
                        " issue.latest_snapshot_event AS issue_latest_snapshot_event, " +
                        
                        " issue.state AS issue_state            " +
                        "FROM area, issue                       " +
                        "WHERE area._id = issue.area_id AND     " +
                        "      (issue_state = 'admission' OR    " +
                        "       issue_state = 'discussion' OR   " +
                        "       issue_state = 'verification' OR " +
                        "       issue_state = 'voting') AND     " +
                        "      issue.area_id = ?                " +
                        // "GROUP BY _id                           " +
                        "ORDER BY _id DESC;                     ";
                cursor = sdb.rawQuery(query5, new String[] { uri.getLastPathSegment() });
                break;
            case ISSUE_BY_AREA_CLOSED:
                String query8 =
                        "SELECT                                                                  " +
                        " issue._id AS _id,                                                      " +
                        " issue.population AS issue_population,                                  " +
                        " area.name AS area_name,                                                " +
                        
                        " issue.created               AS issue_created,               " +
                        " issue.accepted              AS issue_accepted,              " +
                        " issue.half_frozen           AS issue_half_frozen,           " +
                        " issue.fully_frozen          AS issue_fully_frozen,          " +
                        " issue.closed                AS issue_closed,                " +
                        " issue.cleaned               AS issue_cleaned,               " +
                        " issue.admission_time        AS issue_admission_time,        " +
                        " issue.discussion_time       AS issue_discussion_time,       " +
                        " issue.voting_time           AS issue_voting_time,           " +
                        " issue.snapshot              AS issue_snapshot,              " +
                        " issue.latest_snapshot_event AS issue_latest_snapshot_event, " +

                        " issue.state AS issue_state                                             " +
                        "FROM area, issue                                                        " +
                        "WHERE area._id = issue.area_id AND                                      " +
                        "      (issue_state = 'finished_without_winner' OR                       " +
                        "       issue_state = 'finished_with_winner' OR                          " +
                        "       issue_state = 'canceled_no_initiative_admitted' OR               " +
                        "       issue_state = 'canceled_after_revocation_during_verification' OR " +
                        "       issue_state = 'canceled_after_revocation_during_discussion' OR   " +
                        "       issue_state = 'canceled_issue_not_accepted' OR                   " +
                        "       issue_state = 'canceled_revoked_before_accepted') AND            " +
                        "      issue.area_id = ?                                                 " +
                        // "GROUP BY _id " +
                        "ORDER BY _id DESC;";
                cursor = sdb.rawQuery(query8, new String[] { uri.getLastPathSegment() });
                break;
            case ISSUE_ID:
                queryBuilder.setTables(DB.Issue.TABLE);
                // cursor = queryBuilder.query(sdb, projection, selection, selectionArgs, null, null, sortOrder);
                cursor = sdb.rawQuery("SELECT * FROM issue WHERE _id = ?;",
                        new String[] { uri.getLastPathSegment()});
                // Adding the ID to the original query
                // queryBuilder.appendWhere("_id" + "=" + uri.getLastPathSegment());
                break;
            case OPINION:
                queryBuilder.setTables(DB.Opinion.TABLE);
                cursor = queryBuilder.query(sdb, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case OPINION_ID:
                queryBuilder.setTables(DB.Opinion.TABLE);
                // cursor = queryBuilder.query(sdb, projection, selection, selectionArgs, null, null, sortOrder);
                cursor = sdb.rawQuery("SELECT * FROM opinion WHERE _id = ?;",
                        new String[] { uri.getLastPathSegment()});
                // Adding the ID to the original query
                // queryBuilder.appendWhere("_id" + "=" + uri.getLastPathSegment());
                break;
            case POLICY:
                queryBuilder.setTables(DB.Policy.TABLE);
                cursor = queryBuilder.query(sdb, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case POLICY_ID:
                queryBuilder.setTables(DB.Policy.TABLE);
                // cursor = queryBuilder.query(sdb, projection, selection, selectionArgs, null, null, sortOrder);
                cursor = sdb.rawQuery("SELECT * FROM opinion WHERE _id = ?;",
                        new String[] { uri.getLastPathSegment()});
                // Adding the ID to the original query
                // queryBuilder.appendWhere("_id" + "=" + uri.getLastPathSegment());
                break;
            case PRIVILEGE:
                queryBuilder.setTables(DB.Privilege.TABLE);
                cursor = queryBuilder.query(sdb, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case PRIVILEGE_ID:
                queryBuilder.setTables(DB.Privilege.TABLE);
                // cursor = queryBuilder.query(sdb, projection, selection, selectionArgs, null, null, sortOrder);
                cursor = sdb.rawQuery("SELECT * FROM privilege WHERE _id = ?;",
                        new String[] { uri.getLastPathSegment()});
                // Adding the ID to the original query
                // queryBuilder.appendWhere("_id" + "=" + uri.getLastPathSegment());
                break;
            case SUGGESTION:
                queryBuilder.setTables(DB.Suggestion.TABLE);
                cursor = queryBuilder.query(sdb, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case SUGGESTION_ID:
                queryBuilder.setTables(DB.Suggestion.TABLE);
                // cursor = queryBuilder.query(sdb, projection, selection, selectionArgs, null, null, sortOrder);
                cursor = sdb.rawQuery("SELECT * FROM suggestion WHERE _id = ?;",
                        new String[] { uri.getLastPathSegment()});
                // Adding the ID to the original query
                // queryBuilder.appendWhere("_id" + "=" + uri.getLastPathSegment());
                break;
            case SUPPORTER:
                queryBuilder.setTables(DB.Initiative.Supporter.TABLE);
                cursor = queryBuilder.query(sdb, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case SUPPORTER_ID:
                queryBuilder.setTables(DB.Initiative.Supporter.TABLE);
                cursor = sdb.rawQuery("SELECT * FROM supporter WHERE _id = ?;",
                        new String[] { uri.getLastPathSegment()});
                // Adding the ID to the original query
                // queryBuilder.appendWhere("_id" + "=" + uri.getLastPathSegment());
                break;
            case MEMBER:
                queryBuilder.setTables(DB.Member.TABLE);
                cursor = queryBuilder.query(sdb, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case MEMBER_ID:
                queryBuilder.setTables(DB.Member.TABLE);
                // cursor = queryBuilder.query(sdb, projection, selection, selectionArgs, null, null, sortOrder);
                cursor = sdb.rawQuery("SELECT * FROM member WHERE _id = ?;",
                        new String[] { uri.getLastPathSegment()});
                // Adding the ID to the original query
                // queryBuilder.appendWhere("_id" + "=" + uri.getLastPathSegment());
                break;
            case MEMBER_IMAGE:
                queryBuilder.setTables(DB.Member.Image.TABLE);
                cursor = queryBuilder.query(sdb, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case MEMBER_IMAGE_ID:
                queryBuilder.setTables(DB.Member.Image.TABLE);
                // cursor = queryBuilder.query(sdb, projection, selection, selectionArgs, null, null, sortOrder);
                cursor = sdb.rawQuery("SELECT * FROM member_image WHERE member_id = ?;",
                        new String[] { uri.getLastPathSegment()});
                // Adding the ID to the original query
                // queryBuilder.appendWhere("_id" + "=" + uri.getLastPathSegment());
                break;
            case MEMBERSHIP:
                queryBuilder.setTables(DB.Membership.TABLE);
                cursor = queryBuilder.query(sdb, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case MEMBERSHIP_ID:
                queryBuilder.setTables(DB.Membership.TABLE);
                // cursor = queryBuilder.query(sdb, projection, selection, selectionArgs, null, null, sortOrder);
                cursor = sdb.rawQuery("SELECT * FROM membership WHERE area_id = ? AND member_id = ?;",
                        new String[] { uri.getQueryParameter("area_id"), uri.getQueryParameter("member_id") });
                // Adding the ID to the original query
                // queryBuilder.appendWhere("_id" + "=" + uri.getLastPathSegment());
                break;
            case UNIT:
                queryBuilder.setTables(DB.Unit.TABLE);
                cursor = queryBuilder.query(sdb, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case UNIT_LIST:
                cursor = sdb.rawQuery("SELECT unit._id AS _id, unit._id AS unit_id, unit.name AS unit_name, member_count AS unit_member_count " +
                        "from unit ORDER BY unit_name;", null);
                break;
            case UNIT_ID:
                queryBuilder.setTables(DB.Unit.TABLE);
                // cursor = queryBuilder.query(sdb, projection, selection, selectionArgs, null, null, sortOrder);
                cursor = sdb.rawQuery("SELECT * FROM unit WHERE _id = ?;",
                        new String[] { uri.getLastPathSegment()});
                // Adding the ID to the original query
                // queryBuilder.appendWhere("_id" + "=" + uri.getLastPathSegment());
                break;
            case VOTE:
                queryBuilder.setTables(DB.Vote.TABLE);
                cursor = queryBuilder.query(sdb, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case VOTE_ID:
                queryBuilder.setTables(DB.Vote.TABLE);
                // cursor = queryBuilder.query(sdb, projection, selection, selectionArgs, null, null, sortOrder);
                cursor = sdb.rawQuery("SELECT * FROM vote WHERE issue_id = ? AND initiative_id = ? AND member_id = ?;",
                        new String[] {
                            uri.getQueryParameter("issue_id"),
                            uri.getQueryParameter("initiative_id"),
                            uri.getQueryParameter("member_id") });
                // Adding the ID to the original query
                // queryBuilder.appendWhere("_id" + "=" + uri.getLastPathSegment());
                break;
            case VOTE_COMMENT:
                queryBuilder.setTables(DB.Vote.Comment.TABLE);
                cursor = queryBuilder.query(sdb, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case VOTE_COMMENT_ID:
                queryBuilder.setTables(DB.Vote.Comment.TABLE);
                cursor = queryBuilder.query(sdb, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case VOTER:
                queryBuilder.setTables(DB.Vote.Voter.TABLE);
                cursor = queryBuilder.query(sdb, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case VOTER_ID:
                queryBuilder.setTables(DB.Vote.Voter.TABLE);
                cursor = queryBuilder.query(sdb, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case DELEGATING_VOTER:
                queryBuilder.setTables(DB.Vote.DelegatingVoter.TABLE);
                cursor = queryBuilder.query(sdb, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case DELEGATING_VOTER_ID:
                queryBuilder.setTables(DB.Vote.DelegatingVoter.TABLE);
                cursor = queryBuilder.query(sdb, projection, selection, selectionArgs, null, null, sortOrder);
                break;
        default:
            throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        // Make sure that potential listeners are getting notified
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        
        /*
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex("meta_cached");
        
        if (idx != -1 && !cursor.isAfterLast()) {
            long age = (System.currentTimeMillis() - cursor.getLong(idx)) / 1000;
            Log.d(TAG, uri + " age " + age + " seconds");
        } */
        
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
    
    public void bind(SQLiteStatement insert, int index, String value) {
        if (value != null) {
            insert.bindString(index, value);
        } else {
            insert.bindNull(index);
        }
    }
    
    public String cols(String[] columns) {
        String c = "";
        
        for (int i = 0; i < columns.length; i++) {
            c += columns[i];
            
            if (i < columns.length - 1) {
                c += ", ";
            }
        }
        
        return c;
    }
}
