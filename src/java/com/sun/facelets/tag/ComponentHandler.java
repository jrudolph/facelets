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

import javax.el.MethodExpression;
import javax.el.ValueExpression;
import javax.faces.component.ActionSource;
import javax.faces.component.ActionSource2;
import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIComponent;
import javax.faces.component.ValueHolder;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.event.ActionEvent;
import javax.faces.event.MethodExpressionActionListener;
import javax.faces.event.MethodExpressionValueChangeListener;
import javax.faces.event.ValueChangeEvent;
import javax.faces.validator.MethodExpressionValidator;

import com.sun.facelets.FaceletContext;
import com.sun.facelets.el.ELAdaptor;
import com.sun.facelets.el.LegacyMethodBinding;
import com.sun.facelets.util.FacesAPI;

/**
 * Implementation of the tag logic used in the JSF specification. This is your
 * golden hammer for wiring UIComponents to Facelets.
 * 
 * @author Jacob Hookom
 * @version $Id: ComponentHandler.java,v 1.4 2005/07/13 23:35:50 jhook Exp $
 */
public class ComponentHandler extends AbstractComponentHandler {

    private final static Class[] ACTION_LISTENER_SIG = new Class[] { ActionEvent.class };

    private final static Class[] ACTION_SIG = new Class[0];

    private final static Class[] VALIDATOR_SIG = new Class[] {
            FacesContext.class, UIComponent.class, Object.class };

    private final static Class[] VALUECHANGE_SIG = new Class[] { ValueChangeEvent.class };

    protected final TagAttribute action;

    protected final TagAttribute actionListener;

    protected final TagAttribute converter;

    protected final TagAttribute validator;

    protected final TagAttribute value;

    protected final TagAttribute valueChangeListener;

    public ComponentHandler(ComponentConfig config) {
        super(config);
        this.action = this.getAttribute("action");
        this.actionListener = this.getAttribute("actionListener");
        this.valueChangeListener = this.getAttribute("valueChangeListener");
        this.validator = this.getAttribute("validator");
        this.converter = this.getAttribute("converter");
        this.value = this.getAttribute("value");
    }

    /**
     * Depending on the object type, apply the attributes specified.
     * <ul>
     * <li>If it's an instance of {@link ActionSource ActionSource}, call
     * setActionSource</li>
     * <li>If it's an instance of {@link ValueHolder ValueHolder}, call
     * setValueHolder</li>
     * <li>If it's an instance of
     * {@link EditableValueHolder EditableValueHolder}, call
     * setEditableValueHodler</li>
     * </ul>
     * 
     * Finally, call the super to wire all unhandled attributes in accordance
     * with the spec.
     * 
     * @see #setActionSource(FaceletContext, ActionSource)
     * @see #setValueHolder(FaceletContext, ValueHolder)
     * @see #setEditableValueHolder(FaceletContext, EditableValueHolder)
     * @see com.sun.facelets.tag.ObjectHandler#setAttributes(com.sun.facelets.FaceletContext,
     *      java.lang.Object)
     */
    protected void setAttributes(FaceletContext ctx, Object obj) {
        if (obj instanceof ActionSource) {
            this.setActionSource(ctx, (ActionSource) obj);
        }
        if (obj instanceof ValueHolder) {
            this.setValueHolder(ctx, (ValueHolder) obj);
            if (obj instanceof EditableValueHolder) {
                this.setEditableValueHolder(ctx, (EditableValueHolder) obj);
            }
        }
        super.setAttributes(ctx, obj);
    }

    /**
     * Handles wiring the {@link javax.faces.validator.Validator Validator} and
     * {@link javax.faces.event.ValueChangeListener ValueChangeListener} to the
     * specified EditableValueHolder in accordance with the JSF tag spec.
     * 
     * @see EditableValueHolder
     * @see javax.faces.validator.Validator
     * @see javax.faces.event.ValueChangeListener
     * @param ctx
     * @param evh
     */
    protected final void setEditableValueHolder(FaceletContext ctx,
            EditableValueHolder evh) {
        if (this.validator != null) {
            if (this.validator.isLiteral()) {
                evh.addValidator(ctx.getFacesContext().getApplication()
                        .createValidator(this.validator.getValue()));
            } else {
                MethodExpression me = this.validator.getMethodExpression(ctx,
                        null, VALIDATOR_SIG);
                if (FacesAPI.getVersion((UIComponent) evh) >= 12) {
                    evh.addValidator(new MethodExpressionValidator(me));
                } else {
                    evh.setValidator(new LegacyMethodBinding(me));
                }
            }
        }
        if (this.valueChangeListener != null) {
            MethodExpression me = this.valueChangeListener.getMethodExpression(
                    ctx, null, VALUECHANGE_SIG);
            if (FacesAPI.getVersion((UIComponent) evh) >= 12) {
                evh
                        .addValueChangeListener(new MethodExpressionValueChangeListener(
                                me));
            } else {
                evh.setValueChangeListener(new LegacyMethodBinding(me));
            }
        }
    }

