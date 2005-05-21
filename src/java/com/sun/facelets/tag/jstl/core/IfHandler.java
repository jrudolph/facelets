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

package com.sun.facelets.tag.jstl.core;

import java.io.IOException;

import javax.el.ELException;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;

import com.sun.facelets.FaceletContext;
import com.sun.facelets.el.LiteralValueExpression;
import com.sun.facelets.tag.TagAttribute;
import com.sun.facelets.tag.TagConfig;
import com.sun.facelets.tag.TagHandler;

/**
 * @author Jacob Hookom
 * @version $Id: IfHandler.java,v 1.1 2005/05/21 17:54:47 jhook Exp $
 */
public final class IfHandler extends TagHandler {

    protected final TagAttribute test;

    protected final TagAttribute var;

    /**
     * @param config
     */
    public IfHandler(TagConfig config) {
        super(config);
        this.test = this.getRequiredAttribute("test");
        this.var = this.getAttribute("var");
    }

    public void apply(FaceletContext ctx, UIComponent parent)
            throws IOException, FacesException, ELException {
        boolean b = this.test.getBoolean(ctx);
        if (this.var != null) {
            ctx.getVariableMapper().setVariable(var.getValue(ctx),
                    new LiteralValueExpression(new Boolean(b)));
        }
        if (b) {
            this.nextHandler.apply(ctx, parent);
        }
    }

}
