/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sun.facelets.tag.composite;

import com.sun.facelets.FaceletContext;
import com.sun.facelets.FaceletException;
import com.sun.facelets.tag.TagAttribute;
import com.sun.facelets.tag.TagConfig;
import com.sun.facelets.tag.TagHandler;
import com.sun.facelets.tag.jsf.ComponentSupport;
import java.io.IOException;
import javax.el.ELException;
import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.component.UIOutput;
import javax.faces.context.FacesContext;

/**
 *
 * @author edburns
 */
public class InsertFacetHandler extends TagHandler {

    private TagAttribute name = null;

    public InsertFacetHandler(TagConfig config) {
        super(config);
        this.name = this.getRequiredAttribute("name");

    }
    
    public void apply(FaceletContext ctx, UIComponent parent) throws IOException, FacesException, FaceletException, ELException {
        // only process if it's been created
        UIComponent grandParent = null;
        if (null == parent || 
            (null == (grandParent = parent.getParent())) ||
            !(ComponentSupport.isNew(grandParent))) {
            return;
        }

        ValueExpression ve = null;
        String strValue = null;
        // Get the value of required the name propertyDescriptor
        ve = name.getValueExpression(ctx, String.class);
        strValue = (String) ve.getValue(ctx);
        
        FacesContext facesContext = ctx.getFacesContext();
        UIOutput placeHolder = (UIOutput)
                facesContext.getApplication().createComponent("javax.faces.Output");
        placeHolder.setRendererType("javax.faces.CompositeFacet");
        placeHolder.getAttributes().put(UIComponent.FACETS_KEY, strValue);
        
        parent.getChildren().add(placeHolder);
        
    }

}
