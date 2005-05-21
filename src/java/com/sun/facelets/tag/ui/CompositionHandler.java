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

package com.sun.facelets.tag.ui;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.el.ELException;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;

import com.sun.facelets.FaceletContext;
import com.sun.facelets.FaceletException;
import com.sun.facelets.FaceletHandler;
import com.sun.facelets.tag.CompositeFaceletHandler;
import com.sun.facelets.tag.TagAttribute;
import com.sun.facelets.tag.TagAttributeException;
import com.sun.facelets.tag.TagConfig;
import com.sun.facelets.tag.TagHandler;

/**
 * @author Jacob Hookom
 * @version $Id: CompositionHandler.java,v 1.1 2005/05/21 17:54:54 jhook Exp $
 */
public final class CompositionHandler extends TagHandler implements
        TemplateClient {

    public final static String Name = "composition";

    protected final TagAttribute template;

    protected final Map handlers;

    /**
     * @param config
     */
    public CompositionHandler(TagConfig config) {
        super(config);
        this.template = this.getAttribute("template");
        this.handlers = new HashMap();
        if (this.template != null) {
            if (this.nextHandler instanceof CompositeFaceletHandler) {
                FaceletHandler[] c = ((CompositeFaceletHandler) this.nextHandler)
                        .getHandlers();
                for (int i = 0; i < c.length; i++) {
                    if (c[i] instanceof DefineHandler) {
                        this.handlers.put(((DefineHandler) c[i]).getName(),
                                c[i]);
                    }
                }

            } else if (this.nextHandler instanceof DefineHandler) {
                this.handlers.put(((DefineHandler) this.nextHandler).getName(),
                        this.nextHandler);
            }
            if (this.handlers.isEmpty()) {
                throw new TagAttributeException(this.tag, this.template,
                        "Template Specified, but no DefineHandler children");
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sun.facelets.FaceletHandler#apply(com.sun.facelets.FaceletContext,
     *      javax.faces.component.UIComponent)
     */
    public void apply(FaceletContext ctx, UIComponent parent)
            throws IOException, FacesException, FaceletException, ELException {
        if (this.template != null) {
            TemplateManager mngr = TemplateManager.getInstance(ctx);
            mngr.pushClient(this);
            try {
                ctx.includeFacelet(parent, this.template.getValue(ctx));
            } finally {
                mngr.popClient();
            }
        } else {
            this.nextHandler.apply(ctx, parent);
        }
    }

    public FaceletHandler getHandler(FaceletContext ctx, String name) {
        return (FaceletHandler) this.handlers.get(name);
    }

}
