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

import lfapi.v2.schema.Area;
import lfapi.v2.schema.Issue;
import lfapi.v2.schema.Unit;

/**
 * The Class AreaUnitHolder.
 */
public class AreaUnitIssueHolder implements Comparable<AreaUnitIssueHolder> {

    public boolean selected = false;
    
    public Area area;
    
    public Unit unit;
    
    public Issue issue;

    /**
     * Instantiates a new area unit holder.
     *
     * @param area the area
     */
    public AreaUnitIssueHolder(Area area) {
        this.area = area;
    }

    /**
     * Instantiates a new area unit holder.
     *
     * @param unit the unit
     */
    public AreaUnitIssueHolder(Unit unit) {
        this.unit = unit;
    }

    /**
     * Instantiates a new area unit holder.
     *
     * @param area the area
     * @param unit the unit
     */
    public AreaUnitIssueHolder(Area area, Unit unit) {
        this.area = area;
        this.unit = unit;
    }
    
    public AreaUnitIssueHolder(Area area, Unit unit, Issue issue) {
        this.area = area;
        this.unit = unit;
        this.issue = issue;
    }

    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(AreaUnitIssueHolder another) {
        return unit.id.compareTo(another.unit.id);
    }
}
