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

package com.sun.facelets.tag.core;

import javax.el.ELException;
import javax.faces.FacesException;
import javax.faces.convert.Converter;

import com.sun.facelets.FaceletContext;
import com.sun.facelets.FaceletException;
import com.sun.facelets.tag.ConvertHandler;
import com.sun.facelets.tag.TagAttribute;
import com.sun.facelets.tag.TagConfig;

/**
 * Register a named Converter instance on the UIComponent associated with the
 * closest parent UIComponent custom action. <p/> See <a target="_new"
 * href="http://java.sun.com/j2ee/javaserverfaces/1.1_01/docs/tlddocs/f/converter.html">tag
 * documentation</a>.
 * 
 * @author Jacob Hookom
 * @version $Id: ConvertDelegateHandler.java,v 1.2 2005/07/20 06:37:08 jhook Exp $
 */
public final class ConvertDelegateHandler extends ConvertHandler {

    private final TagAttribute converterId;

    /**
     * @param config
     */
    public ConvertDelegateHandler(TagConfig config) {
        super(config);
        this.converterId = this.getRequiredAttribute("converterId");
    }

    /**
     * Uses the specified "converterId" to pull an instance from the Application
     * 
     * @see javax.faces.application.Application#createComponent(java.lang.String)
     * @see com.sun.facelets.tag.jsf.ConverterHandler#createConverter(com.sun.facelets.FaceletContext)
     */
    protected Converter createConverter(FaceletContext ctx)
            throws FacesException, ELException, FaceletException {
        return ctx.getFacesContext().getApplication().createConverter(
                this.converterId.getValue(ctx));
    }

    /**
     * Always returns true (blocks any other attributes from being wired).
     * 
     * @param n
     *            attribute name
     * @return true
     */
    protected boolean isAttributeHandled(String n) {
        return true;
    }

    /**
     * Does nothing, blocks all other attributes from being wired.
     * 
     * @see com.sun.facelets.tag.ObjectHandler#setAttributes(com.sun.facelets.FaceletContext,
     *      java.lang.Object)
     */
    protected void setAttributes(FaceletContext ctx, Object obj) {
        // do nothing
    }
}
