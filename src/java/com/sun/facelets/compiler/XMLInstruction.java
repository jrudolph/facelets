package com.sun.facelets.compiler;

import java.io.IOException;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import com.sun.facelets.el.ELAdaptor;
import com.sun.facelets.el.ELText;
import javax.faces.component.UIComponent;

public class XMLInstruction implements Instruction {
    
    private final static char[] STOP = new char[0];
    
    private final ELText text;
    
    public XMLInstruction(ELText text) {
        this.text = text;
    }

    public void write(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter rw = context.getResponseWriter();
        rw.writeText(STOP, 0, 0); // hack to get closing elements
        this.text.write(rw, ELAdaptor.getELContext(context));
    }

    public Instruction apply(ExpressionFactory factory, ELContext ctx) {
        return new XMLInstruction(text.apply(factory, ctx));
    }

    public boolean isLiteral() {
        return false;
    }
}
