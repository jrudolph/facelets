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

import javax.el.ELException;
import javax.el.FunctionMapper;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;

import com.sun.facelets.FaceletContext;
import com.sun.facelets.FaceletException;
import com.sun.facelets.FaceletHandler;
import com.sun.facelets.el.CompositeFunctionMapper;
import com.sun.facelets.tag.CompositeFaceletHandler;

/**
 * @author Jacob Hookom
 * @version $Id: ConfigurationFaceletHandler.java,v 1.1 2005/05/21 17:54:50 jhook Exp $
 */
final class ConfigurationFaceletHandler implements
        CompositeFaceletHandler {

    private final FaceletHandler[] handlers;

    private final FunctionMapper fnMapper;

    private final int len;

    public ConfigurationFaceletHandler(FaceletHandler[] handlers) {
        this(handlers, null);
    }

    public ConfigurationFaceletHandler(FaceletHandler[] handlers,
            FunctionMapper fnMapper) {
        this.handlers = handlers;
        this.len = handlers.length;
        this.fnMapper = fnMapper;
    }

    public void apply(FaceletContext ctx, UIComponent parent)
            throws IOException, FacesException, FaceletException, ELException {
        if (this.fnMapper != null) {
            FunctionMapper orig = ctx.getFunctionMapper();
            ctx.setFunctionMapper(new CompositeFunctionMapper(this.fnMapper,
                    orig));
            try {
                for (int i = 0; i < len; i++) {
                    this.handlers[i].apply(ctx, parent);
                }
            } finally {
                ctx.setFunctionMapper(orig);
            }
        } else {
            for (int i = 0; i < len; i++) {
                this.handlers[i].apply(ctx, parent);
            }
        }
    }

    public FaceletHandler[] getHandlers() {
        return this.handlers;
    }
}
