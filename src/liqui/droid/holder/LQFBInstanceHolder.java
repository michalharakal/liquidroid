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

package liqui.droid.holder;

/**
 * The Class LQFBInstanceHolder.
 */
public class LQFBInstanceHolder {
    
    public String name;
    
    public String prefKey;
    
    public String apiUrl;
    
    public String webUrl;

    /**
     * Instantiates a new lQFB instance holder.
     *
     * @param name the name
     * @param prefKey the pref key
     * @param apiUrl the api url
     * @param webUrl the web url
     */
    public LQFBInstanceHolder(String name, String prefKey, String apiUrl, String webUrl) {
        this.name = name;
        this.prefKey = prefKey;
        this.apiUrl = apiUrl;
        this.webUrl = webUrl;
    }
}
