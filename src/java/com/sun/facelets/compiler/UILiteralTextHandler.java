package com.sun.facelets.compiler;

import javax.faces.component.UIComponent;

import java.io.IOException;

import javax.el.ELException;
import javax.faces.FacesException;

import com.sun.facelets.FaceletContext;
import com.sun.facelets.FaceletException;
import com.sun.facelets.FaceletHandler;

final class UILiteralTextHandler implements FaceletHandler {
    
    protected final String txtString;
    
    public UILiteralTextHandler(String txtString) {
        this.txtString = txtString;
    }

    public void apply(FaceletContext ctx, UIComponent parent)
            throws IOException, FacesException, FaceletException, ELException {
        if (parent != null) {
            parent.getChildren().add(new UILiteralText(this.txtString));
        }
    }
}
