/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package com.sun.facelets.tag.composite;

import com.sun.facelets.FaceletContext;
import com.sun.facelets.FaceletException;
import com.sun.facelets.tag.TagAttribute;
import com.sun.facelets.tag.TagConfig;
import com.sun.facelets.tag.TagException;
import com.sun.facelets.tag.TagHandler;
import com.sun.facelets.tag.jsf.ComponentSupport;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import javax.el.ELException;
import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;

/**
 *
 * @author edburns
 */
public class AttributeHandler extends TagHandler {
    
    private TagAttribute name = null;
    private TagAttribute required = null;


    public AttributeHandler(TagConfig config) {
        super(config);
        this.name = this.getRequiredAttribute("name");
        this.required = this.getAttribute("required");
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
        boolean booleanValue = false;

        CompositeComponentBeanInfo componentBeanInfo = (CompositeComponentBeanInfo)
                componentAttrs.get(UIComponent.BEANINFO_KEY);

        List<PropertyDescriptor> attributes = componentBeanInfo.getPropertyDescriptorsList();
        PropertyDescriptor propertyDescriptor = null;
        TagAttribute attr = null;

        // Get the value of required the name propertyDescriptor
        ve = name.getValueExpression(ctx, String.class);
        strValue = (String) ve.getValue(ctx);
        try {
            propertyDescriptor = new PropertyDescriptor(strValue, null, null);
        } catch (IntrospectionException ex) {
            throw new  TagException(tag, "Unable to create property descriptor for property " + strValue, ex);
        }
        
        if (null != required) {
            ve = required.getValueExpression(ctx, Boolean.class);
            propertyDescriptor.setValue("required", ve);
        }
        
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
