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
 * Passed to the constructor of TagHandler, it defines the document definition
 * of the handler we are instantiating
 * 
 * @see com.sun.facelets.tag.TagHandler
 * @author Jacob Hookom
 * @version $Id: TagConfig.java,v 1.1 2005/05/21 17:54:39 jhook Exp $
 */
public interface TagConfig {

    /**
     * A Tag representing this handler
     * 
     * @return a tag representing this handler
     */
    public Tag getTag();

    /**
     * The next FaceletHandler (child or children) to be applied
     * 
     * @return next FaceletHandler, never null
     */
    public FaceletHandler getNextHandler();

    /**
     * A document-unique id, follows the convention "_tagId##"
     * 
     * @return a document-unique id
     */
    public String getTagId();
}
