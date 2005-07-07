package com.sun.facelets.el;

import java.util.Map;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import com.sun.el.ExpressionFactoryImpl;
import com.sun.facelets.util.FacesAPI;

public final class ELAdaptor {

    protected static final boolean ELSUPPORT = (FacesAPI.getVersion() >= 12);
    
    private final static String LEGACY_ELCONTEXT_KEY = "com.sun.facelets.legacy.ELCONTEXT";
    
    public ELAdaptor() {
        super();
    }
    
    public final static ExpressionFactory createExpressionFactory(FacesContext faces) {
        if (ELSUPPORT) {
            return faces.getApplication().getExpressionFactory();
        } else {
            return new ExpressionFactoryImpl();
        }
    }
    
    public final static ELContext getELContext(FacesContext faces) {
        if (ELSUPPORT) {
            return faces.getELContext();
        } else {
            Map request = faces.getExternalContext().getRequestMap();
            ELContext ctx = (ELContext) request.get(LEGACY_ELCONTEXT_KEY);
            if (ctx == null) {
                ctx = new LegacyELContext(faces);
                request.put(LEGACY_ELCONTEXT_KEY, ctx);
            }
            return ctx;
        }
    }
    
    public final static void setExpression(UIComponent c, String name, ValueExpression ve) {
        if (FacesAPI.getVersion(c) >= 12) {
            c.setValueExpression(name, ve);
        } else {
            c.setValueBinding(name, new LegacyValueBinding(ve));
        }
    }

}
