/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sun.facelets.tag.composite;

import com.sun.facelets.FaceletContext;
import com.sun.facelets.FaceletException;
import com.sun.facelets.tag.TagAttribute;
import com.sun.facelets.tag.TagConfig;
import com.sun.facelets.tag.TagHandler;
import com.sun.facelets.tag.jsf.ComponentSupport;
import java.beans.BeanDescriptor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import javax.el.ELException;
import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.webapp.pdl.AttachedObjectTarget;
import javax.faces.application.Resource;

/**
 *
 * @author edburns
 */
public class InterfaceHandler extends TagHandler {

    private static final Logger log = Logger.getLogger("facelets.tag.composite");

    public final static String Name = "interface";

    
    public InterfaceHandler(TagConfig config) {
        super(config);
    }
    
    public void apply(FaceletContext ctx, UIComponent parent) throws IOException, FacesException, FaceletException, ELException {

        imbueComponentWithMetadata(ctx, parent);
        this.nextHandler.apply(ctx, parent);
    }
    
    private void imbueComponentWithMetadata(FaceletContext ctx, UIComponent parent) {
        // only process if it's been created
        if (null == parent || 
            (null == (parent = parent.getParent())) ||
            !(ComponentSupport.isNew(parent))) {
            return;
        }
        
        // the real implementation will check if there is a cached beaninfo somewhere first
	Map<String, Object> attrs = parent.getAttributes();

        CompositeComponentBeanInfo componentBeanInfo = 
                new CompositeComponentBeanInfo();
        attrs.put(UIComponent.BEANINFO_KEY, componentBeanInfo);
        BeanDescriptor componentDescriptor = new BeanDescriptor(parent.getClass());
        componentBeanInfo.setBeanDescriptor(componentDescriptor);
        
        if (java.beans.Beans.isDesignTime()) {
            TagAttribute attr = null;
            ValueExpression ve = null;
            String strValue = null;
            boolean booleanValue = false;

            if (null != (attr = this.getAttribute("displayName"))) {
                ve = attr.getValueExpression(ctx, String.class);
                strValue = (String) ve.getValue(ctx);
                if (null != strValue) {
                    componentDescriptor.setDisplayName(strValue);
                }
            }
            if (null != (attr = this.getAttribute("expert"))) {
                ve = attr.getValueExpression(ctx, Boolean.class);
                booleanValue = ((Boolean)ve.getValue(ctx)).booleanValue();
                componentDescriptor.setExpert(booleanValue);
            }
            if (null != (attr = this.getAttribute("hidden"))) {
                ve = attr.getValueExpression(ctx, Boolean.class);
                booleanValue = ((Boolean)ve.getValue(ctx)).booleanValue();
                componentDescriptor.setHidden(booleanValue);
            }
            if (null != (attr = this.getAttribute("name"))) {
                ve = attr.getValueExpression(ctx, String.class);
                strValue = (String) ve.getValue(ctx);
                if (null != strValue) {
                    componentDescriptor.setName(strValue);
                }
            }
            if (null != (attr = this.getAttribute("preferred"))) {
                ve = attr.getValueExpression(ctx, Boolean.class);
                booleanValue = ((Boolean)ve.getValue(ctx)).booleanValue();
                componentDescriptor.setPreferred(booleanValue);
            }
            if (null != (attr = this.getAttribute("shortDescription"))) {
                ve = attr.getValueExpression(ctx, String.class);
                strValue = (String) ve.getValue(ctx);
                if (null != strValue) {
                    componentDescriptor.setShortDescription(strValue);
                }
            }
        }
        
        List<AttachedObjectTarget> targetList = (List<AttachedObjectTarget>)
                componentDescriptor.getValue(AttachedObjectTarget.ATTACHED_OBJECT_TARGETS_KEY);
        if (null == targetList) {
            targetList = new ArrayList<AttachedObjectTarget>();
            componentDescriptor.setValue(AttachedObjectTarget.ATTACHED_OBJECT_TARGETS_KEY,
                    targetList);
        }

	Resource componentResource = 
	    (Resource) attrs.get(Resource.COMPONENT_RESOURCE_KEY);
	if (null == componentResource) {
	    throw new NullPointerException("Unable to find Resource for composite component");
	}
	attrs.put(Resource.COMPONENT_RESOURCE_KEY, componentResource);
        
    }

}
