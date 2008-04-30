/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sun.facelets.tag.renderkit;

import com.sun.facelets.FaceletContext;
import com.sun.facelets.tag.jsf.ComponentConfig;
import com.sun.facelets.tag.jsf.ComponentHandler;
import javax.faces.component.UIComponent;

/**
 *
 * @author edburns
 */
public class TemplateRendererTagHandler extends ComponentHandler {
    
    TemplateRendererTagHandler(ComponentConfig config) {
        super(config);
    }
    
    @Override
    protected UIComponent createComponent(FaceletContext ctx) {
        UIComponent result = null;
        
        result = super.createComponent(ctx);
        
        return result;
    }
    
}
