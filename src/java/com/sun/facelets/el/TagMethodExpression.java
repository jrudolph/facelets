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

public final class TagMethodExpression extends MethodExpression implements
        Externalizable {
    
    protected String attr;
    protected MethodExpression orig;

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
            throw new PropertyNotFoundException(this.attr + ": " + pnfe.getMessage(), pnfe);
        } catch (MethodNotFoundException mnfe) {
            throw new MethodNotFoundException(this.attr + ": " + mnfe.getMessage(), mnfe);
        } catch (ELException e) {
            throw new ELException(this.attr + ": " + e.getMessage(), e);
        }
    }

    public Object invoke(ELContext context, Object[] params) {
        try {
            return this.orig.invoke(context, params);
        } catch (PropertyNotFoundException pnfe) {
            throw new PropertyNotFoundException(this.attr + ": " + pnfe.getMessage(), pnfe);
        } catch (MethodNotFoundException mnfe) {
            throw new MethodNotFoundException(this.attr + ": " + mnfe.getMessage(), mnfe);
        } catch (ELException e) {
            throw new ELException(this.attr + ": " + e.getMessage(), e);
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
