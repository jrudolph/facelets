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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.el.ELException;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;

import com.sun.facelets.FaceletContext;
import com.sun.facelets.FaceletException;
import com.sun.facelets.FaceletHandler;

/**
 * 
 * @author Jacob Hookom
 * @version $Id: CompilationUnit.java,v 1.4 2005/07/20 05:27:45 jhook Exp $
 */
class CompilationUnit {

    protected final static FaceletHandler LEAF = new FaceletHandler() {
        public void apply(FaceletContext ctx, UIComponent parent)
                throws IOException, FacesException, FaceletException,
                ELException {
        }
        public String toString() {
            return "FaceletHandler Tail";
        }
    };

    private List children;

    public CompilationUnit() {
    }

    public void addChild(CompilationUnit unit) {
        if (this.children == null) {
            this.children = new ArrayList();
        }
        this.children.add(unit);
    }

    public FaceletHandler createFaceletHandler() {
        return this.getNextFaceletHandler();
    }

    protected final FaceletHandler getNextFaceletHandler() {
        if (this.children == null || this.children.size() == 0) {
            return LEAF;
        }
        if (this.children.size() == 1) {
            CompilationUnit u = (CompilationUnit) this.children.get(0);
            return u.createFaceletHandler();
        }
        FaceletHandler[] fh = new FaceletHandler[this.children.size()];
        for (int i = 0; i < fh.length; i++) {
            fh[i] = ((CompilationUnit) this.children.get(i))
                    .createFaceletHandler();
        }
        return new CompositeFaceletHandler(fh);
    }

}
