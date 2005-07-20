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

import java.util.HashMap;
import java.util.Map;

import com.sun.facelets.FaceletHandler;
import com.sun.facelets.tag.TagLibrary;

/**
 * 
 * @author Jacob Hookom
 * @version $Id: NamespaceUnit.java,v 1.1 2005/07/18 08:25:29 jhook Exp $
 */
public final class NamespaceUnit extends CompilationUnit {

    private final Map ns = new HashMap();
    private final TagLibrary library;
    
    public NamespaceUnit(TagLibrary library) {
        this.library = library;
    }

    public FaceletHandler createFaceletHandler() {
        FaceletHandler next = this.getNextFaceletHandler();
        return new NamespaceHandler(next, this.library, this.ns);
    }
    
    public void setNamespace(String prefix, String uri) {
        this.ns.put(prefix, uri);
    }

}