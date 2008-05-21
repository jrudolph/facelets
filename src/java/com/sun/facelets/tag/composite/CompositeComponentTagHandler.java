/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sun.facelets.tag.composite;

import com.sun.facelets.Facelet;
import com.sun.facelets.FaceletContext;
import com.sun.facelets.FaceletException;
import com.sun.facelets.FaceletFactory;
import com.sun.facelets.FaceletViewHandler;
import com.sun.facelets.tag.TagAttribute;
import com.sun.facelets.tag.TagAttributes;
import com.sun.facelets.tag.jsf.ComponentConfig;
import com.sun.facelets.tag.jsf.ComponentHandler;
import com.sun.facelets.tag.jsf.ConvertHandler;
import com.sun.facelets.tag.jsf.ValidateHandler;
import com.sun.facelets.tag.jsf.core.ActionListenerHandler;
import com.sun.facelets.tag.jsf.core.ValueChangeListenerHandler;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.el.ELException;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import javax.el.VariableMapper;
import javax.faces.FacesException;
import javax.faces.application.Application;
import javax.faces.application.Resource;
import javax.faces.component.ActionSource2;
import javax.faces.component.CompositeComponent;
import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIComponent;
import javax.faces.component.ValueHolder;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

/**
 *
 * @author edburns
 */
public class CompositeComponentTagHandler extends ComponentHandler {
    
    private final TagAttribute componentType;

    CompositeComponentTagHandler(Resource compositeComponentResource,
            ComponentConfig config) {
        super(config);
        this.compositeComponentResource = compositeComponentResource;
        this.componentType = this.getAttribute("componentType");
    }
    
    private void convertAttributesIntoParams(FaceletContext ctx) {
        TagAttributes tagAttributes = this.tag.getAttributes();
        TagAttribute attrs[] = tagAttributes.getAll();
        String name, value;
        ExpressionFactory expressionFactory = null;
        ValueExpression valueExpression = null;
        VariableMapper variableMapper = null;
        for (int i = 0; i < attrs.length; i++) {
            name = attrs[i].getLocalName();
            if (null != name && 0 < name.length() && 
                !name.equals("id") && !name.equals("binding")){
                value = attrs[i].getValue();
                if (null != value && 0 < value.length()) {
                    // lazily initialize this local variable
                    if (null == expressionFactory) {
                        expressionFactory = ctx.getFacesContext().getApplication().
                                getExpressionFactory();
                        variableMapper = ctx.getVariableMapper();
                    }
                    if (value.startsWith("#{")) {
                        valueExpression = expressionFactory.
                                createValueExpression(ctx, value, Object.class);
                    } else {
                        valueExpression = expressionFactory.
                                createValueExpression(value, Object.class);
                    }
                    variableMapper.setVariable(name, valueExpression);
                }
            }
        }
        
    }
    
    private Resource compositeComponentResource;
    
    

    @Override
    protected UIComponent createComponent(FaceletContext ctx) {
        UIComponent result = null;
        FacesContext context = ctx.getFacesContext();
        Resource componentResource = CompositeComponentTagLibrary.getScriptComponentResource(context, compositeComponentResource);

        if (null != componentResource) {
            result = context.getApplication().createComponent(componentResource);
        }

        if (null == result) {
            if (this.componentType != null) {
                ValueExpression ve = this.componentType.getValueExpression(ctx,
                        String.class);
                String type = (String) ve.getValue(ctx);
                if (null != type && 0 < type.length()) {
                    result = context.getApplication().createComponent(type);
                }
            }
        }
        if (null == result) {
            result = super.createComponent(ctx);
        }
        
        if (result instanceof CompositeComponent) {
            ((CompositeComponent) result).setResource(compositeComponentResource);
        }
        else {
            throw new IllegalArgumentException("The component instance associated with the tag with id " +
                    this.id.getValue(ctx) + " must implement CompositeComponent");
        }
        

        return result;
    }

