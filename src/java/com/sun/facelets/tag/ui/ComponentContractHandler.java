/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sun.facelets.tag.ui;

import com.sun.facelets.FaceletContext;
import com.sun.facelets.FaceletException;
import com.sun.facelets.tag.TagConfig;
import com.sun.facelets.tag.TagHandler;
import java.io.IOException;
import java.util.Map;
import javax.el.ELException;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;

/**
 *
 * @author edburns
 */
public class ComponentContractHandler extends TagHandler {
    
    public ComponentContractHandler(TagConfig config) {
        super(config);
    }
    
    public void apply(FaceletContext ctx, UIComponent parent) throws IOException, FacesException, FaceletException, ELException {
        ComponentContractHandler.setInsideComponentContract(ctx, true);
        this.nextHandler.apply(ctx, parent);
        ComponentContractHandler.setInsideComponentContract(ctx, false);
    }
    
    private final static String insideComponentContractRequestAttrName =
            "com.sun.facelets.tag.ui.InsideComponentContractRequestAttrName";
    
    public static boolean isInsideComponentContract(FaceletContext ctx) {
        boolean result = false;
        ExternalContext extCtx = ctx.getFacesContext().getExternalContext();
        Map<String, Object> requestMap = extCtx.getRequestMap();
        Boolean resultBoolean = null;
        result = (null != (resultBoolean = (Boolean) requestMap.get(insideComponentContractRequestAttrName))) ?
            resultBoolean.booleanValue() : false;
        return result;
    }
    
    public static void setInsideComponentContract(FaceletContext ctx, boolean newValue) {
        ExternalContext extCtx = ctx.getFacesContext().getExternalContext();
        Map<String, Object> requestMap = extCtx.getRequestMap();
        requestMap.put(insideComponentContractRequestAttrName, newValue);
        
    }
    

}
