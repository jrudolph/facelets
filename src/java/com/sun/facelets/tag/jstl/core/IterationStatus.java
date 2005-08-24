/**
 * Licensed under the Common Development and Distribution License,
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.sun.com/cddl/
 *   
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or 
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package com.sun.facelets.tag.jstl.core;

import java.io.Serializable;

/**
 * @author Jacob Hookom
 * @version $Id: IterationStatus.java,v 1.2 2005/08/24 04:38:52 jhook Exp $
 */
public final class IterationStatus implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private final int index;

    private final int begin;

    private final int end;

    private final int step;

    /**
     * 
     */
    public IterationStatus(int index, int begin, int end, int step) {
        this.index = index;
        this.begin = begin;
        this.end = end;
        this.step = step;
    }

    public boolean isFirst() {
        return index == begin;
    }

    public boolean isLast() {
        return index == end;
    }

    public int getBegin() {
        return begin;
    }

    public int getEnd() {
        return end;
    }

    public int getIndex() {
        return index;
    }

    public int getStep() {
        return step;
    }

}
