/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sun.facelets.tag.renderkit;

import java.io.IOException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.render.Renderer;

/**
 *
 * @author edburns
 */
public class TemplateRenderer extends Renderer {

    public TemplateRenderer(String rendererResourceName) {
        
    }
    
    private String rendererResourceName;

    @Override
    public void decode(FacesContext arg0, UIComponent arg1) {
        super.decode(arg0, arg1);
    }

    @Override
    public void encodeBegin(FacesContext arg0, UIComponent arg1) throws IOException {
        super.encodeBegin(arg0, arg1);
    }

    @Override
    public void encodeChildren(FacesContext arg0, UIComponent arg1) throws IOException {
        super.encodeChildren(arg0, arg1);
    }

    @Override
    public void encodeEnd(FacesContext arg0, UIComponent arg1) throws IOException {
        super.encodeEnd(arg0, arg1);
    }

    @Override
    public boolean getRendersChildren() {
        return super.getRendersChildren();
    }
    
    

}
