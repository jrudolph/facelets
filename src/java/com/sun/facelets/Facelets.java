package com.sun.facelets;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

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

}
