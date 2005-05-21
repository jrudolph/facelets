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

import javax.el.ELContext;
import javax.el.PropertyNotWritableException;
import javax.el.ValueExpression;

/**
 * ValueExpression wrapper for an Object instance.
 * 
 * @author Jacob Hookom
 * @version $Id: LiteralValueExpression.java,v 1.1 2005/05/21 17:54:53 jhook Exp $
 */
public final class LiteralValueExpression extends ValueExpression {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    protected final Object obj;

    public LiteralValueExpression(Object obj) {
        this.obj = obj;
    }

    /**
     * Returns Object.class
     * 
     * @return Object.class
     * @see javax.el.ValueExpression#getExpectedType()
     */
    public Class getExpectedType() {
        return Object.class;
    }

    /**
     * @return object's class, if null, return null
     * @see javax.el.ValueExpression#getType(javax.el.ELContext)
     */
    public Class getType(ELContext ctx) {
        if (this.obj != null) {
            return this.obj.getClass();
        }
        return null;
    }

    /**
     * Returns this Object
     * 
     * @return this Object
     * @see javax.el.ValueExpression#getValue(javax.el.ELContext)
     */
    public Object getValue(ELContext ctx) {
        return this.obj;
    }

    /**
     * Immutable, so always returns true
     * 
     * @return always returns true
     * @see javax.el.ValueExpression#isReadOnly(javax.el.ELContext)
     */
    public boolean isReadOnly(ELContext ctx) {
        return true;
    }

    /**
     * Always throws a
     * {@link javax.el.PropertyNotWritableException PropertyNotWritableException}
     * 
     * @see javax.el.ValueExpression#setValue(javax.el.ELContext,
     *      java.lang.Object)
     */
    public void setValue(ELContext ctx, Object value) {
        throw new PropertyNotWritableException("Literal");
    }

    /**
     * Always returns false
     * 
     * @return always returns false
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object arg0) {
        return false;
    }

    /**
     * Return the Object's toString(), otherwise if null, return an empty String
     * 
     * @return this object's toString, if null, return an empty String
     * @see javax.el.Expression#getExpressionString()
     */
    public String getExpressionString() {
        if (this.obj != null) {
            return this.obj.toString();
        }
        return "";
    }

    /**
     * The Object's hashCode
     * 
     * @return the object's hashCode
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return this.obj.hashCode();
    }

    /**
     * Always returns false.
     * 
     * @return false
     * @see javax.el.Expression#isLiteralText()
     */
    public boolean isLiteralText() {
        return false;
    }
}