    @Override
    protected void applyNextHandler(FaceletContext ctx, UIComponent c) throws IOException, FacesException, ELException {
        // Allow any nested elements that reside inside the markup element
        // for this tag to get applied
        super.applyNextHandler(ctx, c);
        // Apply the facelet for this composite component
        ExternalContext extContext = ctx.getFacesContext().getExternalContext();
        setCurrentCompositeComponent(extContext, c);
        applyCompositeComponent(ctx, c);
        setCurrentCompositeComponent(extContext, null);
        // Allow any PDL declared attached objects to be retargeted
        retargetAttachedObjects(ctx, c);

    }

    static UIComponent getCurrentCompositeComponent(ExternalContext extContext) {
        UIComponent result = null;
        result = (UIComponent) extContext.getRequestMap().get("com.sun.facelets.CurrentCompositeComponent");

        return result;
    }

    private static void setCurrentCompositeComponent(ExternalContext extContext,
            UIComponent cur) {
        extContext.getRequestMap().put("com.sun.facelets.CurrentCompositeComponent",
                cur);
    }

    private void retargetAttachedObjects(FaceletContext ctx, UIComponent c) {
        List<UIComponent> targets = AttachedObjectTargetHandler.getAttachedObjectTargets(c);
        List<RetargetableAttachedObjectHandler> handlers =
                AttachedObjectTargetHandler.getRetargetableHandlers(c);
        String handlerTagId = null,
                componentTagId = null;

        for (UIComponent curTarget : targets) {
            if (curTarget instanceof ActionSource2) {
                // search the handlers list for a handler with an
                // ID attribute equal to the componentId of curTarget, and
                // that is an instanceof ActionListenerHandler
                for (RetargetableAttachedObjectHandler curHandler : handlers) {
                    if ((null != (handlerTagId = curHandler.getId())) &&
                        (null != (componentTagId = curTarget.getId())) &&
                        componentTagId.equals(handlerTagId) &&
                        curHandler instanceof ActionListenerHandler) {
                        curHandler.applyAttachedObjectToComponent(ctx,
                                curTarget);
                    }
                }
            }
            if (curTarget instanceof ValueHolder) {
                // search the handlers list for a handler with an
                // ID attribute equal to the componentId of curTarget, and
                // that is an instanceof ConvertHandler.
                for (RetargetableAttachedObjectHandler curHandler : handlers) {
                    if ((null != (handlerTagId = curHandler.getId())) &&
                        (null != (componentTagId = curTarget.getId())) &&
                        componentTagId.equals(handlerTagId)) {
                        if (curHandler instanceof ConvertHandler) {
                            curHandler.applyAttachedObjectToComponent(ctx,
                                    curTarget);
                        }
                    }
                }
            }
            if (curTarget instanceof EditableValueHolder) {
                // search the handlers list for a handler with an
                // ID attribute equal to the componentId of curTarget, and
                // that is an instanceof ValueChangeListenerHandler
                // or ValidateHandler.
                for (RetargetableAttachedObjectHandler curHandler : handlers) {
                    if ((null != (handlerTagId = curHandler.getId())) &&
                        (null != (componentTagId = curTarget.getId())) &&
                        componentTagId.equals(handlerTagId)) {
                        if ((curHandler instanceof ValueChangeListenerHandler) ||
                            (curHandler instanceof ValidateHandler)) {
                            curHandler.applyAttachedObjectToComponent(ctx,
                                    curTarget);
                        }
                    }
                }
            }
        }
    }

    private void applyCompositeComponent(FaceletContext ctx, UIComponent c) {
        Facelet f = null;
        FacesContext facesContext = ctx.getFacesContext();
        FaceletViewHandler faceletViewHandler = (FaceletViewHandler) facesContext.getApplication().getViewHandler();
        FaceletFactory factory = faceletViewHandler.getFaceletFactory();
        try {
            f = factory.getFacelet(compositeComponentResource.getURL());
            convertAttributesIntoParams(ctx);
            f.apply(facesContext, c);
        } catch (IOException ex) {
            Logger.getLogger(CompositeComponentTagHandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FaceletException ex) {
            Logger.getLogger(CompositeComponentTagHandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FacesException ex) {
            Logger.getLogger(CompositeComponentTagHandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ELException ex) {
            Logger.getLogger(CompositeComponentTagHandler.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
