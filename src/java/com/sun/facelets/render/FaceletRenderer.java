package com.sun.facelets.render;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.render.Renderer;

public class FaceletRenderer extends Renderer {
    
    public final static String PARAM_EVENT = "javax.faces.Event";

    public FaceletRenderer() {
        super();
    }

    public void decode(FacesContext faces, UIComponent component) {
        
        super.decode(faces, component);
    }

}
