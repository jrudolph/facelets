package com.sun.facelets;

import java.io.IOException;

import javax.faces.FacesException;
import javax.faces.application.ViewHandler;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import com.sun.facelets.client.ClientWriter;
import com.sun.facelets.component.UIFacelet;

public class Facelets {

    public static void encode(UIComponent component) {
        if (component != null) {
            encode(component.getClientId(FacesContext.getCurrentInstance()));
        }
    }
    
    public static void encode(String clientId) {
        if (clientId == null) return;
        
        FacesContext faces = FacesContext.getCurrentInstance();
        UIViewRoot root = faces.getViewRoot();
        if (!(root instanceof UIFacelet)) {
            throw new FacesException("UIViewRoot not instanceof UIFacelet");
        }
        
        ((UIFacelet) root).getEncodeIdSet().add(clientId);
    }
    
    public static ResponseWriter getResponseWriter() throws IOException {
        FacesContext faces = FacesContext.getCurrentInstance();
        ResponseWriter rw = faces.getResponseWriter();
        if (rw == null) {
            ViewHandler vh = faces.getApplication().getViewHandler();
            FaceletViewHandler fvh = null;
            if (vh instanceof FaceletViewHandler) {
                fvh = (FaceletViewHandler) vh;
            } else {
                fvh = new FaceletViewHandler(vh);
            }
            rw = fvh.createResponseWriter(faces);
            faces.setResponseWriter(rw);
        }
        return rw;
    }
    
    public static ClientWriter getClientWriter() throws IOException {
        return new ClientWriter(getResponseWriter());
    }

}
