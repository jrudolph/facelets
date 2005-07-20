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

package com.sun.facelets.compiler;

import com.sun.facelets.FaceletHandler;
import com.sun.facelets.tag.Tag;
import com.sun.facelets.tag.TagConfig;
import com.sun.facelets.tag.TagLibrary;

/**
 * 
 * @author Jacob Hookom
 * @version $Id: TagUnit.java,v 1.2 2005/07/20 05:27:46 jhook Exp $
 */
final class TagUnit extends CompilationUnit implements TagConfig {

    private final TagLibrary library;

    private final String id;

    private final Tag tag;

    public TagUnit(TagLibrary library, Tag tag, String id) {
        this.library = library;
        this.tag = tag;
        this.id = id;
    }

    public FaceletHandler createFaceletHandler() {
        return this.library.createTagHandler(this.tag.getNamespace(), this.tag
                .getLocalName(), this);
    }

    public FaceletHandler getNextHandler() {
        return this.getNextFaceletHandler();
    }

    public Tag getTag() {
        return this.tag;
    }

    public String getTagId() {
        return this.id;
    }

}
