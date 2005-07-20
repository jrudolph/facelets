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
 * Foundation class for FaceletHandlers associated with markup in a Facelet
 * document.
 * 
 * @author Jacob Hookom
 * @version $Id: TagHandler.java,v 1.2 2005/07/20 06:37:07 jhook Exp $
 */
public abstract class TagHandler implements FaceletHandler {

    protected final String tagId;

    protected final Tag tag;

    protected final FaceletHandler nextHandler;

    public TagHandler(TagConfig config) {
        this.tagId = config.getTagId();
        this.tag = config.getTag();
        this.nextHandler = config.getNextHandler();
    }

    /**
     * Utility method for fetching the appropriate TagAttribute
     * 
     * @param localName
     *            name of attribute
     * @return TagAttribute if found, otherwise null
     */
    protected final TagAttribute getAttribute(String localName) {
        return this.tag.getAttributes().get(localName);
    }

    /**
     * Utility method for fetching a required TagAttribute
     * 
     * @param localName
     *            name of the attribute
     * @return TagAttribute if found, otherwise error
     * @throws TagException
     *             if the attribute was not found
     */
    protected final TagAttribute getRequiredAttribute(String localName)
            throws TagException {
        TagAttribute attr = this.getAttribute(localName);
        if (attr == null) {
            throw new TagException(this.tag, "Attribute '" + localName
                    + "' is required");
        }
        return attr;
    }

    public String toString() {
        return this.tag.toString();
    }
}
