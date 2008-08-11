/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sun.facelets.tag.composite;

import com.sun.facelets.FaceletContext;
import com.sun.facelets.FaceletException;
import com.sun.facelets.tag.TagAttribute;
import com.sun.facelets.tag.jsf.ComponentConfig;
import com.sun.facelets.tag.jsf.ComponentHandler;
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
public class InsertFacetHandler extends ComponentHandler {

    private TagAttribute name = null;

    public InsertFacetHandler(ComponentConfig config) {
        super(config);
        this.name = this.getRequiredAttribute("name");
    }

    @Override
    protected void onComponentCreated(FaceletContext ctx, UIComponent c, UIComponent parent) {
        ValueExpression ve = null;
        String strValue = null;
        // Get the value of required the name propertyDescriptor
        ve = name.getValueExpression(ctx, String.class);
        strValue = (String) ve.getValue(ctx);
        
        c.getAttributes().put(UIComponent.FACETS_KEY, strValue);
        
    }

}
