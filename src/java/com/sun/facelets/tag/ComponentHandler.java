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

import java.io.Serializable;

import javax.el.MethodExpression;
import javax.faces.component.ActionSource;
import javax.faces.component.ActionSource2;
import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIComponent;
import javax.faces.component.ValueHolder;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.el.EvaluationException;
import javax.faces.el.MethodBinding;
import javax.faces.el.MethodNotFoundException;
import javax.faces.event.ActionEvent;
import javax.faces.event.MethodExpressionActionListener;
import javax.faces.event.MethodExpressionValueChangeListener;
import javax.faces.event.ValueChangeEvent;
import javax.faces.validator.MethodExpressionValidator;

import com.sun.facelets.FaceletContext;

/**
 * Implementation of the tag logic used in the JSF specification. This is your
 * golden hammer for wiring UIComponents to Facelets.
 * 
 * @author Jacob Hookom
 * @version $Id: ComponentHandler.java,v 1.1 2005/05/21 17:54:37 jhook Exp $
 */
public class ComponentHandler extends AbstractComponentHandler {

    /**
     * For legacy ActionSources
     * 
     * @see ActionSource
     * @author Jacob Hookom
     * @deprecated
     */
    public static class MethodExpressionAsBinding extends
            MethodBinding implements Serializable {

        private static final long serialVersionUID = 1L;

        protected final MethodExpression m;

        public MethodExpressionAsBinding(MethodExpression m) {
            this.m = m;
        }

        /*
         * (non-Javadoc)
         * 
         * @see javax.faces.el.MethodBinding#getType(javax.faces.context.FacesContext)
         */
        public Class getType(FacesContext context)
                throws MethodNotFoundException {
            return m.getMethodInfo(context.getELContext()).getReturnType();
        }

        /*
         * (non-Javadoc)
         * 
         * @see javax.faces.el.MethodBinding#invoke(javax.faces.context.FacesContext,
         *      java.lang.Object[])
         */
        public Object invoke(FacesContext context, Object[] params)
                throws EvaluationException, MethodNotFoundException {
            return m.invoke(context.getELContext(), params);
        }
    }

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
        if (this.valueChangeListener != null
                && !this.valueChangeListener.isLiteral()) {
            throw new TagAttributeException(
                    this.tag,
                    this.valueChangeListener,
                    "Must be a MethodExpression that points to a method that accepts a ValueChangeEvent");
        }
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
                evh.addValidator(new MethodExpressionValidator(this.validator
                        .getMethodExpression(ctx, null, VALIDATOR_SIG)));
            }
        }
        if (this.valueChangeListener != null) {
            evh.addValueChangeListener(new MethodExpressionValueChangeListener(
                    this.valueChangeListener.getMethodExpression(ctx, null,
                            VALUECHANGE_SIG)));
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
            ((UIComponent) oc).setValueExpression("value", this.value
                    .getValueExpression(ctx, Object.class));
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
            src.addActionListener(new MethodExpressionActionListener(
                    this.actionListener.getMethodExpression(ctx, null,
                            ACTION_LISTENER_SIG)));
        }
        if (this.action == null) {
            throw new TagException(this.tag,
                    "'action' attribute is required for ActionSource Components");
        }
        MethodExpression m = this.action.getMethodExpression(ctx, String.class,
                ACTION_SIG);
        if (src instanceof ActionSource2) {
            ((ActionSource2) src).setActionExpression(m);
        } else {
            src.setAction(new MethodExpressionAsBinding(m));
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
