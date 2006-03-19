package com.sun.facelets.compiler;

import java.io.IOException;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.faces.context.FacesContext;

final class XMLInstruction implements Instruction {

    private final char[] instruction;
    private final int len;
    
    public XMLInstruction(String literal) {
        this.instruction = literal.toCharArray();
        this.len = this.instruction.length;
    }

    public void write(FacesContext context) throws IOException {
        context.getResponseWriter().write(this.instruction, 0, this.len);
    }

    public Instruction apply(ExpressionFactory factory, ELContext ctx) {
        return this;
    }

    public boolean isLiteral() {
        return true;
    }

}
