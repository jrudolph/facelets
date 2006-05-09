package com.sun.facelets.compiler;

import java.io.IOException;

import javax.el.ELException;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;

import com.sun.facelets.FaceletContext;
import com.sun.facelets.FaceletException;
import com.sun.facelets.FaceletHandler;

public class EncodingHandler implements FaceletHandler {

    private final FaceletHandler next;
    private final String encoding;
    
    public EncodingHandler(FaceletHandler next, String encoding) {
        this.next = next;
        this.encoding = encoding;
    }

    public void apply(FaceletContext ctx, UIComponent parent)
            throws IOException, FacesException, FaceletException, ELException {
        ctx.getFacesContext().getExternalContext().getRequestMap().put("facelets.Encoding", this.encoding);
        this.next.apply(ctx, parent);
    }

}
