/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sun.facelets.tag.jsf;

import com.sun.facelets.Facelet;
import com.sun.facelets.FaceletContext;
import com.sun.facelets.FaceletContext;
import com.sun.facelets.FaceletException;
import com.sun.facelets.FaceletFactory;
import com.sun.facelets.FaceletViewHandler;
import com.sun.facelets.el.VariableMapperWrapper;
import com.sun.facelets.tag.TagAttribute;
import com.sun.facelets.tag.TagAttributes;
import com.sun.facelets.tag.TagException;
import com.sun.facelets.tag.composite.CompositeComponentBeanInfo;
import com.sun.facelets.tag.jsf.ComponentConfig;
import com.sun.facelets.tag.jsf.ComponentHandler;
import java.beans.BeanDescriptor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.el.ELException;
import javax.el.Expression;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import javax.el.VariableMapper;
import javax.faces.FacesException;
import javax.faces.application.Resource;
import javax.faces.component.UIComponent;
import javax.faces.component.UIPanel;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.webapp.pdl.AttachedObjectHandler;
import javax.faces.webapp.pdl.PDLUtils;

/**
 *
 * @author edburns
 */
public class CompositeComponentTagHandler extends ComponentHandler {
    
    CompositeComponentTagHandler(Resource compositeComponentResource,
            ComponentConfig config) {
        super(config);
        this.compositeComponentResource = compositeComponentResource;
    }
    
    private void copyTagAttributesIntoComponentAttributes(FaceletContext ctx,
            UIComponent compositeComponent) {
        TagAttributes tagAttributes = this.tag.getAttributes();
        TagAttribute attrs[] = tagAttributes.getAll();
        String name, value;
        ExpressionFactory expressionFactory = null;
        Expression expression = null;
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
                    }
                    // PENDING(edburns): deal with methodExpressions
                    if (value.startsWith("#{")) {
                        expression = expressionFactory.
                                createValueExpression(ctx, value, Object.class);
                    } else {
                        expression = expressionFactory.
                                createValueExpression(value, Object.class);
                    }
                    compositeComponent.getAttributes().put(name, expression);
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

        // PENDING this is wasteful.  I need some way to discover the 
        // component metadata before creating the component instance.
        // The easiest way right now is to create a dummy component instance
        // suck out its metadata, then create the "real" component.
        CompositeComponentBeanInfo componentMetadata = discoverComponentMetadata(ctx);
        
        assert(null != componentMetadata);
        BeanDescriptor componentBeanDescriptor = componentMetadata.getBeanDescriptor();
        
        if (null != componentResource) {
            result = context.getApplication().createComponent(componentResource);
        }

        if (null == result) {
            ValueExpression ve = (ValueExpression)
                    componentBeanDescriptor.getValue(UIComponent.COMPOSITE_COMPONENT_TYPE_KEY);
            if (null != ve) {
                String type = (String) ve.getValue(ctx);
                if (null != type && 0 < type.length()) {
                    result = context.getApplication().createComponent(type);
                }
            }
        }
        
        if (null == result) {
            String packageName = compositeComponentResource.getLibraryName();
            String className = compositeComponentResource.getResourceName();
            className = className.substring(0, className.lastIndexOf("."));
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            if (null == cl) {
                cl = this.getClass().getClassLoader();
            }
            try {
                Class clazz = cl.loadClass(packageName + "." + className);
                result = (UIComponent) clazz.newInstance();
            } catch (ClassNotFoundException ex) {
                // take no action, this is not an error.
            } catch (InstantiationException ie) {
                throw new TagException(this.tag, ie);
            } catch (IllegalAccessException iae) {
                throw new TagException(this.tag, iae);
            } catch (ClassCastException cce) {
                throw new TagException(this.tag, cce);
            } catch (Throwable otherwise) {
                // take no action, not an error
            }
        }
        
        
        if (null == result) {
            componentType = CompositeComponentImpl.TYPE;
            result = super.createComponent(ctx);
        }
        
        if (null != result) {
            result.getAttributes().put(Resource.COMPONENT_RESOURCE_KEY, 
                    compositeComponentResource);
        }
        else {
            throw new NullPointerException("Unable to instantiate component for tag " +
                    this.tag.getLocalName() + " with id " + this.id.getValue(ctx));
        }
        result.setRendererType("javax.faces.Composite");

