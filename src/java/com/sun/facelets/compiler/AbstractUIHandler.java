package com.sun.facelets.compiler;

import javax.faces.component.UIComponent;

import com.sun.facelets.FaceletContext;
import com.sun.facelets.FaceletHandler;
import com.sun.facelets.tag.TextHandler;
import com.sun.facelets.tag.jsf.core.FacetHandler;

public abstract class AbstractUIHandler implements FaceletHandler, TextHandler {

	public void addComponent(FaceletContext ctx, UIComponent parent, UIComponent c) {
		// possible facet scoped
        String facetName = (String) ctx.getAttribute(FacetHandler.KEY);
        if (facetName == null) {
        	parent.getChildren().add(c);
        } else {
        	parent.getFacets().put(facetName, c);
        }
	}

}