    /**
     * Handles wiring the {@link Converter Converter} and value to the
     * ValueHolder in accordance with the JSF tag spec.
     * 
     * @see ValueHolder
     * @see Converter
     * @param ctx
     *            FaceletContext to use
     * @param oc
     *            ValueHolder to apply attributes to
     */
    protected final void setValueHolder(FaceletContext ctx, ValueHolder oc) {
        if (this.converter != null) {
            if (this.converter.isLiteral()) {
                oc.setConverter(ctx.getFacesContext().getApplication()
                        .createConverter(this.converter.getValue()));
            } else {
                oc.setConverter((Converter) this.converter.getObject(ctx,
                        Converter.class));
            }
        }
        if (this.value == null) {
            throw new TagException(this.tag,
                    "'value' attribute is required for ValueHolder Components");
        }
        if (this.value.isLiteral()) {
            oc.setValue(this.value.getValue());
        } else {
            ValueExpression ve = this.value.getValueExpression(ctx,
                    Object.class);
            ELAdaptor.setExpression((UIComponent) oc, "value", ve);
        }
    }

    /**
     * Handles wiring the
     * {@link javax.faces.event.ActionListener ActionListener} and action ({@link MethodExpression MethodExpression})
     * in accordance with the JSF tag spec. Works with both ActionSource and
     * ActionSource2.
     * 
     * @link ActionSource
     * @link ActionSource2
     * @param ctx
     *            FaceletContext to use
     * @param src
     *            ActionSource to apply attributes to
     */
    protected final void setActionSource(FaceletContext ctx, ActionSource src) {
        if (this.actionListener != null) {
            MethodExpression m = this.actionListener.getMethodExpression(ctx,
                    null, ACTION_LISTENER_SIG);
            if (FacesAPI.getVersion((UIComponent) src) >= 12) {
                src.addActionListener(new MethodExpressionActionListener(m));
            } else {
                src.setActionListener(new LegacyMethodBinding(m));
            }
        }

        if (this.action != null) {
            MethodExpression m = this.action.getMethodExpression(ctx,
                    String.class, ACTION_SIG);
            if (FacesAPI.getVersion((UIComponent) src) >= 12
                    && src instanceof ActionSource2) {
                ((ActionSource2) src).setActionExpression(m);
            } else {
                src.setAction(new LegacyMethodBinding(m));
            }
        }
    }

    /**
     * Depending on the Object type, any number of attributes can be handled.
     * <ul>
     * <li>If it's an instance of {@link ActionSource ActionSource}, "action"
     * and "actionListener" are handled.</li>
     * <li>If it's an instance of {@link ValueHolder ValueHolder}, "converter"
     * and "value" are handled.</li>
     * <li>If it's an instance of
     * {@link EditableValueHolder EditableValueHolder}, "valueChangeListener"
     * and "validator" are handled.</li>
     * </ul>
     * 
     * @see com.sun.facelets.tag.ObjectHandler#isAttributeHandled(java.lang.Object,
     *      java.lang.String)
     */
    protected boolean isAttributeHandled(Object obj, String n) {
        if (obj instanceof ActionSource) {
            if ("action".equals(n) || "actionListener".equals(n)) {
                return true;
            }
        }
        if (obj instanceof ValueHolder) {
            if ("converter".equals(n) || "value".equals(n)) {
                return true;
            }
        }
        if (obj instanceof EditableValueHolder) {
            if ("valueChangeListener".equals(n) || "validator".equals(n)) {
                return true;
            }
        }
        return super.isAttributeHandled(obj, n);
    }
}
