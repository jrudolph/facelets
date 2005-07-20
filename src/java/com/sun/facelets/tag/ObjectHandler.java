/**
 * Copyright 2005 Sun Microsystems, Inc. All rights reserved.
 * Licensed under the Common Development and Distribution License,
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.sun.com/cddl/
 *   
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or 
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package com.sun.facelets.tag;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.el.ELException;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;

import com.sun.facelets.FaceletContext;
import com.sun.facelets.el.LegacyValueBinding;
import com.sun.facelets.util.FacesAPI;

/**
 * Utility class for wiring TagAttributes to Object properties. Uses the
 * "facelets.tag.object" logger.
 * 
 * @see com.sun.facelets.tag.TagAttribute
 * @author Jacob Hookom
 * @version $Id: ObjectHandler.java,v 1.4 2005/07/20 06:37:07 jhook Exp $
 */
abstract class ObjectHandler extends TagHandler {

    private final static Logger log = Logger.getLogger("facelets.tag.object");

    private class AttributeMapper implements Mapper {

        protected final String property;

        protected final String value;

        public AttributeMapper(String property, String value) {
            this.property = property;
            this.value = value;
        }

        public void apply(Object obj, FaceletContext ctx)
                throws FacesException, ELException {
            ((UIComponent) obj).getAttributes().put(this.property, this.value);
        }
    }

    private class EvalExpressionMapper implements Mapper {
        protected final TagAttribute attr;

        protected final Method m;

        protected final Class type;

        public EvalExpressionMapper(TagAttribute attr, Method m, Class type) {
            this.m = m;
            this.attr = attr;
            this.type = type;
        }

        public void apply(Object obj, FaceletContext ctx)
                throws FacesException, ELException {
            Object val = this.attr.getObject(ctx, this.type);
            try {
                this.m.invoke(obj, new Object[] { val });
            } catch (InvocationTargetException ite) {
                throw new TagAttributeException(tag, this.attr, ite.getCause());
            } catch (Exception e) {
                throw new TagAttributeException(tag, this.attr, e);
            }
        }
    }

    private class ExpressionMapper implements Mapper {
        protected final TagAttribute attr;

        protected final String property;

        protected final Class type;

        public ExpressionMapper(String property, TagAttribute attr, Class type) {
            this.property = property;
            this.attr = attr;
            this.type = type;
        }

        public void apply(Object obj, FaceletContext ctx)
                throws FacesException, ELException {
            ((UIComponent) obj).setValueExpression(this.property, this.attr
                    .getValueExpression(ctx, this.type));
        }
    }

    private class ValueBindingMapper implements Mapper {
        protected final TagAttribute attr;

        protected final String property;

        protected final Class type;

        public ValueBindingMapper(String property, TagAttribute attr, Class type) {
            this.property = property;
            this.attr = attr;
            this.type = type;
        }

        public void apply(Object obj, FaceletContext ctx)
                throws FacesException, ELException {
            ((UIComponent) obj).setValueBinding(this.property,
                    new LegacyValueBinding(this.attr.getValueExpression(ctx,
                            this.type)));
        }
    }

    private interface Mapper {
        public void apply(Object obj, FaceletContext ctx)
                throws FacesException, ELException;
    }

    private class PropertyMapper implements Mapper {

        protected final TagAttribute attr;

        protected final Method m;

        protected final Object[] value;

        public PropertyMapper(TagAttribute attr, Method m, Object value) {
            this.attr = attr;
            this.m = m;
            this.value = new Object[] { value };
        }

        public void apply(Object obj, FaceletContext ctx)
                throws FacesException, ELException {
            try {
                this.m.invoke(obj, (Object[]) this.value);
            } catch (InvocationTargetException ite) {
                throw new TagAttributeException(tag, this.attr, ite.getCause());
            } catch (Exception e) {
                throw new TagAttributeException(tag, this.attr, e);
            }
        }
    }

    private final static Map descriptors = new WeakHashMap();

    private Class expectedType;

    private Mapper[] mappers;

    /**
     * @param config
     */
    public ObjectHandler(TagConfig config) {
        super(config);
    }

