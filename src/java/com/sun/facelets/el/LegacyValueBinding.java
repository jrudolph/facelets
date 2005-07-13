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

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import javax.el.ELContext;
import javax.el.ELException;
import javax.el.PropertyNotWritableException;
import javax.el.ValueExpression;
import javax.faces.context.FacesContext;
import javax.faces.el.EvaluationException;
import javax.faces.el.PropertyNotFoundException;
import javax.faces.el.ValueBinding;

/**
 * @author Jacob Hookom
 * @deprecated
 */
public final class LegacyValueBinding extends ValueBinding implements Externalizable {

    protected ValueExpression delegate;
    
    public LegacyValueBinding() {
        super();
    }
    
    public LegacyValueBinding(ValueExpression ve) {
        this.delegate = ve;
    }

    public Object getValue(FacesContext context) throws EvaluationException,
            PropertyNotFoundException {
        ELContext ctx = ELAdaptor.getELContext(context);
        try {
            return this.delegate.getValue(ctx);
        } catch (javax.el.PropertyNotFoundException e) {
            throw new PropertyNotFoundException(e.getMessage(), e.getCause());
        } catch (ELException e) {
            throw new EvaluationException(e.getMessage(), e.getCause());
        }
    }

    public void setValue(FacesContext context, Object value)
            throws EvaluationException, PropertyNotFoundException {
        ELContext ctx = ELAdaptor.getELContext(context);
        try {
            this.delegate.setValue(ctx, value);
        } catch (PropertyNotWritableException e) {
            throw new PropertyNotFoundException(e.getMessage(), e.getCause());
        } catch (javax.el.PropertyNotFoundException e) {
            throw new PropertyNotFoundException(e.getMessage(), e.getCause());
        } catch (ELException e) {
            throw new EvaluationException(e.getMessage(), e.getCause());
        }
    }

    public boolean isReadOnly(FacesContext context) throws EvaluationException,
            PropertyNotFoundException {
        ELContext ctx = ELAdaptor.getELContext(context);
        try {
            return this.delegate.isReadOnly(ctx);
        } catch (javax.el.PropertyNotFoundException e) {
            throw new PropertyNotFoundException(e.getMessage(), e.getCause());
        } catch (ELException e) {
            throw new EvaluationException(e.getMessage(), e.getCause());
        }
    }

    public Class getType(FacesContext context) throws EvaluationException,
            PropertyNotFoundException {
        ELContext ctx = ELAdaptor.getELContext(context);
        try {
            return this.delegate.getType(ctx);
        } catch (javax.el.PropertyNotFoundException e) {
            throw new PropertyNotFoundException(e.getMessage(), e.getCause());
        } catch (ELException e) {
            throw new EvaluationException(e.getMessage(), e.getCause());
        }
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.delegate = (ValueExpression) in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(this.delegate);
    }

    public String getExpressionString() {
        return this.delegate.getExpressionString();
    }
}
