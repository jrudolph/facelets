/**
 * Copyright 2005 Sun Microsystems, Inc. All rights reserved.
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

package com.sun.facelets.tag.html;

import com.sun.facelets.tag.ComponentConfig;
import com.sun.facelets.tag.ComponentHandler;

/**
 * @author Jacob Hookom
 * @version $Id: HtmlComponentHandler.java,v 1.1 2005/05/21 17:54:41 jhook Exp $
 */
public class HtmlComponentHandler extends ComponentHandler {

    /**
     * @param config
     */
    public HtmlComponentHandler(ComponentConfig config) {
        super(config);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sun.facelets.tag.ObjectHandler#transformAttribute(java.lang.Object,
     *      java.lang.String)
     */
    protected String transformAttribute(Object obj, String n) {
        if ("class".equals(n)) {
            return "styleClass";
        }
        return super.transformAttribute(obj, n);
    }

}
