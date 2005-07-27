package com.sun.facelets.tag;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author Jacob Hookom
 * @version $Id: MetadataTargetImpl.java,v 1.1 2005/07/27 04:32:53 jhook Exp $
 */
final class MetadataTargetImpl extends MetadataTarget {

    private final Map pd;
    private final Class type;
    
    
    public MetadataTargetImpl(Class type) throws IntrospectionException {
        this.type = type;
        this.pd = new HashMap();
        BeanInfo info = Introspector.getBeanInfo(type);
        PropertyDescriptor[] pda = info.getPropertyDescriptors();
        for (int i = 0; i < pda.length; i++) {
            this.pd.put(pda[i].getName(), pda[i]);
        }
    }

    public PropertyDescriptor getProperty(String name) {
        return (PropertyDescriptor) this.pd.get(name);
    }

    public boolean isTargetInstanceOf(Class type) {
        return type.isAssignableFrom(this.type);
    }

    public Class getTargetClass() {
        return this.type;
    }

    public Class getPropertyType(String name) {
        PropertyDescriptor pd = this.getProperty(name);
        if (pd != null) {
            return pd.getPropertyType();
        }
        return null;
    }

    public Method getWriteMethod(String name) {
        PropertyDescriptor pd = this.getProperty(name);
        if (pd != null) {
            return pd.getWriteMethod();
        }
        return null;
    }

    public Method getReadMethod(String name) {
        PropertyDescriptor pd = this.getProperty(name);
        if (pd != null) {
            return pd.getReadMethod();
        }
        return null;
    }

}
