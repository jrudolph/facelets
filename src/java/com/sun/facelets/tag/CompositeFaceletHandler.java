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

package com.sun.facelets.tag;

import com.sun.facelets.FaceletHandler;

/**
 * A FaceletHandler that is derived of 1 or more, inner FaceletHandlers. This
 * interface would be found if the next FaceletHandler is structually, a body
 * with multiple child elements as defined in XML.
 * 
 * @author Jacob Hookom
 * @version $Id: CompositeFaceletHandler.java,v 1.1 2005/05/21 17:54:37 jhook Exp $
 */
public interface CompositeFaceletHandler extends FaceletHandler {
    /**
     * Inner FaceletHandlers this handler delegates to
     * 
     * @return an array of 0 or more in length, never null.
     */
    public FaceletHandler[] getHandlers();
}
