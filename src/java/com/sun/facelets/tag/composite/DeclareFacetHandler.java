/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sun.facelets.tag.composite;

import com.sun.facelets.FaceletContext;
import com.sun.facelets.FaceletException;
import com.sun.facelets.tag.TagAttribute;
import com.sun.facelets.tag.TagConfig;
import com.sun.facelets.tag.TagException;
import com.sun.facelets.tag.TagHandler;
import com.sun.facelets.tag.jsf.ComponentSupport;
import java.beans.BeanDescriptor;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.el.ELException;
import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;

/**
 *
 * @author edburns
 */
public class DeclareFacetHandler extends TagHandler {
    
    private TagAttribute name = null;


    public DeclareFacetHandler(TagConfig config) {
        super(config);
        this.name = this.getRequiredAttribute("name");
        
    }
    
    public void apply(FaceletContext ctx, UIComponent parent) throws IOException, FacesException, FaceletException, ELException {
        // only process if it's been created
        if (null == parent || 
            (null == (parent = parent.getParent())) ||
            !(ComponentSupport.isNew(parent))) {
            return;
        }
        
        Map<String, Object> componentAttrs = parent.getAttributes();
        ValueExpression ve = null;
        String strValue = null;

        CompositeComponentBeanInfo componentBeanInfo = (CompositeComponentBeanInfo)
                componentAttrs.get(UIComponent.BEANINFO_KEY);

        // Get the value of required the name propertyDescriptor
        ve = name.getValueExpression(ctx, String.class);
        strValue = (String) ve.getValue(ctx);
        boolean booleanValue = false;
        BeanDescriptor componentBeanDescriptor = componentBeanInfo.getBeanDescriptor();
        
        Map<String, PropertyDescriptor> facetDescriptors = null;
        PropertyDescriptor propertyDescriptor = null;
        TagAttribute attr = null;
        
        if (null == (facetDescriptors = (Map<String, PropertyDescriptor>) 
                   componentBeanDescriptor.getValue(UIComponent.FACETS_KEY))) {

            facetDescriptors = new HashMap<String, PropertyDescriptor>();
            componentBeanDescriptor.setValue(UIComponent.FACETS_KEY, 
                    facetDescriptors);
        }
        try {
            propertyDescriptor = new PropertyDescriptor(strValue, null, null);
        } catch (IntrospectionException ex) {
            throw new  TagException(tag, "Unable to create property descriptor for facet" + strValue, ex);
        }
        facetDescriptors.put(strValue, propertyDescriptor);
        
        if (null != (attr = this.getAttribute("displayName"))) {
            ve = attr.getValueExpression(ctx, String.class);
            strValue = (String) ve.getValue(ctx);
            if (null != strValue) {
                propertyDescriptor.setDisplayName(strValue);
            }
        }
        if (null != (attr = this.getAttribute("expert"))) {
            ve = attr.getValueExpression(ctx, Boolean.class);
            booleanValue = ((Boolean) ve.getValue(ctx)).booleanValue();
            propertyDescriptor.setExpert(booleanValue);
        }
        if (null != (attr = this.getAttribute("hidden"))) {
            ve = attr.getValueExpression(ctx, Boolean.class);
            booleanValue = ((Boolean) ve.getValue(ctx)).booleanValue();
            propertyDescriptor.setHidden(booleanValue);
        }
        if (null != (attr = this.getAttribute("preferred"))) {
            ve = attr.getValueExpression(ctx, Boolean.class);
            booleanValue = ((Boolean) ve.getValue(ctx)).booleanValue();
            propertyDescriptor.setPreferred(booleanValue);
        }
        if (null != (attr = this.getAttribute("shortDescription"))) {
            ve = attr.getValueExpression(ctx, String.class);
            strValue = (String) ve.getValue(ctx);
            if (null != strValue) {
                propertyDescriptor.setShortDescription(strValue);
            }
        }
        if (null != (attr = this.getAttribute("default"))) {
            ve = attr.getValueExpression(ctx, String.class);
            propertyDescriptor.setValue("default", ve);
        }
        
        
    }

}
