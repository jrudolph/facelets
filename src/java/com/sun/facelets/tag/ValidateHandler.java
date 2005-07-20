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
import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIComponent;
import javax.faces.validator.Validator;

import com.sun.facelets.FaceletContext;
import com.sun.facelets.FaceletException;

/**
 * Handles setting a Validator instance on a EditableValueHolder. Will wire all
 * attributes set to the Validator instance created/fetched. Uses the "binding"
 * attribute for grabbing instances to apply attributes to. <p/> Will only
 * set/create Validator is the passed UIComponent's parent is null, signifying
 * that it wasn't restored from an existing tree.
 * 
 * @author Jacob Hookom
 * @version $Id: ValidateHandler.java,v 1.2 2005/07/20 06:37:07 jhook Exp $
 */
public abstract class ValidateHandler extends ObjectHandler {

    private final TagAttribute binding;

    public ValidateHandler(TagConfig config) {
        super(config);
        this.binding = this.getAttribute("binding");
    }

    /**
     * TODO
     * 
     * @see com.sun.facelets.FaceletHandler#apply(com.sun.facelets.FaceletContext,
     *      javax.faces.component.UIComponent)
     */
    public final void apply(FaceletContext ctx, UIComponent parent)
            throws IOException, FacesException, FaceletException, ELException {

        if (parent == null || !(parent instanceof EditableValueHolder)) {
            throw new TagException(this.tag,
                    "Parent not an instance of EditableValueHolder: " + parent);
        }

        // only process if it's been created
        if (parent.getParent() == null) {
            // cast to a ValueHolder
            EditableValueHolder evh = (EditableValueHolder) parent;
            ValueExpression ve = null;
            Validator v = null;
            if (this.binding != null) {
                ve = this.binding.getValueExpression(ctx, Validator.class);
                v = (Validator) ve.getValue(ctx);
            }
            if (v == null) {
                this.createValidator(ctx);
                if (ve != null) {
                    ve.setValue(ctx, v);
                }
            }
            if (v == null) {
                throw new TagException(this.tag, "No Validator was created");
            }
            this.setAttributes(ctx, v);
            evh.addValidator(v);
        }
    }

    /**
     * Template method for creating a Validator instance
     * 
     * @param ctx
     *            FaceletContext to use
     * @return a new Validator instance
     */
    protected abstract Validator createValidator(FaceletContext ctx);

    /**
     * Attribute "binding" is handled
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
