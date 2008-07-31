/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sun.facelets.tag.composite;

import com.sun.facelets.FaceletContext;
import com.sun.facelets.FaceletException;
import com.sun.facelets.tag.TagConfig;
import com.sun.facelets.tag.TagHandler;
import com.sun.facelets.tag.jsf.ComponentSupport;
import java.io.IOException;
import javax.el.ELException;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.component.UIOutput;
import javax.faces.context.FacesContext;

/**
 *
 * @author edburns
 */
public class InsertChildrenHandler extends TagHandler {

    public InsertChildrenHandler(TagConfig config) {
        super(config);
    }
    
    public void apply(FaceletContext ctx, UIComponent parent) throws IOException, FacesException, FaceletException, ELException {
        // only process if it's been created
        UIComponent grandParent = null;
        if (null == parent || 
            (null == (grandParent = parent.getParent())) ||
            !(ComponentSupport.isNew(grandParent))) {
            return;
        }

        FacesContext facesContext = ctx.getFacesContext();
        UIOutput placeHolder = (UIOutput)
                facesContext.getApplication().createComponent("javax.faces.Output");
        placeHolder.setRendererType("javax.faces.ConsumingPageCompositeChildren");
        parent.getChildren().add(placeHolder);
    }

}
