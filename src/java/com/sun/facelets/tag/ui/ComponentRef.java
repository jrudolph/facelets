package com.sun.facelets.tag.ui;

import javax.faces.component.UIComponentBase;

public final class ComponentRef extends UIComponentBase {

    public final static String COMPONENT_TYPE = "facelets.ui.ComponentRef";
    public final static String COMPONENT_FAMILY = "facelets";
    
    public ComponentRef() {
        super();
    }

    public String getFamily() {
        return COMPONENT_FAMILY;
    }

}
