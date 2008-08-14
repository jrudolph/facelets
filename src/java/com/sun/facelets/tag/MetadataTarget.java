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
 * @version $Id: MetadataTarget.java,v 1.2.28.1 2008/08/14 21:11:36 rlubke Exp $
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