        return result;
    }
    
    @Override
    protected void applyNextHandler(FaceletContext ctx, UIComponent c) throws IOException, FacesException, ELException {
        // Allow any nested elements that reside inside the markup element
        // for this tag to get applied
        super.applyNextHandler(ctx, c);
        // Apply the facelet for this composite component
        ExternalContext extContext = ctx.getFacesContext().getExternalContext();
        applyCompositeComponent(ctx, c);
        // Allow any PDL declared attached objects to be retargeted
        if (ComponentSupport.isNew(c)) {
            PDLUtils.retargetAttachedObjects(ctx.getFacesContext(), c,
                    getAttachedObjectHandlers(c, false));
        }

    }
    
    private CompositeComponentBeanInfo discoverComponentMetadata(FaceletContext ctx) {
        CompositeComponentBeanInfo result = null;
        FacesContext context = ctx.getFacesContext();
        FaceletViewHandler faceletViewHandler = (FaceletViewHandler) context.getApplication().getViewHandler();
        FaceletFactory factory = faceletViewHandler.getFaceletFactory();
        VariableMapper orig = ctx.getVariableMapper();
        UIComponent tmp = context.getApplication().createComponent("javax.faces.NamingContainer");
        UIPanel facetComponent = (UIPanel)
                context.getApplication().createComponent("javax.faces.Panel");
        facetComponent.setRendererType("javax.faces.Group");
        tmp.getFacets().put(UIComponent.COMPOSITE_FACET_NAME, facetComponent);
        tmp.getAttributes().put(Resource.COMPONENT_RESOURCE_KEY, 
                compositeComponentResource);
        
        Facelet f;

        try {
            f = factory.getFacelet(compositeComponentResource.getURL());
            copyTagAttributesIntoComponentAttributes(ctx, tmp);
            VariableMapper wrapper = new VariableMapperWrapper(orig) {

                @Override
                public ValueExpression resolveVariable(String variable) {
                    return super.resolveVariable(variable);
                }
                
            };
            ctx.setVariableMapper(wrapper);
            f.apply(context, facetComponent);
        } catch (IOException ex) {
            Logger.getLogger(CompositeComponentTagHandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FaceletException ex) {
            Logger.getLogger(CompositeComponentTagHandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FacesException ex) {
            Logger.getLogger(CompositeComponentTagHandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ELException ex) {
            Logger.getLogger(CompositeComponentTagHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally {
            ctx.setVariableMapper(orig);
        }
        result = (CompositeComponentBeanInfo) 
                tmp.getAttributes().get(UIComponent.BEANINFO_KEY);
        
        return result;
    }

    private void applyCompositeComponent(FaceletContext ctx, UIComponent c) {
        Facelet f = null;
        FacesContext facesContext = ctx.getFacesContext();
        FaceletViewHandler faceletViewHandler = (FaceletViewHandler) facesContext.getApplication().getViewHandler();
        FaceletFactory factory = faceletViewHandler.getFaceletFactory();
        VariableMapper orig = ctx.getVariableMapper();
        
        UIPanel facetComponent = (UIPanel)
                facesContext.getApplication().createComponent("javax.faces.Panel");
        facetComponent.setRendererType("javax.faces.Group");
        c.getFacets().put(UIComponent.COMPOSITE_FACET_NAME, facetComponent);
        
        try {
            f = factory.getFacelet(compositeComponentResource.getURL());
            copyTagAttributesIntoComponentAttributes(ctx, c);
            VariableMapper wrapper = new VariableMapperWrapper(orig) {

                @Override
                public ValueExpression resolveVariable(String variable) {
                    return super.resolveVariable(variable);
                }
                
            };
            ctx.setVariableMapper(wrapper);
            f.apply(facesContext, facetComponent);
        } catch (IOException ex) {
            Logger.getLogger(CompositeComponentTagHandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FaceletException ex) {
            Logger.getLogger(CompositeComponentTagHandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FacesException ex) {
            Logger.getLogger(CompositeComponentTagHandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ELException ex) {
            Logger.getLogger(CompositeComponentTagHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally {
            ctx.setVariableMapper(orig);
        }

    }

    public static List<AttachedObjectHandler> getAttachedObjectHandlers(UIComponent component) {
        return getAttachedObjectHandlers(component, true);
    }
    
    public static List<AttachedObjectHandler> getAttachedObjectHandlers(UIComponent component,
            boolean create) {
        Map<String, Object> attrs = component.getAttributes();
        List<AttachedObjectHandler> result = (List<AttachedObjectHandler>)
                attrs.get("javax.faces.RetargetableHandlers");
        
        if (null == result) {
            if (create) {
                result = new ArrayList<AttachedObjectHandler>();
                attrs.put("javax.faces.RetargetableHandlers", result);
            }
            else {
                result = Collections.EMPTY_LIST;
            }
        }
        return result;
    }
    
    
    
}
