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

import java.io.IOException;

import javax.el.ELException;
import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.component.ValueHolder;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import com.sun.facelets.FaceletContext;
import com.sun.facelets.FaceletException;

/**
 * Handles setting a Converter instance on a ValueHolder. Will wire all
 * attributes set to the Converter instance created/fetched. Uses the "binding"
 * attribute for grabbing instances to apply attributes to. <p/> Will only
 * set/create Converter is the passed UIComponent's parent is null, signifying
 * that it wasn't restored from an existing tree.
 * 
 * @see javax.faces.webapp.ConverterELTag
 * @see javax.faces.convert.Converter
 * @see javax.faces.component.ValueHolder
 * @author Jacob Hookom
 * @version $Id: ConvertHandler.java,v 1.2 2005/07/20 06:37:07 jhook Exp $
 */
public abstract class ConvertHandler extends ObjectHandler {

    private final TagAttribute binding;

    /**
     * @param config
     */
    public ConvertHandler(TagConfig config) {
        super(config);
        this.binding = this.getAttribute("binding");
    }

    /**
     * Set Converter instance on parent ValueHolder if it's not being restored.
     * <ol>
     * <li>Cast to ValueHolder</li>
     * <li>If "binding" attribute was specified, fetch/create and re-bind to
     * expression.</li>
     * <li>Otherwise, call
     * {@link #createConverter(FaceletContext) createConverter}.</li>
     * <li>Call
     * {@link ObjectHandler#setAttributes(FaceletContext, Object) setAttributes}
     * on Converter instance.</li>
     * <li>Set the Converter on the ValueHolder</li>
     * <li>If the ValueHolder has a localValue, convert it and set the value</li>
     * </ol>
     * 
     * @see ValueHolder
     * @see Converter
     * @see #createConverter(FaceletContext)
     * @see com.sun.facelets.FaceletHandler#apply(com.sun.facelets.FaceletContext,
     *      javax.faces.component.UIComponent)
     */
    public final void apply(FaceletContext ctx, UIComponent parent)
            throws IOException, FacesException, FaceletException, ELException {
        if (parent == null || !(parent instanceof ValueHolder)) {
            throw new TagException(this.tag,
                    "Parent not an instance of ValueHolder: " + parent);
        }

        // only process if it's been created
        if (parent.getParent() == null) {
            // cast to a ValueHolder
            ValueHolder vh = (ValueHolder) parent;
            ValueExpression ve = null;
            Converter c = null;
            if (this.binding != null) {
                ve = this.binding.getValueExpression(ctx, Converter.class);
                c = (Converter) ve.getValue(ctx);
            }
            if (c == null) {
                c = this.createConverter(ctx);
                if (ve != null) {
                    ve.setValue(ctx, c);
                }
            }
            if (c == null) {
                throw new TagException(this.tag, "No Converter was created");
            }
            this.setAttributes(ctx, c);
            vh.setConverter(c);
            Object lv = vh.getLocalValue();
            FacesContext faces = ctx.getFacesContext();
            if (lv instanceof String) {
                vh.setValue(c.getAsObject(faces, parent, (String) lv));
            }
        }
    }

    /**
     * Create a Converter instance
     * 
     * @param ctx
     *            FaceletContext to use
     * @return Converter instance, cannot be null
     */
    protected abstract Converter createConverter(FaceletContext ctx);

    /**
     * "binding" attribute is handled
     * 
     * @see com.sun.facelets.tag.ObjectHandler#isAttributeHandled(java.lang.Object,
     *      java.lang.String)
     */
    protected boolean isAttributeHandled(Object obj, String n) {
        if ("binding".equals(n)) {
            return true;
        }
        return super.isAttributeHandled(obj, n);
    }
}