    private final Mapper[] createMappers(FaceletContext ctx, Object obj) {
        Class type = obj.getClass();
        if (this.expectedType == null || !this.expectedType.equals(type)) {
            // get our attributes
            Map pdm = this.getPropertyDescriptors(type);
            boolean isUIComponent = (obj instanceof UIComponent);
            boolean isELSupported = (isUIComponent && FacesAPI
                    .getVersion((UIComponent) obj) >= 12);

            // do processing
            TagAttribute[] aa = this.tag.getAttributes().getAll();
            Collection ma = new ArrayList(aa.length);
            TagAttribute a;
            String n;
            Method m;
            Mapper mapper;
            PropertyDescriptor pd;
            for (int i = 0; i < aa.length; i++) {
                mapper = null;
                a = aa[i];
                n = a.getLocalName();
                n = this.transformAttribute(obj, n);
                if (!this.isAttributeHandled(obj, n)) {
                    pd = (PropertyDescriptor) pdm.get(n);
                    if (a.isLiteral()) {
                        if (pd == null || pd.getWriteMethod() == null) {
                            this.warnAttr(a, type, n);
                            if (isUIComponent) {
                                mapper = new AttributeMapper(n, a.getValue());
                            }
                        } else {
                            mapper = new PropertyMapper(a, pd.getWriteMethod(),
                                    ctx.getExpressionFactory().coerceToType(
                                            a.getValue(), pd.getPropertyType()));
                        }
                    } else {
                        // not literal
                        if (pd == null) {
                            this.warnAttr(a, type, n);
                            if (isUIComponent) {
                                if (isELSupported) {
                                    mapper = new ExpressionMapper(n, a,
                                            Object.class);
                                } else {
                                    mapper = new ValueBindingMapper(n, a,
                                            Object.class);
                                }
                            }
                        } else {
                            if (isUIComponent) {
                                if (isELSupported) {
                                    mapper = new ExpressionMapper(n, a, pd
                                            .getPropertyType());
                                } else {
                                    mapper = new ValueBindingMapper(n, a,
                                            Object.class);
                                }
                            } else {
                                mapper = new EvalExpressionMapper(a, pd
                                        .getWriteMethod(), pd.getPropertyType());
                            }
                        }
                    }
                    if (mapper != null) {
                        ma.add(mapper);
                    }
                }
            }
            this.mappers = (Mapper[]) ma.toArray(new Mapper[ma.size()]);
            this.expectedType = type;
        }
        return this.mappers;
    }

    private void warnAttr(TagAttribute attr, Class type, String n) {
        if (log.isLoggable(Level.WARNING)) {
            log.warning(attr.getLocation() + " <" + this.tag.getQName() + " "
                    + attr.getQName() + "=\"" + attr.getValue()
                    + "\"> Property '" + n + "' is not on type: "
                    + type.getName());
        }
    }

    private final Map getPropertyDescriptors(Class type) {
        Map m = (Map) descriptors.get(type.getName());
        if (m == null) {
            try {
                PropertyDescriptor[] pd = Introspector.getBeanInfo(type)
                        .getPropertyDescriptors();
                m = new HashMap();
                String n;
                for (int i = 0; i < pd.length; i++) {
                    n = pd[i].getName();
                    if (!"class".equals(n) && !"id".equals(n)) {
                        m.put(pd[i].getName(), pd[i]);
                    }
                }
                descriptors.put(type.getName(), m);
            } catch (Exception e) {
                throw new TagException(this.tag, "Error Compiling Attributes",
                        e);
            }
        }
        return m;
    }

    /**
     * Determine if the passed attribute name is handled for the Object.
     * 
     * @param obj
     *            Object the attribute would be applied to
     * @param n
     *            attribute name
     * @return true if handled
     */
    protected boolean isAttributeHandled(Object obj, String n) {
        return false;
    }

    /**
     * Any TagAttribute that wasn't handled, wire them to the passed Object.
     * Takes attribute name and associates them to the bean property name.
     * <p />
     * If the Object is an instance of UIComponent, then we can fall back on
     * wiring arbitrary TagAttributes to UIComponent's attribute Map. Also, in
     * the case of UIComponent, if the TagAttribute is not literal, it will
     * instead invoke setValueExpression on the component.
     * 
     * @see TagAttribute
     * @see UIComponent#getAttributes()
     * @see UIComponent#setValueExpression(java.lang.String,
     *      javax.el.ValueExpression)
     * @param ctx
     *            FaceletContext to use
     * @param obj
     *            instance to apply TagAttributes to
     */
    protected void setAttributes(FaceletContext ctx, Object obj) {
        // get property mapper
        Mapper[] m = this.createMappers(ctx, obj);
        for (int i = 0; i < m.length; i++) {
            m[i].apply(obj, ctx);
        }
    }

    /**
     * Allows a TagAttribute name to be wired to a different bean property name.
     * Example would be "class" to "styleClass".
     * 
     * @param obj
     *            Object the attribute would be applied to
     * @param n
     *            attribute name
     * @return passed attribute name, or an alternate one
     */
    protected String transformAttribute(Object obj, String n) {
        return n;
    }

}
