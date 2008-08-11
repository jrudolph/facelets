/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sun.facelets.tag.composite;

import java.beans.BeanDescriptor;
import java.beans.BeanInfo;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author edburns
 */
public class CompositeComponentBeanInfo extends SimpleBeanInfo implements BeanInfo {

    private BeanDescriptor descriptor = null;
    
    @Override
    public BeanDescriptor getBeanDescriptor() {
        return descriptor;
    }
    
    public void setBeanDescriptor(BeanDescriptor newDescriptor) {
        descriptor = newDescriptor;
    }

    @Override
    public PropertyDescriptor[] getPropertyDescriptors() {
        List<PropertyDescriptor> list = getPropertyDescriptorsList();
        PropertyDescriptor [] result = new PropertyDescriptor[list.size()];
        list.toArray(result);
        return result;
    }

    private List<PropertyDescriptor> propertyDescriptors;
    public List<PropertyDescriptor> getPropertyDescriptorsList() {

        if (null == propertyDescriptors) {
            propertyDescriptors = new ArrayList<PropertyDescriptor>();
        }
        return propertyDescriptors;
    }
    
    
}
