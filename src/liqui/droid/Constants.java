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

package liqui.droid;

import liqui.droid.holder.LQFBInstanceHolder;

/**
 * The Interface Constants.
 */
public interface Constants {
    
    public interface Account {
        public static final String API_NAME    = "API_NAME";
        public static final String API_URL     = "API_URL";
        public static final String MEMBER_ID   = "MEMBER_ID";
        public static final String SESSION_KEY = "SESSION_KEY";
    }
    
    public static final Integer LIMIT = 100;

    /** The Constant API_TEST. */
    public static final String API_TEST = "http://apitest.liquidfeedback.org:25520/";

    /** The Constant WEB_TEST. */
    public static final String WEB_TEST = "http://dev.liquidfeedback.org/lf2/";

    /** The Constant PREF_NAME. */
    public static final String API_PREF = "lqfb-v2-test-pref";

    /** The Constant API. */
    public static final LQFBInstanceHolder API = new LQFBInstanceHolder("LQFB V2 Test", API_PREF,
            API_TEST, WEB_TEST);

    /** The Constant LOG_TAG. */
    public static final String LOG_TAG = "LQFB";

    /** The Constant DATA_BUNDLE. */
    public static final String DATA_BUNDLE = "DATA_BUNDLE";

    /** The Constant ACTIONBAR_TITLE. */
    public static final String ACTIONBAR_TITLE = "ACTIONBAR_TITLE";

    /** The Constant SUBTITLE. */
    public static final String SUBTITLE = "SUBTITLE";

    /** The Constant EXPLORE. */
    public static final String EXPLORE = "EXPLORE";
    
    /**
     * Event properties.
     */
    public interface Event {
        public static final String ID = "_id";
    }

    /**
     * User properties.
     */
    public interface Member {

        public static final String ID = "_id";

        /** The Constant PASSWORD. */
        public static final String KEY = "MEMBER_KEY";

        /** The Constant LOGIN. */
        public static final String LOGIN = "MEMBER_LOGIN";

        /** The Constant NAME. */
        public static final String NAME = "MEMBER_NAME";
    }

    /**
     *  Area properties.
     */
    public interface Area {
        public static final String ID = "_id";
        public static final String UNIT_ID = "unit_id";
        public static final String ACTIVE = "active";
        public static final String NAME = "name";
        public static final String DESCRIPTION = "description";
        public static final String DIRECT_MEMBER_COUNT = "direct_member_count";
        public static final String MEMBER_WEIGHT = "member_weight";
    }
    
    /**
     * Issue properties.
     */
    public interface Issue {
        
        public static final String ID = "_id";
    }

}
