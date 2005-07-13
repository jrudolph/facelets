package com.sun.facelets.el;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import javax.el.ELContext;
import javax.el.ELException;
import javax.el.PropertyNotFoundException;
import javax.el.PropertyNotWritableException;
import javax.el.ValueExpression;

import com.sun.facelets.tag.TagAttribute;

public class TagValueExpression extends ValueExpression implements
        Externalizable {

    protected ValueExpression orig;

    protected String attr;

    public TagValueExpression() {
        super();
    }

    public TagValueExpression(TagAttribute attr, ValueExpression orig) {
        this.attr = attr.toString();
        this.orig = orig;
    }

    public Class getExpectedType() {
        return this.orig.getExpectedType();
    }

    public Class getType(ELContext context) {
        try {
            return this.orig.getType(context);
        } catch (PropertyNotFoundException pnfe) {
            throw new PropertyNotFoundException(this.attr + ": "
                    + pnfe.getMessage(), pnfe.getCause());
        } catch (ELException e) {
            throw new ELException(this.attr + ": " + e.getMessage(), e.getCause());
        }
    }

    public Object getValue(ELContext context) {
        try {
            return this.orig.getValue(context);
        } catch (PropertyNotFoundException pnfe) {
            throw new PropertyNotFoundException(this.attr + ": "
                    + pnfe.getMessage(), pnfe.getCause());
        } catch (ELException e) {
            throw new ELException(this.attr + ": " + e.getMessage(), e.getCause());
        }
    }

    public boolean isReadOnly(ELContext context) {
        try {
            return this.orig.isReadOnly(context);
        } catch (PropertyNotFoundException pnfe) {
            throw new PropertyNotFoundException(this.attr + ": "
                    + pnfe.getMessage(), pnfe.getCause());
        } catch (ELException e) {
            throw new ELException(this.attr + ": " + e.getMessage(), e.getCause());
        }
    }

    public void setValue(ELContext context, Object value) {
        try {
            this.orig.setValue(context, value);
        } catch (PropertyNotFoundException pnfe) {
            throw new PropertyNotFoundException(this.attr + ": "
                    + pnfe.getMessage(), pnfe.getCause());
        } catch (PropertyNotWritableException pnwe) {
            throw new PropertyNotWritableException(this.attr + ": "
                    + pnwe.getMessage(), pnwe.getCause());
        } catch (ELException e) {
            throw new ELException(this.attr + ": " + e.getMessage(), e.getCause());
        }
    }

    public boolean equals(Object obj) {
        return this.orig.equals(obj);
    }

    public String getExpressionString() {
        return this.orig.getExpressionString();
    }

    public int hashCode() {
        return this.orig.hashCode();
    }

    public boolean isLiteralText() {
        return this.orig.isLiteralText();
    }

    public void readExternal(ObjectInput in) throws IOException,
            ClassNotFoundException {
        this.orig = (ValueExpression) in.readObject();
        this.attr = in.readUTF();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(this.orig);
        out.writeUTF(this.attr);
    }

    public String toString() {
        return this.attr + ": " + this.orig;
    }
}
