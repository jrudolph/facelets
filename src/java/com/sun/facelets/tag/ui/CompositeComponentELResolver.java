/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sun.facelets.tag.ui;

import java.beans.FeatureDescriptor;
import java.util.Iterator;
import javax.el.ELContext;
import javax.el.ELException;
import javax.el.ELResolver;
import javax.el.PropertyNotFoundException;
import javax.el.ValueExpression;
import javax.faces.context.FacesContext;

/**
 *
 * @author edburns
 */
public class CompositeComponentELResolver extends ELResolver {
    
    @Override
    public Class<?> getCommonPropertyType(ELContext arg0, Object arg1) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext arg0, Object arg1) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Class<?> getType(ELContext el, Object base, Object property) {

        if (base == null && property == null) {
            String message = "Cannot get tpe from Composite Component"; // ?????
            throw new PropertyNotFoundException(message);
        }

        if (!(base instanceof Component2Ref) || null == property) {
            return null;
        }
        Component2Ref composite = (Component2Ref) base;
        Class result = null;
        
        ValueExpression ve = getValueExpressionForProperty(el, composite, property);
        if (null != ve) {
            try {
                result = ve.getType(el);
                el.setPropertyResolved(true);
            }
            catch (ELException ele) {
                throw ele;
            }
        }
        else {
            el.setPropertyResolved(true);
        }
        
        return result;
    }

    @Override
    public Object getValue(ELContext el, Object base, Object property) {
        Object result = null;
        
        if (null == base) {
            return null;
        }
        
        if (!(base instanceof Component2Ref) || null == property) {
            return null;
        }
        Component2Ref composite = (Component2Ref) base;
        
        ValueExpression ve = getValueExpressionForProperty(el, composite, property);
        if (null != ve) {
            try {
                result = ve.getValue(el);
                el.setPropertyResolved(true);
            }
            catch (ELException ele) {
                throw ele;
            }
        }
        else {
            el.setPropertyResolved(true);
        }
        
        return result;
    }

    @Override
    public boolean isReadOnly(ELContext context, Object base, Object property) {
        if (base != null) {
            return false;
        }
        if (property == null) {
            String message = "Composite Component unable to test readonly";
            throw new PropertyNotFoundException(message);
        }

        return false;
    }

    @Override
    public void setValue(ELContext el, Object base, Object property, Object value) {
        if (base == null && property == null) {
            String message = "A Composite Component was not able to receive its value";
            throw new PropertyNotFoundException(message);
        }
        if (!(base instanceof Component2Ref) || null == property) {
            return;
        }
        Component2Ref composite = (Component2Ref) base;
        
        ValueExpression ve = getValueExpressionForProperty(el, composite, property);
        if (null != ve) {
            try {
                ve.setValue(el, value);
                el.setPropertyResolved(true);
            }
            catch (ELException ele) {
                throw ele;
            }
        }
        else {
            el.setPropertyResolved(true);
        }
        
    }
    
    private ValueExpression getValueExpressionForProperty(ELContext el, 
            Component2Ref composite, Object property) {
        ValueExpression ve = composite.getValueExpression("targetValue");
        if (null != ve) {
            String expressionString = ve.getExpressionString();
            if (null != expressionString && 0 < expressionString.length()) {
                expressionString = expressionString.substring(0, 
                        expressionString.length() - 1) + "." + 
                        property.toString() + "}";
                FacesContext ctx = (FacesContext) 
                        el.getContext(FacesContext.class);
                ve = ctx.getApplication().getExpressionFactory().
                        createValueExpression(el,expressionString,Object.class);
            }
        }
        return ve;
    }

}
