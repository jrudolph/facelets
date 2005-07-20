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
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.el.ELException;
import javax.el.MethodExpression;
import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.application.Application;
import javax.faces.component.ActionSource;
import javax.faces.component.ActionSource2;
import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIComponent;
import javax.faces.component.ValueHolder;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.el.ValueBinding;
import javax.faces.event.ActionEvent;
import javax.faces.event.MethodExpressionActionListener;
import javax.faces.event.MethodExpressionValueChangeListener;
import javax.faces.event.ValueChangeEvent;
import javax.faces.validator.MethodExpressionValidator;

import com.sun.facelets.FaceletContext;
import com.sun.facelets.el.ELAdaptor;
import com.sun.facelets.el.LegacyMethodBinding;
import com.sun.facelets.el.LegacyValueBinding;
import com.sun.facelets.util.FacesAPI;

/**
 * Implementation of the tag logic used in the JSF specification. This is your
 * golden hammer for wiring UIComponents to Facelets.
 * 
 * @author Jacob Hookom
 * @version $Id: ComponentHandler.java,v 1.6 2005/07/20 06:37:06 jhook Exp $
 */
public class ComponentHandler extends ObjectHandler {

    private final static Logger log = Logger
            .getLogger("facelets.tag.component");

    private final static Class[] ACTION_LISTENER_SIG = new Class[] { ActionEvent.class };

    private final static Class[] ACTION_SIG = new Class[0];

    private final static Class[] VALIDATOR_SIG = new Class[] {
            FacesContext.class, UIComponent.class, Object.class };

    private final static Class[] VALUECHANGE_SIG = new Class[] { ValueChangeEvent.class };

    private final TagAttribute binding;

    private final String componentType;

    private final TagAttribute id;

    private final String rendererType;

    private final TagAttribute action;

    private final TagAttribute actionListener;

    private final TagAttribute converter;

    private final TagAttribute validator;

    private final TagAttribute value;

    private final TagAttribute valueChangeListener;

    public ComponentHandler(ComponentConfig config) {
        super(config);
        this.componentType = config.getComponentType();
        this.rendererType = config.getRendererType();
        this.id = this.getAttribute("id");
        this.binding = this.getAttribute("binding");
        this.action = this.getAttribute("action");
        this.actionListener = this.getAttribute("actionListener");
        this.valueChangeListener = this.getAttribute("valueChangeListener");
        this.validator = this.getAttribute("validator");
        this.converter = this.getAttribute("converter");
        this.value = this.getAttribute("value");
    }

    /**
     * Method handles UIComponent tree creation in accordance with the JSF 1.2
     * spec.
     * <ol>
     * <li>First determines this UIComponent's id by calling
     * {@link #getId(FaceletContext) getId(FaceletContext)}.</li>
     * <li>Search the parent for an existing UIComponent of the id we just
     * grabbed</li>
     * <li>If found, {@link #markForDeletion(UIComponent) mark} its children
     * for deletion.</li>
     * <li>If <i>not</i> found, call
     * {@link #createComponent(FaceletContext) createComponent}.
     * <ol>
     * <li>Only here do we apply
     * {@link ObjectHandler#setAttributes(FaceletContext, Object) attributes}</li>
     * <li>Set the UIComponent's id</li>
     * <li>Set the RendererType of this instance</li>
     * </ol>
     * </li>
     * <li>Now apply the nextHandler, passing the UIComponent we've
     * created/found.</li>
     * <li>Now add the UIComponent to the passed parent</li>
     * <li>Lastly, if the UIComponent already existed (found), then
     * {@link #finalizeForDeletion(UIComponent) finalize} for deletion.</li>
     * </ol>
     * 
     * @see com.sun.facelets.FaceletHandler#apply(com.sun.facelets.FaceletContext,
     *      javax.faces.component.UIComponent)
     * 
     * @throws TagException
     *             if the UIComponent parent is null
     */
    public final void apply(FaceletContext ctx, UIComponent parent)
            throws IOException, FacesException, ELException {
        // make sure our parent is not null
        if (parent == null) {
            throw new TagException(this.tag, "Parent UIComponent was null");
        }

        // our id
        String id = this.getId(ctx);

        // grab our component
        UIComponent c = ComponentSupport.findChild(parent, id);
        boolean componentFound = false;
        if (c != null) {
            componentFound = true;
            // mark all children for cleaning
            if (log.isLoggable(Level.FINE)) {
                log.fine(this.tag
                        + " Component Found, marking children for cleanup");
            }
            ComponentSupport.markForDeletion(c);
        } else {
            c = this.createComponent(ctx);
            if (log.isLoggable(Level.FINE)) {
                log.fine(this.tag + " Component Created: "
                        + c.getClass().getName());
            }
            this.setAttributes(ctx, c);
            c.setId(id);
            if (this.rendererType != null) {
                c.setRendererType(this.rendererType);
            }
        }

        // first allow c to get populated
        this.nextHandler.apply(ctx, c);

        // add to the tree afterwards
        // this allows children to determine if it's
        // been part of the tree or not yet
        parent.getChildren().add(c);

        // finish cleaning up orphaned children
        if (componentFound) {
            ComponentSupport.finalizeForDeletion(c);
        }
    }

