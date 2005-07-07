package com.sun.facelets.el;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.el.ELContext;
import javax.el.ELResolver;
import javax.el.FunctionMapper;
import javax.el.PropertyNotWritableException;
import javax.el.VariableMapper;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.el.PropertyResolver;
import javax.faces.el.VariableResolver;

/**
 * @author Jacob Hookom
 * @deprecated
 */
public final class LegacyELContext extends ELContext {

    private static final String[] IMPLICIT_OBJECTS = new String[] {
        "application", "applicationScope", "cookie", "facesContext",
        "header", "headerValues", "initParam", "param", "paramValues",
        "request", "requestScope", "session", "sessionScope", "view" };
    
    private final static FunctionMapper functions = new EmptyFunctionMapper();
    
    protected final FacesContext faces;
    protected final ELResolver resolver;
    protected final VariableMapper variables;
    
    public LegacyELContext(FacesContext faces) {
        this.faces = faces;
        this.resolver = new LegacyELResolver();
        this.variables = new DefaultVariableMapper();
    }

    public ELResolver getELResolver() {
        return this.resolver;
    }

    public FunctionMapper getFunctionMapper() {
        return functions;
    }

    public VariableMapper getVariableMapper() {
        return this.variables;
    }
    
    private final class LegacyELResolver extends ELResolver {

        public Class getCommonPropertyType(ELContext context, Object base) {
            return Object.class;
        }

        public Iterator getFeatureDescriptors(ELContext context, Object base) {
            return Collections.EMPTY_LIST.iterator();
        }
        
        private VariableResolver getVariableResolver() {
            return faces.getApplication().getVariableResolver();
        }
        
        private PropertyResolver getPropertyResolver() {
            return faces.getApplication().getPropertyResolver();
        }

        public Class getType(ELContext context, Object base, Object property) {
            if (property == null) {
                return null;
            }
            context.setPropertyResolved(true);
            if (base == null) {
                Object obj = this.getVariableResolver().resolveVariable(faces, property.toString());
                return (obj != null) ? obj.getClass() : null;
            } else {
                if (base instanceof List || base.getClass().isArray()) {
                    return this.getPropertyResolver().getType(base, Integer.parseInt(property.toString()));
                } else {
                    return this.getPropertyResolver().getType(base, property);
                }
            }
        }

        public Object getValue(ELContext context, Object base, Object property) {
            if (property == null) {
                return null;
            }
            context.setPropertyResolved(true);
            if (base == null) {
                return this.getVariableResolver().resolveVariable(faces, property.toString());
            } else {
                if (base instanceof List || base.getClass().isArray()) {
                    return this.getPropertyResolver().getValue(base, Integer.parseInt(property.toString()));
                } else {
                    return this.getPropertyResolver().getValue(base, property);
                }
            }
        }

        public boolean isReadOnly(ELContext context, Object base, Object property) {
            if (property == null) {
                return true;
            }
            context.setPropertyResolved(true);
            if (base == null) {
                return false;  // what can I do?
            } else {
                if (base instanceof List || base.getClass().isArray()) {
                    return this.getPropertyResolver().isReadOnly(base, Integer.parseInt(property.toString()));
                } else {
                    return this.getPropertyResolver().isReadOnly(base, property);
                }
            }
        }

        public void setValue(ELContext context, Object base, Object property, Object value) {
            if (property == null) {
                throw new PropertyNotWritableException("Null Property");
            }
            context.setPropertyResolved(true);
            if (base == null) {
                if (Arrays.binarySearch(IMPLICIT_OBJECTS, property.toString()) >= 0) {
                    throw new PropertyNotWritableException("Implicit Variable Not Setable: " +property);
                } else {
                    Map scope = this.resolveScope(property.toString());
                    this.getPropertyResolver().setValue(scope, property, value);
                }
            } else {
                if (base instanceof List || base.getClass().isArray()) {
                    this.getPropertyResolver().setValue(base, Integer.parseInt(property.toString()), value);
                } else {
                    this.getPropertyResolver().setValue(base, property, value);
                }
            }
            
        }
        
        private final Map resolveScope(String var)
        {
            ExternalContext ext = faces.getExternalContext();

            // cycle through the scopes to find a match, if no
            // match is found, then return the requestScope
            Map map = ext.getRequestMap();
            if (!map.containsKey(var))
            {
                map = ext.getSessionMap();
                if (!map.containsKey(var))
                {
                    map = ext.getApplicationMap();
                    if (!map.containsKey(var))
                    {
                        map = ext.getRequestMap();
                    }
                }
            }
            return map;
        }
    }
    
    private final static class EmptyFunctionMapper extends FunctionMapper {

        public Method resolveFunction(String prefix, String localName) {
            return null;
        }
        
    }

}
