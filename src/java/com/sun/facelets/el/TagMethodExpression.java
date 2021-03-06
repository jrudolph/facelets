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

package com.sun.facelets.el;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import javax.el.ELContext;
import javax.el.ELException;
import javax.el.MethodExpression;
import javax.el.MethodInfo;
import javax.el.MethodNotFoundException;
import javax.el.PropertyNotFoundException;

import com.sun.facelets.tag.TagAttribute;

/**
 * 
 * 
 * @author Jacob Hookom
 * @version $Id: TagMethodExpression.java,v 1.7 2008/07/13 19:01:43 rlubke Exp $
 */
public final class TagMethodExpression extends MethodExpression implements
        Externalizable {
    
    private static final long serialVersionUID = 1L;
    
    private String attr;
    private MethodExpression orig;

    public TagMethodExpression() {
        super();
    }
    
    public TagMethodExpression(TagAttribute attr, MethodExpression orig) {
        this.attr = attr.toString();
        this.orig = orig;
    }

    public MethodInfo getMethodInfo(ELContext context) {
        try {
            return this.orig.getMethodInfo(context);
        } catch (PropertyNotFoundException pnfe) {
            throw new PropertyNotFoundException(this.attr + ": " + pnfe.getMessage(), pnfe.getCause());
        } catch (MethodNotFoundException mnfe) {
            throw new MethodNotFoundException(this.attr + ": " + mnfe.getMessage(), mnfe.getCause());
        } catch (ELException e) {
            throw new ELException(this.attr + ": " + e.getMessage(), e.getCause());
        }
    }

    public Object invoke(ELContext context, Object[] params) {
        try {
            return this.orig.invoke(context, params);
        } catch (PropertyNotFoundException pnfe) {
            throw new PropertyNotFoundException(this.attr + ": " + pnfe.getMessage(), pnfe.getCause());
        } catch (MethodNotFoundException mnfe) {
            throw new MethodNotFoundException(this.attr + ": " + mnfe.getMessage(), mnfe.getCause());
        } catch (ELException e) {
            throw new ELException(this.attr + ": " + e.getMessage(), e.getCause());
        }
    }

    public String getExpressionString() {
        return this.orig.getExpressionString();
    }

    public boolean equals(Object obj) {
        return this.orig.equals(obj);
    }

    public int hashCode() {
        return this.orig.hashCode();
    }

    public boolean isLiteralText() {
        return this.orig.isLiteralText();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(this.orig);
        out.writeUTF(this.attr);
    }

    public void readExternal(ObjectInput in) throws IOException,
            ClassNotFoundException {
        this.orig = (MethodExpression) in.readObject();
        this.attr = in.readUTF();
    }

    public String toString() {
        return this.attr + ": " + this.orig;
    }
}