    /**
     * If the binding attribute was specified, use that in conjuction with our
     * componentType String variable to call createComponent on the Application,
     * otherwise just pass the componentType String.
     * <p />
     * If the binding was used, then set the ValueExpression "binding" on the
     * created UIComponent.
     * 
     * @see Application#createComponent(javax.faces.el.ValueBinding,
     *      javax.faces.context.FacesContext, java.lang.String)
     * @see Application#createComponent(java.lang.String)
     * @param ctx
     *            FaceletContext to use in creating a component
     * @return
     */
    protected UIComponent createComponent(FaceletContext ctx) {
        UIComponent c = null;
        FacesContext faces = ctx.getFacesContext();
        Application app = faces.getApplication();
        if (this.binding != null) {
            ValueExpression ve = this.binding.getValueExpression(ctx,
                    Object.class);
            if (FacesAPI.getVersion() >= 12) {
                c = app.createComponent(ve, faces, this.componentType);
                if (c != null) {
                    // Make sure the component supports 1.2
                    if (FacesAPI.getVersion(c) >= 12) {
                        c.setValueExpression("binding", ve);
                    } else {
                        ValueBinding vb = new LegacyValueBinding(ve);
                        c.setValueBinding("binding", vb);
                    }

                }
            } else {
                ValueBinding vb = new LegacyValueBinding(ve);
                c = app.createComponent(vb, faces, this.componentType);
                if (c != null) {
                    c.setValueBinding("binding", vb);
                }
            }
        } else {
            c = app.createComponent(this.componentType);
        }
        return c;
    }

    /**
     * If the id TagAttribute was specified, get it's value, otherwise generate
     * a unique id from our tagId.
     * 
     * @see TagAttribute#getValue(FaceletContext)
     * @param ctx
     *            FaceletContext to use
     * @return what should be a unique Id
     */
    protected String getId(FaceletContext ctx) {
        if (this.id != null) {
            return this.id.getValue(ctx);
        }
        return ctx.generateUniqueId(this.tagId);
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
    private final void setEditableValueHolder(FaceletContext ctx,
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
    private final void setValueHolder(FaceletContext ctx, ValueHolder oc) {
        if (this.converter != null) {
            if (this.converter.isLiteral()) {
                oc.setConverter(ctx.getFacesContext().getApplication()
                        .createConverter(this.converter.getValue()));
            } else {
                oc.setConverter((Converter) this.converter.getObject(ctx,
                        Converter.class));
            }
        }
        if (this.value != null) {
            if (this.value.isLiteral()) {
                oc.setValue(this.value.getValue());
            } else {
                ValueExpression ve = this.value.getValueExpression(ctx,
                        Object.class);
                ELAdaptor.setExpression((UIComponent) oc, "value", ve);
            }
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
    private final void setActionSource(FaceletContext ctx, ActionSource src) {
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
        if ("binding".equals(n) || "id".equals(n)) {
            return true;
        }
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
