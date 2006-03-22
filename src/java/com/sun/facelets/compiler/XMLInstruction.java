package com.sun.facelets.compiler;

import java.io.IOException;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

final class XMLInstruction implements Instruction {

    private final static char[] STOP = new char[0];
    
    private final char[] instruction;
    private final int len;
    
    public XMLInstruction(String literal) {
        this.instruction = literal.toCharArray();
        this.len = this.instruction.length;
    }

    public void write(FacesContext context) throws IOException {
        ResponseWriter rw = context.getResponseWriter();
        rw.writeText(STOP, 0, 0); // hack to get closing elements
        rw.write(this.instruction, 0, this.len);
    }

    public Instruction apply(ExpressionFactory factory, ELContext ctx) {
        return this;
    }

    public boolean isLiteral() {
        return true;
    }

}
