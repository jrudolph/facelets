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

package com.sun.facelets.el;

import java.util.HashMap;
import java.util.Map;

import javax.el.ValueExpression;
import javax.el.VariableMapper;

/**
 * Default instance of a VariableMapper backed by a Map
 * 
 * @see javax.el.VariableMapper
 * @see javax.el.ValueExpression
 * @see java.util.Map
 * 
 * @author Jacob Hookom
 * @version $Id: DefaultVariableMapper.java,v 1.1 2005/05/21 17:54:52 jhook Exp $
 */
public final class DefaultVariableMapper extends VariableMapper {

    private Map vars;

    public DefaultVariableMapper() {
        super();
    }

    /**
     * @see javax.el.VariableMapper#resolveVariable(java.lang.String)
     */
    public ValueExpression resolveVariable(String name) {
        if (this.vars != null) {
            return (ValueExpression) this.vars.get(name);
        }
        return null;
    }

    /**
     * @see javax.el.VariableMapper#setVariable(java.lang.String, javax.el.ValueExpression)
     */
    public ValueExpression setVariable(String name, ValueExpression expression) {
        if (this.vars == null) {
            this.vars = new HashMap();
        }
        return (ValueExpression) this.vars.put(name, expression);
    }

}
