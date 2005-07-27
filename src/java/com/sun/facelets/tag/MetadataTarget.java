package com.sun.facelets.tag;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

/**
 * Information used with MetaRule for determining how and what Metadata should
 * be wired.
 *
 * @see com.sun.facelets.tag.MetaRule
 * @see com.sun.facelets.tag.Metadata
 * @author Jacob Hookom
 * @version $Id: MetadataTarget.java,v 1.1 2005/07/27 04:32:52 jhook Exp $
 */
public abstract class MetadataTarget {

    /**
     * @param name
     * @return
     */
    public abstract PropertyDescriptor getProperty(String name);

    /**
     * @param type
     * @return
     */
    public abstract boolean isTargetInstanceOf(Class type);

    /**
     * @return
     */
    public abstract Class getTargetClass();

    /**
     * @param name
     * @return
     */
    public abstract Class getPropertyType(String name);

    /**
     * @param name
     * @return
     */
    public abstract Method getWriteMethod(String name);

    /**
     * @param name
     * @return
     */
    public abstract Method getReadMethod(String name);

}
