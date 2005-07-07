package com.sun.facelets.util;

import javax.faces.application.Application;
import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;

public final class FacesAPI {

    private static final int version = specifyVersion();
    private static final Class[] UIC_SIG = new Class[] { String.class };
    
    private FacesAPI() {
        super();
    }
    
    private final static int specifyVersion() {
        try {
            Application.class.getMethod("getExpressionFactory", null);
        } catch (NoSuchMethodException e) {
            return 11;
        }
        return 12;
    }
    
    public final static int getVersion() {
        return version;
    }
    
    public final static int getVersion(UIComponent c) {
        return (version >= 12 && c instanceof UIComponentBase) ? 12 : 11;
    }
}
