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

import java.io.IOException;

import javax.el.ELException;
import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIComponent;
import javax.faces.event.ValueChangeListener;

import com.sun.facelets.FaceletContext;
import com.sun.facelets.FaceletException;
import com.sun.facelets.tag.TagAttribute;
import com.sun.facelets.tag.TagAttributeException;
import com.sun.facelets.tag.TagConfig;
import com.sun.facelets.tag.TagException;
import com.sun.facelets.tag.TagHandler;

/**
 * Register an ValueChangeListener instance on the UIComponent associated with
 * the closest parent UIComponent custom action.<p/> See <a target="_new"
 * href="http://java.sun.com/j2ee/javaserverfaces/1.1_01/docs/tlddocs/f/valueChangeListener.html">tag
 * documentation</a>.
 * 
 * @author Jacob Hookom
 * @version $Id: ValueChangeListenerHandler.java,v 1.2 2005/07/20 06:37:09 jhook Exp $
 */
public final class ValueChangeListenerHandler extends TagHandler {

    private Class listenerType;

    private final TagAttribute type;

    private final TagAttribute binding;

    public ValueChangeListenerHandler(TagConfig config) {
        super(config);
        this.binding = this.getAttribute("binding");
        this.type = this.getAttribute("type");
        if (type != null) {
            try {
                this.listenerType = Class.forName(type.getValue());
            } catch (Exception e) {
                throw new TagAttributeException(this.tag, this.type, e);
            }
        }
    }

    /**
     * See taglib documentation.
     * 
     * @see com.sun.facelets.FaceletHandler#apply(com.sun.facelets.FaceletContext,
     *      javax.faces.component.UIComponent)
     */
    public void apply(FaceletContext ctx, UIComponent parent)
            throws IOException, FacesException, FaceletException, ELException {
        if (parent instanceof EditableValueHolder) {
            // only process if parent was just created
            if (parent.getParent() == null) {
                EditableValueHolder evh = (EditableValueHolder) parent;
                ValueChangeListener listener = null;
                ValueExpression ve = null;
                if (this.binding != null) {
                    ve = this.binding.getValueExpression(ctx,
                            ValueChangeListener.class);
                    listener = (ValueChangeListener) ve.getValue(ctx);
                }
                if (listener == null && this.listenerType != null) {
                    try {
                        listener = (ValueChangeListener) listenerType
                                .newInstance();
                    } catch (Exception e) {
                        throw new TagAttributeException(this.tag, this.type, e);
                    }
                    if (ve != null) {
                        ve.setValue(ctx, ve);
                    }
                } else {
                    throw new TagAttributeException(this.tag, this.binding,
                            "Binding evaluated to null, and there wasn't a 'type' Attribute Specified");
                }
                evh.addValueChangeListener(listener);
            }
        } else {
            throw new TagException(this.tag,
                    "Parent is not of type EditableValueHolder, type is: "
                            + parent);
        }
    }

}
