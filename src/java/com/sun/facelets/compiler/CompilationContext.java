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

package com.sun.facelets.compiler;

import java.lang.reflect.Method;

import javax.el.ArrayELResolver;
import javax.el.BeanELResolver;
import javax.el.CompositeELResolver;
import javax.el.ELContext;
import javax.el.ELResolver;
import javax.el.FunctionMapper;
import javax.el.ListELResolver;
import javax.el.MapELResolver;
import javax.el.ValueExpression;
import javax.el.VariableMapper;

import com.sun.facelets.tag.TagLibrary;

/**
 * ELContext implementation that is used to capture EL state during parsing.
 * 
 * @see javax.el.ELContext
 * @see com.sun.facelets.compiler.NamespaceManager
 * @see com.sun.facelets.tag.TagLibrary
 * 
 * @author Jacob Hookom
 * @version $Id: CompilationContext.java,v 1.2 2005/06/07 02:15:35 jhook Exp $
 */
final class CompilationContext extends ELContext {

    private final static CompositeELResolver ELRESOLVER = new CompositeELResolver();

    static {
        ELRESOLVER.add(new MapELResolver());
        ELRESOLVER.add(new ListELResolver());
        ELRESOLVER.add(new ArrayELResolver());
        ELRESOLVER.add(new BeanELResolver());
    }

    private final FunctionMapper fnMapper = new FunctionMapper() {
        public Method resolveFunction(String prefix, String localName) {
            String ns = nsMngr.getNamespace(prefix);
            Method m = library.createFunction(ns, localName);
            if (m != null) {

            }
            return m;
        }
    };

    private final static VariableMapper VARMAPPER = new VariableMapper() {
        public ValueExpression setVariable(String variable,
                ValueExpression expression) {
            return null;
        }

        public ValueExpression resolveVariable(String variable) {
            return null;
        }
    };

    protected final NamespaceManager nsMngr;

    protected final TagLibrary library;

    public CompilationContext(NamespaceManager nsMngr, TagLibrary library) {
        this.nsMngr = nsMngr;
        this.library = library;
    }

    public FunctionMapper getFunctionMapper() {
        return this.fnMapper;
    }

    public VariableMapper getVariableMapper() {
        return VARMAPPER;
    }

    public FunctionMapper createFunctionMapper() {
        return null;
    }

    public ELResolver getELResolver() {
        return ELRESOLVER;
    }

}
