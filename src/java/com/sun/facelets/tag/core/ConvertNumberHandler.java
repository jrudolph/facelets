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
import javax.faces.convert.NumberConverter;

import com.sun.facelets.FaceletContext;
import com.sun.facelets.FaceletException;
import com.sun.facelets.tag.ComponentSupport;
import com.sun.facelets.tag.ConvertHandler;
import com.sun.facelets.tag.TagAttribute;
import com.sun.facelets.tag.TagConfig;

/**
 * Register a NumberConverter instance on the UIComponent associated with the
 * closest parent UIComponent custom action. <p/> See <a target="_new"
 * href="http://java.sun.com/j2ee/javaserverfaces/1.1_01/docs/tlddocs/f/convertNumber.html">tag
 * documentation</a>.
 * 
 * @author Jacob Hookom
 * @version $Id: ConvertNumberHandler.java,v 1.2 2005/07/20 06:37:08 jhook Exp $
 */
public final class ConvertNumberHandler extends ConvertHandler {

    private final TagAttribute locale;

    /**
     * @param config
     */
    public ConvertNumberHandler(TagConfig config) {
        super(config);
        this.locale = this.getAttribute("locale");
    }

    /**
     * Returns a new NumberConverter
     * 
     * @see NumberConverter
     * @see com.sun.facelets.tag.jsf.ConverterHandler#createConverter(com.sun.facelets.FaceletContext)
     */
    protected Converter createConverter(FaceletContext ctx)
            throws FacesException, ELException, FaceletException {
        return new NumberConverter();
    }

    /**
     * "locale" is handled
     * @see com.sun.facelets.tag.ObjectHandler#isAttributeHandled(java.lang.Object, java.lang.String)
     */
    protected boolean isAttributeHandled(Object obj, String n) {
        if ("locale".equals(n)) {
            return true;
        }
        return super.isAttributeHandled(obj, n);
    }

    /* (non-Javadoc)
     * @see com.sun.facelets.tag.ObjectHandler#setAttributes(com.sun.facelets.FaceletContext, java.lang.Object)
     */
    protected void setAttributes(FaceletContext ctx, Object obj) {
        super.setAttributes(ctx, obj);
        NumberConverter c = (NumberConverter) obj;
        if (this.locale != null) {
            c.setLocale(ComponentSupport.getLocale(ctx, this.locale));
        }
    }

}
