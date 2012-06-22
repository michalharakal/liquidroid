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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DB extends SQLiteOpenHelper {
    
    protected String mDatabaseName;
    
    protected static final int VERSION = 52;
    
    public static class Updated {
        public static final String TABLE = "updated";
        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_UPDATED = "updated";
        
        public static final String[] COLUMNS = new String[] {
            COLUMN_ID,
            COLUMN_NAME,
            COLUMN_UPDATED
        };
        
        public static void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + TABLE + " (" +
                    "name             INTEGER    NOT NULL                          , " +
                    "updated          TIMESTAMP  NOT NULL                           " +
                    ");");
        }
        
        public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE); onCreate(db);
        }
    }
    
    public static class Area {
        public static final String TABLE = "area";
        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_UNIT_ID = "unit_id";
        public static final String COLUMN_ACTIVE = "active";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_DIRECT_MEMBER_COUNT = "direct_member_count";
        public static final String COLUMN_MEMBER_WEIGHT = "member_weight";

        public static final String COLUMN_META_CACHED = "meta_cached";
        
        public static final String[] COLUMNS = new String[] {
            COLUMN_ID,
            COLUMN_UNIT_ID,
            COLUMN_ACTIVE,
            COLUMN_NAME,
            COLUMN_DESCRIPTION,
            COLUMN_DIRECT_MEMBER_COUNT,
            COLUMN_MEMBER_WEIGHT,
            COLUMN_META_CACHED
        };
        
        public static void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + TABLE + " (" +
                    "_id                     INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                    "unit_id                 INTEGER                                   , " +
                    "active                  INTEGER                                   , " +
                    "name                    TEXT                                      , " +
                    "description             TEXT                                      , " +
                    "direct_member_count     INTEGER                                   , " +
                    "member_weight           INTEGER                                   , " +
                    "meta_cached             TIMESTAMP                                   " +
                    ");");
        }
        
        public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE); onCreate(db);
        }
    }
    
    public static class Delegation {
        public static final String TABLE = "delegation";
        public static final String COLUMN_UNIT_ID = "unit_id";
        public static final String COLUMN_AREA_ID = "area_id";
        public static final String COLUMN_ISSUE_ID = "issue_id";
        public static final String COLUMN_SCOPE = "scope";
        public static final String COLUMN_TRUSTER_ID = "truster_id";
        public static final String COLUMN_TRUSTEE_ID = "trustee_id";
        
        public static final String COLUMN_META_CACHED = "meta_cached";
        
        public static final String[] COLUMNS = new String[] {
            COLUMN_UNIT_ID,
            COLUMN_AREA_ID,
            COLUMN_ISSUE_ID,
            COLUMN_SCOPE,
            COLUMN_TRUSTER_ID,
            COLUMN_TRUSTEE_ID,
            COLUMN_META_CACHED
        };
        
        public static void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + TABLE + " (" +
                    "unit_id                 INTEGER                                   , " +
                    "area_id                 INTEGER                                   , " +
                    "issue_id                INTEGER                                   , " +
                    "scope                   TEXT                                      , " +
                    "truster_id              INTEGER                                   , " +
                    "trustee_id              INTEGER                                   , " +
                    "meta_cached             TIMESTAMP                                   " +
                    ");");
        }
        
        public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE); onCreate(db);
        }
    }
    
    public static class Draft {
        public static final String TABLE = "draft";
        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_AREA_ID = "area_id";
        public static final String COLUMN_ISSUE_ID = "issue_id";
        public static final String COLUMN_INITIATIVE_ID = "initiative_id";
        public static final String COLUMN_POLICY_ID = "policy_id";
        public static final String COLUMN_INITIATIVE_NAME = "initiative_name";
        public static final String COLUMN_INITIATIVE_DISCUSSION_URL = "initiative_discussion_url";
        public static final String COLUMN_FORMATTING_ENGINE = "formatting_engine";
        public static final String COLUMN_CONTENT = "content";

        public static final String COLUMN_META_CACHED = "meta_cached";
        
        public static final String[] COLUMNS = new String[] {
            COLUMN_ID,
            COLUMN_AREA_ID,
            COLUMN_ISSUE_ID,
            COLUMN_INITIATIVE_ID,
            COLUMN_POLICY_ID,
            COLUMN_INITIATIVE_NAME,
            COLUMN_INITIATIVE_DISCUSSION_URL,
            COLUMN_FORMATTING_ENGINE,
            COLUMN_CONTENT,
            COLUMN_META_CACHED
        };
        
        public static void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + TABLE + " (" +
                    "_id                       INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                    "area_id                   INTEGER                                   , " +
                    "issue_id                  INTEGER                                   , " +
                    "initiative_id             INTEGER                                   , " +
                    "policy_id                 INTEGER                                   , " +
                    "initiative_name           TEXT                                      , " +
                    "initiative_discussion_url TEXT                                      , " +
                    "formatting_engine         TEXT                                      , " +
                    "content                   TEXT                                      , " +
                    "meta_cached               TIMESTAMP                                   " +
                    ");");
        }
        
        public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE); onCreate(db);
        }
    }
    
    public static class Event {
        public static final String TABLE = "event";
        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_OCCURRENCE = "occurrence";
        public static final String COLUMN_EVENT = "event";
        public static final String COLUMN_MEMBER_ID = "member_id";
        public static final String COLUMN_ISSUE_ID = "issue_id";
        public static final String COLUMN_DRAFT_ID = "draft_id";
        public static final String COLUMN_SUGGESTION_ID = "suggestion_id";

        public static final String COLUMN_META_CACHED = "meta_cached";
        
        public static final String[] COLUMNS = new String[] {
            COLUMN_ID,
            COLUMN_OCCURRENCE,
            COLUMN_EVENT,
            COLUMN_MEMBER_ID,
            COLUMN_ISSUE_ID,
            COLUMN_DRAFT_ID,
            COLUMN_SUGGESTION_ID,
            COLUMN_META_CACHED
        };
        
        public static void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + TABLE + " (" +
                    "_id                     INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                    "occurrence              INTEGER            , " +
                    "event                   TEXT               , " +
                    "member_id               INTEGER            , " +
                    "issue_id                INTEGER            , " +
                    "draft_id                INTEGER            , " +
                    "suggestion_id           INTEGER            , " + 
                    "meta_cached             TIMESTAMP            " +
                    ");");
        }
        
        public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE); onCreate(db);
        }
    }
    
    public static class Initiative {
        public static final String TABLE = "initiative";
        public static final String COLUMN_ID                        = "_id";
        public static final String COLUMN_ISSUE_ID                  = "issue_id";
        public static final String COLUMN_NAME                      = "name";
        public static final String COLUMN_DISCUSSION_URL            = "discussion_url";
        public static final String COLUMN_CREATED                   = "created";
        public static final String COLUMN_REVOKED                   = "revoked";
        public static final String COLUMN_REVOKED_BY_MEMBER_ID      = "revoked_by_member_id";
        public static final String COLUMN_SUGGESTED_INITIATIVE_ID   = "suggested_initiative_id";
        public static final String COLUMN_ADMITTED                  = "admitted";
        public static final String COLUMN_SUPPORTER_COUNT           = "supporter_count";
        public static final String COLUMN_INFORMED_SUPPORTER_COUNT  = "informed_supporter_count";
        public static final String COLUMN_SATISFIED_SUPPORTER_COUNT = "satisfied_supporter_count";
        public static final String COLUMN_SATISFIED_INFORMED_SUPPORTER_COUNT = "satisfied_informed_supporter_count";
        public static final String COLUMN_POSITIVE_VOTES            = "positive_votes";
        public static final String COLUMN_NEGATIVE_VOTES            = "negative_votes";
        public static final String COLUMN_DIRECT_MAJORITY           = "direct_majority";
        public static final String COLUMN_INDIRECT_MAJORITY         = "indirect_majority";
        public static final String COLUMN_SCHULZE_RANK              = "schulze_rank";
        public static final String COLUMN_BETTER_THAN_STATUS_QUO    = "better_than_status_quo";
        public static final String COLUMN_WORSE_THAN_STATUS_QUO     = "worse_than_status_quo";
        public static final String COLUMN_REVERSE_BEAT_PATH         = "reverse_beat_path";
        public static final String COLUMN_MULTISTAGE_MAJORITY       = "multistage_majority";
        public static final String COLUMN_ELIGIBLE                  = "eligible";
        public static final String COLUMN_WINNER                    = "winner";
        public static final String COLUMN_RANK                      = "rank";

        public static final String COLUMN_META_CACHED = "meta_cached";
        
        public static final String[] COLUMNS = new String[] {
            COLUMN_ID,
            COLUMN_ISSUE_ID,
            COLUMN_NAME,
            COLUMN_DISCUSSION_URL,
            COLUMN_CREATED,
            COLUMN_REVOKED,
            COLUMN_REVOKED_BY_MEMBER_ID,
            COLUMN_SUGGESTED_INITIATIVE_ID,
            COLUMN_ADMITTED,
            COLUMN_SUPPORTER_COUNT,
            COLUMN_INFORMED_SUPPORTER_COUNT,
            COLUMN_SATISFIED_SUPPORTER_COUNT,
            COLUMN_SATISFIED_INFORMED_SUPPORTER_COUNT,
            COLUMN_POSITIVE_VOTES,
            COLUMN_NEGATIVE_VOTES,
            COLUMN_DIRECT_MAJORITY,
            COLUMN_INDIRECT_MAJORITY,
            COLUMN_SCHULZE_RANK,
            COLUMN_BETTER_THAN_STATUS_QUO,
            COLUMN_WORSE_THAN_STATUS_QUO,
            COLUMN_REVERSE_BEAT_PATH,
            COLUMN_MULTISTAGE_MAJORITY,
            COLUMN_ELIGIBLE,
            COLUMN_WINNER,
            COLUMN_RANK,
            COLUMN_META_CACHED
        };
        
        public static void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + TABLE + " (" +
                    "_id                     INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                    "issue_id         INTEGER                        , " +
                    "name             TEXT                           , " +
                    "discussion_url   TEXT                           , " +
                    "created          INTEGER                        , " +
                    "revoked          INTEGER                        , " +
                    "revoked_by_member_id INTEGER                    , " +
                    "suggested_initiative_id INTEGER                 , " +
                    "admitted         INTEGER                        , " +
                    "supporter_count  INTEGER                        , " +
                    "informed_supporter_count INTEGER                , " +
                    "satisfied_supporter_count INTEGER               , " +
                    "satisfied_informed_supporter_count INTEGER      , " +
                    "positive_votes   INTEGER                        , " +
                    "negative_votes   INTEGER                        , " +
                    "direct_majority  INTEGER                        , " +
                    "indirect_majority INTEGER                       , " +
                    "schulze_rank     INTEGER                        , " +
                    "better_than_status_quo INTEGER                  , " +
                    "worse_than_status_quo INTEGER                   , " +
                    "reverse_beat_path INTEGER                       , " +
                    "multistage_majority INTEGER                     , " +
                    "eligible         INTEGER                        , " +
                    "winner           INTEGER                        , " +
                    "rank             INTEGER                        , " +
                    "meta_cached      TIMESTAMP                        " +
                    ");");
        }
        
        public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE); onCreate(db);
        }
        
        public static class Battle {
            public static final String TABLE                        = "battle";
            public static final String COLUMN_ISSUE_ID              = "issue_id";
            public static final String COLUMN_WINNING_INITIATIVE_ID = "winning_initiative_id";
            public static final String COLUMN_LOSING_INITIATIVE_ID  = "losing_initiative_id";
            public static final String COLUMN_COUNT                 = "count";
            
            public static final String COLUMN_META_CACHED = "meta_cached";
            
            public static final String[] COLUMNS = new String[] {
                COLUMN_ISSUE_ID,
                COLUMN_WINNING_INITIATIVE_ID,
                COLUMN_LOSING_INITIATIVE_ID,
                COLUMN_COUNT,
                COLUMN_META_CACHED
            };
            
            public static void onCreate(SQLiteDatabase db) {
                db.execSQL("CREATE TABLE " + TABLE + " (" +
                        "issue_id              INTEGER                        , " +
                        "winning_initiative_id INTEGER                        , " +
                        "losing_initiative_id  INTEGER                        , " +
                        "count                 INTEGER                        , " +
                        "meta_cached           TIMESTAMP                        " +
                        ");");
            }
            
            public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
                db.execSQL("DROP TABLE IF EXISTS " + TABLE); onCreate(db);
            }
        }
        
        public static class Initiator {
            public static final String TABLE                  = "initiator";
            public static final String COLUMN_INITIATIVE_ID   = "initiative_id";
            public static final String COLUMN_MEMBER_ID       = "member_id";
            public static final String COLUMN_ACCEPTED        = "accepted";

            public static final String COLUMN_META_CACHED = "meta_cached";
            
            public static final String[] COLUMNS = new String[] {
                COLUMN_INITIATIVE_ID,
                COLUMN_MEMBER_ID,
                COLUMN_ACCEPTED,
                COLUMN_META_CACHED
            };
            
            public static void onCreate(SQLiteDatabase db) {
                db.execSQL("CREATE TABLE " + TABLE + " (" +
                        "initiative_id            INTEGER                         , " +
                        "member_id                INTEGER                         , " +
                        "accepted                 INTEGER                         , " +
                        "meta_cached              TIMESTAMP                         " +
                        ");");
            }

            public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
                db.execSQL("DROP TABLE IF EXISTS " + TABLE); onCreate(db);
            }
        }
        
        public static class Supporter {
            public static final String TABLE                  = "supporter";
            public static final String COLUMN_INITIATIVE_ID   = "initiative_id";
            public static final String COLUMN_EVENT           = "event";
            public static final String COLUMN_MEMBER_ID       = "member_id";
            public static final String COLUMN_DRAFT_ID        = "draft_id";
            public static final String COLUMN_WEIGHT          = "weight";
            public static final String COLUMN_SCOPE           = "scope";
            public static final String COLUMN_DELEGATION_MEMBER_IDS = "delegation_member_ids";
            public static final String COLUMN_INFORMED        = "informed";
            public static final String COLUMN_SATISFIED       = "satisfied";

            public static final String COLUMN_META_CACHED = "meta_cached";
            
            public static final String[] COLUMNS = new String[] {
                COLUMN_INITIATIVE_ID,
                COLUMN_EVENT,
                COLUMN_MEMBER_ID,
                COLUMN_DRAFT_ID,
                COLUMN_WEIGHT,
                COLUMN_SCOPE,
                COLUMN_DELEGATION_MEMBER_IDS,
                COLUMN_INFORMED,
                COLUMN_SATISFIED,
                COLUMN_META_CACHED
            };
            
            public static void onCreate(SQLiteDatabase db) {
                db.execSQL("CREATE TABLE " + TABLE + " (" +
                        "initiative_id            INTEGER                       , " +
                        "event                    TEXT                          , " +
                        "member_id                INTEGER                       , " +
                        "draft_id                 INTEGER                       , " +
                        "weight                   INTEGER                       , " +
                        "scope                    TEXT                          , " +
                        "delegation_member_ids    TEXT                          , " +
                        "informed                 INTEGER                       , " +
                        "satisfied                INTEGER                       , " +
                        "meta_cached              TIMESTAMP                       " +
                        ");");
            }

            public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
                db.execSQL("DROP TABLE IF EXISTS " + TABLE); onCreate(db);
            }
        }
    }
    
    public static class Issue {
        public static final String TABLE                  = "issue";
        public static final String COLUMN_ID              = "_id";
        public static final String COLUMN_AREA_ID         = "area_id";
        public static final String COLUMN_POLICY_ID       = "policy_id";
        public static final String COLUMN_STATE           = "state";
        public static final String COLUMN_CREATED         = "created";
        public static final String COLUMN_ACCEPTED        = "accepted";
        public static final String COLUMN_HALF_FROZEN     = "half_frozen";
        public static final String COLUMN_FULLY_FROZEN    = "fully_frozen";
        public static final String COLUMN_CLOSED          = "closed";
        public static final String COLUMN_RANKS_AVAILABLE = "ranks_available";
        public static final String COLUMN_CLEANED         = "cleaned";
        public static final String COLUMN_ADMISSION_TIME  = "admission_time";
        public static final String COLUMN_DISCUSSION_TIME = "discussion_time";
        public static final String COLUMN_VOTING_TIME     = "voting_time";
        public static final String COLUMN_SNAPSHOT        = "snapshot";
        public static final String COLUMN_LATEST_SNAPSHOT_EVENT = "latest_snapshot_event";
        public static final String COLUMN_POPULATION      = "population";
        public static final String COLUMN_VOTER_COUNT     = "voter_count";
        public static final String COLUMN_STATUS_QUO_SCHULZE_RANK = "status_quo_schulze_rank";
        
        public static final String COLUMN_META_CACHED = "meta_cached";
        
        public static final String[] COLUMNS = new String[] {
            COLUMN_ID,
            COLUMN_AREA_ID,
            COLUMN_POLICY_ID,
            COLUMN_STATE,
            COLUMN_CREATED,
            COLUMN_ACCEPTED,
            COLUMN_HALF_FROZEN,
            COLUMN_FULLY_FROZEN,
            COLUMN_CLOSED,
            COLUMN_RANKS_AVAILABLE,
            COLUMN_CLEANED,
            COLUMN_ADMISSION_TIME,
            COLUMN_DISCUSSION_TIME,
            COLUMN_VOTING_TIME,
            COLUMN_SNAPSHOT,
            COLUMN_LATEST_SNAPSHOT_EVENT,
            COLUMN_POPULATION,
            COLUMN_VOTER_COUNT,
            COLUMN_STATUS_QUO_SCHULZE_RANK,
            COLUMN_META_CACHED,
        };
        
        public static final String STATE_META_OPEN      = "open";
        
        public static final String STATE_ANY_PHASE      = "";
        public static final String STATE_ADMISSION      = "admission";
        public static final String STATE_DISCUSSION     = "discussion";
        public static final String STATE_VERIFICATION   = "verification";
        public static final String STATE_VOTING         = "voting";
        
        public static final String STATE_META_CLOSED    = "closed";

        public static final String STATE_ANY_STATE      = "";
        public static final String STATE_FINISHED       = "finished";
        public static final String STATE_WITH_WINNER    = "finished_with_winner";
        public static final String STATE_WITHOUT_WINNER = "finished_without_winner";
        public static final String STATE_CANCELLED      = "cancelled";

        public static void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + TABLE + " (" +
                    "_id                     INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                    "area_id                 INTEGER            , " +
                    "policy_id               INTEGER            , " +
                    "state                   TEXT               , " +
                    "created                 INTEGER            , " +
                    "accepted                INTEGER            , " +
                    "half_frozen             INTEGER            , " +
                    "fully_frozen            INTEGER            , " +
                    "closed                  INTEGER            , " +
                    "ranks_available         INTEGER            , " +
                    "cleaned                 INTEGER            , " +
                    "admission_time          TEXT               , " +
                    "discussion_time         TEXT               , " +
                    "voting_time             TEXT               , " +
                    "snapshot                INTEGER            , " +
                    "latest_snapshot_event   INTEGER            , " +
                    "population              INTEGER            , " +
                    "voter_count             INTEGER            , " +
                    "status_quo_schulze_rank INTEGER            , " +
                    "meta_cached             TIMESTAMP            " +
                    ");");
        }
        
        public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE); onCreate(db);
        }
        
        public static class Comment {
            public static final String TABLE                      = "issue_comment";
            public static final String COLUMN_ISSUE_ID            = "issue_id";
            public static final String COLUMN_MEMBER_ID           = "member_id";
            public static final String COLUMN_CHANGED             = "changed";
            public static final String COLUMN_FORMATTING_ENGINE   = "formatting_engine";
            public static final String COLUMN_CONTENT             = "content";
            
            public static final String COLUMN_META_CACHED = "meta_cached";
            
            public static final String[] COLUMNS = new String[] {
                COLUMN_ISSUE_ID,
                COLUMN_MEMBER_ID,
                COLUMN_CHANGED,
                COLUMN_FORMATTING_ENGINE,
                COLUMN_CONTENT,
                COLUMN_META_CACHED 
            };
            
            public static void onCreate(SQLiteDatabase db) {
                db.execSQL("CREATE TABLE " + TABLE + " (" +
                        "issue_id             INTEGER                      , " +
                        "member_id            INTEGER                      , " +
                        "changed              INTEGER                      , " +
                        "formatting_engine    TEXT                         , " +
                        "content              TEXT                         , " +
                        "meta_cached          TIMESTAMP                      " +
                        ");");
            }

            public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
                db.execSQL("DROP TABLE IF EXISTS " + TABLE); onCreate(db);
            }
        }

        public static class Interest {
            public static final String TABLE                      = "interest";
            public static final String COLUMN_ISSUE_ID            = "issue_id";
            public static final String COLUMN_EVENT               = "event";
            public static final String COLUMN_MEMBER_ID           = "member_id";
            public static final String COLUMN_WEIGHT              = "weight";
            public static final String COLUMN_SCOPE               = "scope";
            public static final String COLUMN_DELEGATE_MEMBER_IDS = "delegate_member_ids";

            public static final String COLUMN_META_CACHED = "meta_cached";
            
            public static final String[] COLUMNS = new String[] {
                COLUMN_ISSUE_ID,
                COLUMN_EVENT,
                COLUMN_MEMBER_ID,
                COLUMN_WEIGHT,
                COLUMN_SCOPE,
                COLUMN_DELEGATE_MEMBER_IDS,
                COLUMN_META_CACHED
            };
            
            public static void onCreate(SQLiteDatabase db) {
                db.execSQL("CREATE TABLE " + TABLE + " (" +
                        "issue_id            INTEGER                       , " +
                        "event               TEXT                          , " +
                        "member_id           INTEGER                       , " +
                        "weight              INTEGER                       , " +
                        "scope               TEXT                          , " +
                        "delegate_member_ids TEXT                          , " +
                        "meta_cached         TIMESTAMP                       " +
                        ");");
            }

            public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
                db.execSQL("DROP TABLE IF EXISTS " + TABLE); onCreate(db);
            }
        }
        
        public static class Population {
            public static final String TABLE = "population";
            public static final String COLUMN_ISSUE_ID  = "issue_id";
            public static final String COLUMN_EVENT     = "event";
            public static final String COLUMN_MEMBER_ID = "member_id";
            public static final String COLUMN_WEIGHT    = "weight";
            public static final String COLUMN_SCOPE     = "scope";
            public static final String COLUMN_DELEGATE_MEMBER_IDS = "delegate_member_ids";

            public static final String COLUMN_META_CACHED = "meta_cached";

            public static final String[] COLUMNS = new String[] {
                COLUMN_ISSUE_ID,
                COLUMN_EVENT,
                COLUMN_MEMBER_ID,
                COLUMN_WEIGHT,
                COLUMN_SCOPE,
                COLUMN_DELEGATE_MEMBER_IDS,
                COLUMN_META_CACHED
            };
            
            public static void onCreate(SQLiteDatabase db) {
                db.execSQL("CREATE TABLE " + TABLE + " (" +
                    "issue_id          INTEGER                        , " +
                    "event             TEXT                           , " +
                    "member_id         INTEGER                        , " +
                    "weight            INTEGER                        , " +
                    "scope             TEXT                           , " +
                    "meta_cached       TIMESTAMP                        " +
                    ");");
            }

            public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
                db.execSQL("DROP TABLE IF EXISTS " + TABLE); onCreate(db);
            }
        }
    }
    
    public static class Opinion {
        public static final String TABLE = "opinion";
        public static final String COLUMN_SUGGESTION_ID = "suggestion_id";
        public static final String COLUMN_MEMBER_ID = "member_id";
        public static final String COLUMN_DEGREE = "degree";
        public static final String COLUMN_FULFILLED = "fulfilled";

        public static final String COLUMN_META_CACHED = "meta_cached";

        public static final String[] COLUMNS = new String[] {
            COLUMN_SUGGESTION_ID,
            COLUMN_MEMBER_ID,
            COLUMN_DEGREE,
            COLUMN_FULFILLED,
            COLUMN_META_CACHED
        };
        
        public static void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + TABLE + " (" +
                    "suggestion_id    INTEGER                        , " +
                    "member_id        INTEGER                        , " +
                    "degree           INTEGER                        , " +
                    "fulfilled        INTEGER                        , " +
                    "meta_cached      TIMESTAMP                        " +
                    ");");
        }
        
        public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE); onCreate(db);
        }
    }
    
    public static class Policy {
        public static final String TABLE = "policy";
        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_ACTIVE = "active";
        public static final String COLUMN_INDEX = "idx";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_ADMISSION_TIME = "admission_time";
        public static final String COLUMN_DISCUSSION_TIME = "discussion_time";
        public static final String COLUMN_VERIFICATION_TIME = "verification_time";
        public static final String COLUMN_VOTING_TIME = "voting_time";
        public static final String COLUMN_ISSUE_QUORUM_NUM = "issue_quorum_num";
        public static final String COLUMN_ISSUE_QUORUM_DEN = "issue_quorum_den";
        public static final String COLUMN_INITIATIVE_QUORUM_NUM = "initiative_quorum_num";
        public static final String COLUMN_INITIATIVE_QUORUM_DEN = "initiative_quorum_den";
        public static final String COLUMN_DIRECT_MAJORITY_NUM = "direct_majority_num";
        public static final String COLUMN_DIRECT_MAJORITY_DEN = "direct_majority_den";
        public static final String COLUMN_DIRECT_MAJORITY_STRICT = "direct_majority_strict";
        public static final String COLUMN_DIRECT_MAJORITY_POSITIVE = "direct_majority_positive";
        public static final String COLUMN_DIRECT_MAJORITY_NEGATIVE = "direct_majority_negative";
        public static final String COLUMN_INDIRECT_MAJORITY_NUM = "indirect_majority_num";
        public static final String COLUMN_INDIRECT_MAJORITY_DEN = "indirect_majority_den";
        public static final String COLUMN_INDIRECT_MAJORITY_STRICT = "indirect_majority_strict";
        public static final String COLUMN_INDIRECT_MAJORITY_POSITIVE = "indirect_majority_positive";
        public static final String COLUMN_INDIRECT_MAJORITY_NEGATIVE = "indirect_majority_negative";
        public static final String COLUMN_NO_REVERSE_BEATPATH = "no_reverse_beatpath";
        public static final String COLUMN_NO_MULTISTAGE_MAJORITY = "no_multistage_majority";

        public static final String COLUMN_META_CACHED = "meta_cached";
        
        public static final String[] COLUMNS = new String[] {
            COLUMN_ID,
            COLUMN_ACTIVE,
            COLUMN_INDEX,
            COLUMN_NAME,
            COLUMN_DESCRIPTION,
            COLUMN_ADMISSION_TIME,
            COLUMN_DISCUSSION_TIME,
            COLUMN_VERIFICATION_TIME,
            COLUMN_VOTING_TIME,
            COLUMN_ISSUE_QUORUM_DEN,
            COLUMN_ISSUE_QUORUM_NUM,
            COLUMN_INITIATIVE_QUORUM_DEN,
            COLUMN_INITIATIVE_QUORUM_NUM,
            COLUMN_DIRECT_MAJORITY_NUM,
            COLUMN_DIRECT_MAJORITY_DEN,
            COLUMN_DIRECT_MAJORITY_STRICT,
            COLUMN_DIRECT_MAJORITY_POSITIVE,
            COLUMN_DIRECT_MAJORITY_NEGATIVE,
            COLUMN_INDIRECT_MAJORITY_NUM,
            COLUMN_INDIRECT_MAJORITY_DEN,
            COLUMN_INDIRECT_MAJORITY_STRICT,
            COLUMN_INDIRECT_MAJORITY_POSITIVE,
            COLUMN_INDIRECT_MAJORITY_NEGATIVE,
            COLUMN_NO_REVERSE_BEATPATH,
            COLUMN_NO_MULTISTAGE_MAJORITY,
            COLUMN_META_CACHED
        };

        public static void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + TABLE + " (" +
                    "_id                     INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                    "active           INTEGER                        , " +
                    "idx              TEXT                           , " + 
                    "name             TEXT                           , " +
                    "description      TEXT                           , " +
                    "admission_time   TEXT                           , " +
                    "discussion_time  TEXT                           , " +
                    "verification_time TEXT                          , " +
                    "voting_time       TEXT                          , " +
                    "issue_quorum_num  TEXT                          , " +
                    "issue_quorum_den  TEXT                          , " +
                    "initiative_quorum_num  TEXT                     , " +
                    "initiative_quorum_den  TEXT                     , " +
                    "direct_majority_num        INTEGER              , " +
                    "direct_majority_den        INTEGER              , " +
                    "direct_majority_strict     INTEGER              , " +
                    "direct_majority_positive   INTEGER              , " +
                    "direct_majority_negative   INTEGER              , " +
                    "indirect_majority_num      INTEGER              , " +
                    "indirect_majority_den      INTEGER              , " +
                    "indirect_majority_strict   INTEGER              , " +
                    "indirect_majority_positive INTEGER              , " +
                    "indirect_majority_negative INTEGER              , " +
                    "no_reverse_beatpath        INTEGER              , " +
                    "no_multistage_majority     INTEGER              , " +
                    "meta_cached                TIMESTAMP              " +
                    ");");
        }
        
        public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE); onCreate(db);
        }
    }
    
    public static class Privilege {
        public static final String TABLE = "privilege";
        public static final String COLUMN_UNIT_ID = "unit_id";
        public static final String COLUMN_MEMBER_ID = "member_id";
        public static final String COLUMN_ADMIN_MANAGER = "admin_manager";
        public static final String COLUMN_UNIT_MANAGER = "unit_manager";
        public static final String COLUMN_AREA_MANAGER = "area_manager";
        public static final String COLUMN_VOTING_RIGHT_MANAGER = "voting_right_manager";
        public static final String COLUMN_VOTING_RIGHT = "voting_right";

        public static final String COLUMN_META_CACHED = "meta_cached";

        public static final String[] COLUMNS = new String[] {
            COLUMN_UNIT_ID,
            COLUMN_MEMBER_ID,
            COLUMN_ADMIN_MANAGER,
            COLUMN_UNIT_MANAGER,
            COLUMN_AREA_MANAGER,
            COLUMN_VOTING_RIGHT_MANAGER,
            COLUMN_VOTING_RIGHT,
            COLUMN_META_CACHED
        };
            
        public static void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + TABLE + " (" +
                    "unit_id          INTEGER                        , " +
                    "member_id        INTEGER                        , " +
                    "admin_manager    STRING                         , " +
                    "unit_manager     STRING                         , " +
                    "area_manager     STRING                         , " +
                    "voting_right_manager STRING                     , " +
                    "voting_right     STRING                         , " +
                    "meta_cached      TIMESTAMP                        " +
                    ");");
        }
        
        public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE); onCreate(db);
        }
    }
    
    public static class Suggestion {
        public static final String TABLE = "suggestion";
        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_CREATED = "created";
        public static final String COLUMN_AUTHOR_ID = "author_id";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_FORMATTING_ENGINE = "formatting_engine";
        public static final String COLUMN_CONTENT = "content";
        public static final String COLUMN_MINUS2_UNFULFILLED_COUNT = "minus2_unfulfilled_count";
        public static final String COLUMN_MINUS2_FULFILLED_COUNT = "minus2_fulfilled_count";
        public static final String COLUMN_MINUS1_UNFULFILLED_COUNT = "minus1_unfulfilled_count";
        public static final String COLUMN_MINUS1_FULFILLED_COUNT = "minus1_fulfilled_count";
        public static final String COLUMN_PLUS1_UNFULFILLED_COUNT = "plus1_unfulfilled_count";
        public static final String COLUMN_PLUS1_FULFILLED_COUNT = "plus1_fulfilled_count";
        public static final String COLUMN_PLUS2_UNFULFILLED_COUNT = "plus2_unfulfilled_count";
        public static final String COLUMN_PLUS2_FULFILLED_COUNT = "plus2_fulfilled_count";

        public static final String COLUMN_META_CACHED = "meta_cached";

        public static final String[] COLUMNS = new String[] {
            COLUMN_ID,
            COLUMN_CREATED,
            COLUMN_AUTHOR_ID,
            COLUMN_NAME,
            COLUMN_FORMATTING_ENGINE,
            COLUMN_CONTENT,
            COLUMN_MINUS2_UNFULFILLED_COUNT,
            COLUMN_MINUS2_FULFILLED_COUNT,
            COLUMN_MINUS1_UNFULFILLED_COUNT,
            COLUMN_MINUS1_FULFILLED_COUNT,
            COLUMN_PLUS1_UNFULFILLED_COUNT,
            COLUMN_PLUS1_FULFILLED_COUNT,
            COLUMN_PLUS2_UNFULFILLED_COUNT,
            COLUMN_PLUS2_FULFILLED_COUNT,
            COLUMN_META_CACHED
        };

        public static void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + TABLE + " (" +
                    "_id                     INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                    "created          INTEGER                        , " +
                    "author_id        INTEGER                        , " +
                    "name             TEXT                           , " +
                    "formatting_engine TEXT                          , " +
                    "content          TEXT                           , " +
                    "minus2_unfulfilled_count   INTEGER              , " +
                    "minus2_fulfilled_count     INTEGER              , " +
                    "minus1_unfulfilled_count   INTEGER              , " +
                    "minus1_fulfilled_count     INTEGER              , " +
                    "plus1_unfulfilled_count    INTEGER              , " +
                    "plus1_fulfilled_count      INTEGER              , " + 
                    "plus2_unfulfilled_count    INTEGER              , " +
                    "plus2_fulfilled_count      INTEGER              , " +
                    "meta_cached                TIMESTAMP              " +
                    ");");
        }
        
        public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE); onCreate(db);
        }
    }
    
    public static class Member {
        public static final String TABLE = "member";
        public static final String COLUMN_ID             = "_id";
        public static final String COLUMN_ACTIVATED      = "activated";
        public static final String COLUMN_LAST_ACTIVITY  = "last_activity";
        public static final String COLUMN_LAST_LOGIN     = "last_login";
        public static final String COLUMN_LOCKED         = "locked";
        public static final String COLUMN_ACTIVE         = "active";
        public static final String COLUMN_NAME           = "name";
        public static final String COLUMN_IDENTIFICATION = "identification";
        public static final String COLUMN_ORGANIZATIONAL_UNIT = "organizational_unit";
        public static final String COLUMN_INTERNAL_POSTS = "internal_posts";
        public static final String COLUMN_REAL_NAME      = "real_name";
        public static final String COLUMN_BIRTHDAY       = "birthday";
        public static final String COLUMN_ADDRESS        = "address";
        public static final String COLUMN_EMAIL          = "email";
        public static final String COLUMN_XMPP_ADDRESS   = "xmpp_address";
        public static final String COLUMN_WEBSITE        = "website";
        public static final String COLUMN_PHONE          = "phone";
        public static final String COLUMN_MOBILE_PHONE   = "mobile_phone";
        public static final String COLUMN_PROFESSION     = "profession";
        public static final String COLUMN_EXTERNAL_MEMBERSHIPS = "external_memberships";
        public static final String COLUMN_EXTERNAL_POSTS = "external_posts";
        public static final String COLUMN_FORMATTING_ENGINE = "formatting_engine";
        public static final String COLUMN_STATEMENT      = "statement";

        public static final String COLUMN_META_CACHED = "meta_cached";

        public static final String[] COLUMNS = new String[] {
            COLUMN_ID,
            COLUMN_ACTIVATED,
            COLUMN_LAST_ACTIVITY,
            COLUMN_LAST_LOGIN,
            COLUMN_LOCKED,
            COLUMN_ACTIVE,
            COLUMN_NAME,
            COLUMN_IDENTIFICATION,
            COLUMN_ORGANIZATIONAL_UNIT,
            COLUMN_INTERNAL_POSTS,
            COLUMN_REAL_NAME,
            COLUMN_BIRTHDAY,
            COLUMN_ADDRESS,
            COLUMN_EMAIL,
            COLUMN_XMPP_ADDRESS,
            COLUMN_WEBSITE,
            COLUMN_PHONE,
            COLUMN_MOBILE_PHONE,
            COLUMN_PROFESSION,
            COLUMN_EXTERNAL_MEMBERSHIPS,
            COLUMN_EXTERNAL_POSTS,
            COLUMN_FORMATTING_ENGINE,
            COLUMN_STATEMENT,
            COLUMN_META_CACHED
        };
        
        public static void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + TABLE + " (" +
                    "_id                     INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                    "activated               INTEGER            , " +
                    "last_activity           INTEGER            , " +
                    "last_login              INTEGER            , " +
                    "locked                  INTEGER            , " +
                    "active                  INTEGER            , " +
                    "name                    TEXT               , " +
                    "identification          TEXT               , " +
                    "organizational_unit     TEXT               , " +
                    "internal_posts          TEXT               , " +
                    "real_name               TEXT               , " +
                    "birthday                INTEGER            , " +
                    "address                 TEXT               , " +
                    "email                   TEXT               , " +
                    "xmpp_address            TEXT               , " +
                    "website                 TEXT               , " +
                    "phone                   TEXT               , " +
                    "mobile_phone            TEXT               , " +
                    "profession              TEXT               , " +
                    "external_memberships    TEXT               , " +
                    "external_posts          TEXT               , " +
                    "formatting_engine       TEXT               , " +
                    "statement               TEXT               , " +
                    "meta_cached             TIMESTAMP            " +
                    ");");
        }
        
        public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE); onCreate(db);
        }
        
        public static class Contact {
            public static final String TABLE = "contact";
            public static final String COLUMN_MEMBER_ID       = "member_id";
            public static final String COLUMN_OTHER_MEMBER_ID = "other_member_id";
            public static final String COLUMN_PUBLIC          = "public";

            public static final String COLUMN_META_CACHED = "meta_cached";

            public static final String[] COLUMNS = new String[] {
                COLUMN_MEMBER_ID,
                COLUMN_OTHER_MEMBER_ID,
                COLUMN_PUBLIC,
                COLUMN_META_CACHED
            };
            
            public static void onCreate(SQLiteDatabase db) {
                db.execSQL("CREATE TABLE " + TABLE + " (" +
                    "member_id               INTEGER             , " +
                    "other_member_id         INTEGER             , " +
                    "public                  INTEGER             , " +
                    "meta_cached             TIMESTAMP             " +
                    ");");
            }

            public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
                db.execSQL("DROP TABLE IF EXISTS " + TABLE); onCreate(db);
            }
        }
        
        public static class Image {
            public static final String TABLE = "member_image";
            public static final String COLUMN_MEMBER_ID    = "member_id";
            public static final String COLUMN_IMAGE_TYPE   = "image_type";
            public static final String COLUMN_SCALED       = "scaled";
            public static final String COLUMN_CONTENT_TYPE = "content_type";
            public static final String COLUMN_DATA         = "data";
            
            public static final String COLUMN_META_CACHED = "meta_cached";

            public static final String[] COLUMNS = new String[] {
                COLUMN_MEMBER_ID,
                COLUMN_IMAGE_TYPE,
                COLUMN_SCALED,
                COLUMN_CONTENT_TYPE,
                COLUMN_DATA,
                COLUMN_META_CACHED
            };

            public static void onCreate(SQLiteDatabase db) {
                db.execSQL("CREATE TABLE " + TABLE + " (" +
                        "member_id          INTEGER              , " +
                        "image_type         TEXT                 , " +
                        "scaled             INTEGER              , " +
                        "content_type       TEXT                 , " +
                        "data               TEXT                 , " +
                        "meta_cached        TIMESTAMP              " +
                        ");");
            }

            public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
                db.execSQL("DROP TABLE IF EXISTS " + TABLE); onCreate(db);
            }
        }
    }
    
    public static class Membership {
        public static final String TABLE = "membership";
        public static final String COLUMN_AREA_ID   = "area_id";
        public static final String COLUMN_MEMBER_ID = "member_id";
        
        public static final String COLUMN_META_CACHED = "meta_cached";

        public static final String[] COLUMNS = new String[] {
            COLUMN_AREA_ID,
            COLUMN_MEMBER_ID,
            COLUMN_META_CACHED
        };

        public static void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + TABLE + " (" +
                    "area_id                 INTEGER NOT NULL    , " +
                    "member_id               INTEGER NOT NULL    , " +
                    "meta_cached             TIMESTAMP             " +
                    ");");
        }
        
        public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE); onCreate(db);
        }
    }
    
    public static class Unit {
        public static final String TABLE = "unit";
        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_PARENT_ID = "parent_id";
        public static final String COLUMN_ACTIVE = "active";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_MEMBER_COUNT = "member_count";

        public static final String COLUMN_META_CACHED = "meta_cached";

        public static final String[] COLUMNS = new String[] {
            COLUMN_ID,
            COLUMN_PARENT_ID,
            COLUMN_ACTIVE,
            COLUMN_NAME,
            COLUMN_DESCRIPTION,
            COLUMN_MEMBER_COUNT,
            COLUMN_META_CACHED
        };
        
        public static void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + TABLE + " (" +
                    "_id                     INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                    "parent_id               INTEGER                                   , " +
                    "active                  BOOLEAN                                   , " +
                    "name                    TEXT                                      , " +
                    "description             TEXT                                      , " +
                    "member_count            INTEGER                                   , " +
                    "meta_cached             TIMESTAMP                                   " +
                    ");");
        }
        
        public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE); onCreate(db);
        }
    }
    
    public static class Vote {
        public static final String TABLE = "vote";
        public static final String COLUMN_ISSUE_ID = "issue_id";
        public static final String COLUMN_INITIATIVE_ID = "initiative_id";
        public static final String COLUMN_MEMBER_ID = "member_id";
        public static final String COLUMN_GRADE = "grade";

        public static final String COLUMN_META_CACHED = "meta_cached";

        public static final String[] COLUMNS = new String[] {
            COLUMN_ISSUE_ID,
            COLUMN_INITIATIVE_ID,
            COLUMN_MEMBER_ID,
            COLUMN_GRADE,
            COLUMN_META_CACHED
        };

        public static void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + TABLE + " (" +
                    "issue_id                INTEGER             , " +
                    "initiative_id           INTEGER             , " +
                    "member_id               INTEGER             , " +
                    "grade                   INTEGER             , " +
                    "meta_cached             TIMESTAMP             " +
                    ");");
        }

        public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE); onCreate(db);
        }
        
        public static class Comment {
            public static final String TABLE = "voting_comment";
            public static final String COLUMN_ISSUE_ID          = "issue_id";
            public static final String COLUMN_MEMBER_ID         = "member_id";
            public static final String COLUMN_CHANGED           = "changed";
            public static final String COLUMN_FORMATTING_ENGINE = "formatting_engine";
            public static final String COLUMN_CONTENT           = "content";
            
            public static final String COLUMN_META_CACHED = "meta_cached";
            
            public static final String[] COLUMNS = new String[] {
                COLUMN_ISSUE_ID,
                COLUMN_MEMBER_ID,
                COLUMN_CHANGED,
                COLUMN_FORMATTING_ENGINE,
                COLUMN_CONTENT,
                Vote.COLUMN_META_CACHED
            };

            public static void onCreate(SQLiteDatabase db) {
                db.execSQL("CREATE TABLE " + TABLE + " (" +
                        "issue_id                INTEGER             , " +
                        "member_id               INTEGER             , " +
                        "changed                 TIMESTAMP           , " +
                        "formatting_engine       TEXT                , " +
                        "content                 TEXT                , " +
                        "meta_cached             TIMESTAMP             " +
                        ");");
            }

            public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
                db.execSQL("DROP TABLE IF EXISTS " + TABLE); onCreate(db);
            }
        }
        
        public static class Voter {
            public static final String TABLE = "voter";
            public static final String COLUMN_ISSUE_ID  = "issue_id";
            public static final String COLUMN_MEMBER_ID = "member_id";
            public static final String COLUMN_WEIGHT    = "weight";
            
            public static final String COLUMN_META_CACHED = "meta_cached";
            
            public static final String[] COLUMNS = new String[] {
                COLUMN_ISSUE_ID,
                COLUMN_MEMBER_ID,
                COLUMN_WEIGHT,
                COLUMN_META_CACHED
            };

            public static void onCreate(SQLiteDatabase db) {
                db.execSQL("CREATE TABLE " + TABLE + " (" +
                        "issue_id                INTEGER             , " +
                        "member_id               INTEGER             , " +
                        "weight                  INTEGER             , " +
                        "meta_cached             TIMESTAMP             " +
                        ");");
            }

            public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
                db.execSQL("DROP TABLE IF EXISTS " + TABLE); onCreate(db);
            }
        }
        
        public static class NonVoter {
            public static final String TABLE = "non_voter";
            public static final String COLUMN_ISSUE_ID = "issue_id";
            
            public static final String COLUMN_META_CACHED = "meta_cached";
            
            public static final String[] COLUMNS = new String[] {
                COLUMN_ISSUE_ID,
                COLUMN_META_CACHED
            };

            public static void onCreate(SQLiteDatabase db) {
                db.execSQL("CREATE TABLE " + TABLE + " (" +
                        "issue_id                INTEGER             , " +
                        "meta_cached             TIMESTAMP             " +
                        ");");
            }

            public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
                db.execSQL("DROP TABLE IF EXISTS " + TABLE); onCreate(db);
            }
        }
        
        public static class DelegatingVoter {
            public static final String TABLE = "delegating_voter";
            public static final String COLUMN_ISSUE_ID            = "issue_id";
            public static final String COLUMN_MEMBER_ID           = "member_id";
            public static final String COLUMN_WEIGHT              = "weight";
            public static final String COLUMN_SCOPE               = "scope";
            public static final String COLUMN_DELEGATE_MEMBER_IDS = "delegate_member_ids";
            
            public static final String COLUMN_META_CACHED         = "meta_cached";
            
            public static final String[] COLUMNS = new String[] {
                COLUMN_ISSUE_ID,
                COLUMN_MEMBER_ID,
                COLUMN_WEIGHT,
                COLUMN_SCOPE,
                COLUMN_DELEGATE_MEMBER_IDS,
                COLUMN_META_CACHED
            };

            public static void onCreate(SQLiteDatabase db) {
                db.execSQL("CREATE TABLE " + TABLE + " (" +
                        "issue_id                INTEGER             , " +
                        "member_id               INTEGER             , " +
                        "weight                  INTEGER             , " +
                        "scope                   TEXT                , " +
                        "delegate_member_ids     TEXT                , " +
                        "meta_cached             TIMESTAMP             " +
                        ");");
            }

            public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
                db.execSQL("DROP TABLE IF EXISTS " + TABLE); onCreate(db);
            }
        }
    }
    
    public DB(Context connection, String databaseName) {
        super(connection, databaseName, null, VERSION);
        mDatabaseName = databaseName;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        
        // system tables
        Updated.onCreate(db);

        // schema tables
        Area.onCreate(db);
        Delegation.onCreate(db);
        Draft.onCreate(db);
        Event.onCreate(db);
        Initiative.onCreate(db);
        Initiative.Battle.onCreate(db);
        Initiative.Initiator.onCreate(db);
        Initiative.Supporter.onCreate(db);
        Issue.onCreate(db);
        Issue.Interest.onCreate(db);
        Issue.Comment.onCreate(db);
        Opinion.onCreate(db);
        Policy.onCreate(db);
        Privilege.onCreate(db);
        Suggestion.onCreate(db);
        Member.onCreate(db);
        Member.Contact.onCreate(db);
        Member.Image.onCreate(db);
        Membership.onCreate(db);
        Unit.onCreate(db);
        Vote.onCreate(db);
        Vote.Comment.onCreate(db);
        Vote.DelegatingVoter.onCreate(db);
        Vote.Voter.onCreate(db);
        Vote.NonVoter.onCreate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int ov, int nv) {
        // system tables
        Updated.onUpgrade(db, ov, nv);
        
        // schema tables
        Area.onUpgrade(db, ov, nv);
        Delegation.onUpgrade(db, ov, nv);
        Draft.onUpgrade(db, ov, nv);
        Event.onUpgrade(db, ov, nv);
        Initiative.onUpgrade(db, ov, nv);
        Initiative.Battle.onUpgrade(db, ov, nv);
        Initiative.Initiator.onUpgrade(db, ov, nv);
        Initiative.Supporter.onUpgrade(db, ov, nv);
        Issue.onUpgrade(db, ov, nv);
        Issue.Interest.onUpgrade(db, ov, nv);
        Issue.Comment.onUpgrade(db, ov, nv);
        Opinion.onUpgrade(db, ov, nv);
        Policy.onUpgrade(db, ov, nv);
        Privilege.onUpgrade(db, ov, nv);
        Suggestion.onUpgrade(db, ov, nv);
        Member.onUpgrade(db, ov, nv);
        Member.Contact.onUpgrade(db, ov, nv);
        Member.Image.onUpgrade(db, ov, nv);
        Membership.onUpgrade(db, ov, nv);
        Unit.onUpgrade(db, ov, nv);
        Vote.onUpgrade(db, ov, nv);
        Vote.Comment.onUpgrade(db, ov, nv);
        Vote.DelegatingVoter.onUpgrade(db, ov, nv);
        Vote.Voter.onUpgrade(db, ov, nv);
        Vote.NonVoter.onUpgrade(db, ov, nv);
    }
    
    public String getDatabaseName() {
        return mDatabaseName;
    }
}
