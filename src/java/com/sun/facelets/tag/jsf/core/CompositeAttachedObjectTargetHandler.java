/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sun.facelets.tag.jsf.core;

import com.sun.facelets.FaceletContext;
import com.sun.facelets.FaceletException;
import com.sun.facelets.tag.TagAttribute;
import com.sun.facelets.tag.TagConfig;
import com.sun.facelets.tag.TagHandler;
import com.sun.facelets.tag.jsf.Component2Handler;
import com.sun.facelets.tag.ui.Component2Ref;
import java.io.IOException;
import javax.el.ELException;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;

/**
 *
 * @author edburns
 */
public class CompositeAttachedObjectTargetHandler extends TagHandler {
    
    public CompositeAttachedObjectTargetHandler(TagConfig config) {
        super(config);
    }

    public void apply(FaceletContext ctx, UIComponent parent) throws IOException, FacesException, FaceletException, ELException {

        String outerIdVal = parent.getId();
        Component2Handler composite = (Component2Handler)
                ctx.getFacesContext().getExternalContext().getRequestMap().get("Component2Handler");
        composite.getAttachedObjectTargetMap().put(outerIdVal, parent);
        
    }
}
