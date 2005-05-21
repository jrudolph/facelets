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
import javax.el.FunctionMapper;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;

import com.sun.facelets.FaceletContext;
import com.sun.facelets.FaceletException;
import com.sun.facelets.FaceletHandler;
import com.sun.facelets.tag.Tag;
import com.sun.facelets.tag.TagConfig;

/**
 * CompilationUnit support class, not for public use
 * 
 * @author Jacob Hookom
 * @version $Id: Configuration.java,v 1.1 2005/05/21 17:54:50 jhook Exp $
 */
final class Configuration implements TagConfig {

    private final static class Next implements FaceletHandler {

        public void apply(FaceletContext ctx, UIComponent parent)
                throws IOException, FacesException, FaceletException,
                ELException {
            // do nothing
        }
    }

    private final static FaceletHandler NEXT = new Next();

    private final Tag tag;

    private final String id;

    private final List children;

    private FunctionMapper fnMapper;

    private boolean closed;

    public Configuration(Tag tag, String id) {
        this.tag = tag;
        this.id = id;
        this.children = new ArrayList();
    }

    public Tag getTag() {
        return this.tag;
    }

    public FaceletHandler getNextHandler() {
        if (this.children.isEmpty()) {
            return NEXT;
        }
        if (this.children.size() == 1 && this.fnMapper == null) {
            return (FaceletHandler) this.children.get(0);
        }
        return new ConfigurationFaceletHandler((FaceletHandler[]) this.children
                .toArray(new FaceletHandler[this.children.size()]),
                this.fnMapper);
    }

    public String getTagId() {
        return this.id;
    }

    public void addHandler(FaceletHandler handler) {
        if (!this.closed) {
            this.children.add(handler);
        }
    }

    public void close() {
        this.closed = true;
    }

    public void reset() {
        this.children.clear();
    }

    public void setFunctionMapper(FunctionMapper fnMapper) {
        this.fnMapper = fnMapper;
    }
}
