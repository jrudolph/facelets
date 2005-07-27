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

package com.sun.facelets.impl;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.el.ELContext;
import javax.el.ELException;
import javax.el.ELResolver;
import javax.el.ExpressionFactory;
import javax.el.FunctionMapper;
import javax.el.ValueExpression;
import javax.el.VariableMapper;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import com.sun.facelets.FaceletContext;
import com.sun.facelets.FaceletException;
import com.sun.facelets.el.DefaultVariableMapper;
import com.sun.facelets.el.ELAdaptor;

/**
 * Default FaceletContext implementation.
 * 
 * A single FaceletContext is used for all Facelets involved in an invocation of
 * {@link com.sun.facelets.Facelet#apply(FacesContext, UIComponent) Facelet#apply(FacesContext, UIComponent)}.
 * This means that included Facelets are treated the same as the JSP include
 * directive.
 * 
 * @author Jacob Hookom
 * @version $Id: DefaultFaceletContext.java,v 1.5 2005/07/27 04:32:49 jhook Exp $
 */
final class DefaultFaceletContext extends FaceletContext {

    private final FacesContext faces;

    private final ELContext ctx;

    private final DefaultFacelet facelet;

    private VariableMapper varMapper;

    private FunctionMapper fnMapper;

    private final Map ids;

    public DefaultFaceletContext(FacesContext faces, DefaultFacelet facelet) {
        this.ctx = ELAdaptor.getELContext(faces);
        this.ids = new HashMap();
        this.faces = faces;
        this.facelet = facelet;
        this.varMapper = this.ctx.getVariableMapper();
        if (this.varMapper == null) {
            this.varMapper = new DefaultVariableMapper();
        }
        this.fnMapper = this.ctx.getFunctionMapper();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sun.facelets.FaceletContext#getFacesContext()
     */
    public FacesContext getFacesContext() {
        return this.faces;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sun.facelets.FaceletContext#getExpressionFactory()
     */
    public ExpressionFactory getExpressionFactory() {
        return this.facelet.getExpressionFactory();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sun.facelets.FaceletContext#setVariableMapper(javax.el.VariableMapper)
     */
    public void setVariableMapper(VariableMapper varMapper) {
        // Assert.param("varMapper", varMapper);
        this.varMapper = varMapper;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sun.facelets.FaceletContext#setFunctionMapper(javax.el.FunctionMapper)
     */
    public void setFunctionMapper(FunctionMapper fnMapper) {
        // Assert.param("fnMapper", fnMapper);
        this.fnMapper = fnMapper;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sun.facelets.FaceletContext#includeFacelet(javax.faces.component.UIComponent,
     *      java.lang.String)
     */
    public void includeFacelet(UIComponent parent, String relativePath)
            throws IOException, FaceletException, FacesException, ELException {
        this.facelet.include(this, parent, relativePath);
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.el.ELContext#getFunctionMapper()
     */
    public FunctionMapper getFunctionMapper() {
        return this.fnMapper;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.el.ELContext#getVariableMapper()
     */
    public VariableMapper getVariableMapper() {
        return this.varMapper;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.el.ELContext#getContext(java.lang.Class)
     */
    public Object getContext(Class key) {
        return this.ctx.getContext(key);
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.el.ELContext#putContext(java.lang.Class, java.lang.Object)
     */
    public void putContext(Class key, Object contextObject) {
        this.ctx.putContext(key, contextObject);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sun.facelets.FaceletContext#generateUniqueId(java.lang.String)
     */
    public String generateUniqueId(String base) {
        Integer cnt = (Integer) this.ids.get(base);
        if (cnt == null) {
            this.ids.put(base, new Integer(0));
            return base;
        } else {
            int i = cnt.intValue() + 1;
            this.ids.put(base, new Integer(i));
            return base + "_" + i;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sun.facelets.FaceletContext#getAttribute(java.lang.String)
     */
    public Object getAttribute(String name) {
        if (this.varMapper != null) {
            ValueExpression ve = this.varMapper.resolveVariable(name);
            if (ve != null) {
                return ve.getValue(this);
            }
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sun.facelets.FaceletContext#setAttribute(java.lang.String,
     *      java.lang.Object)
     */
    public void setAttribute(String name, Object value) {
        if (this.varMapper != null) {
            if (value == null) {
                this.varMapper.setVariable(name, null);
            } else {
                this.varMapper.setVariable(name, this.facelet
                        .getExpressionFactory().createValueExpression(value,
                                Object.class));
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sun.facelets.FaceletContext#includeFacelet(javax.faces.component.UIComponent,
     *      java.net.URL)
     */
    public void includeFacelet(UIComponent parent, URL absolutePath)
            throws IOException, FaceletException, FacesException, ELException {
        this.facelet.include(this, parent, absolutePath);
    }

    public ELResolver getELResolver() {
        return this.ctx.getELResolver();
    }
}
