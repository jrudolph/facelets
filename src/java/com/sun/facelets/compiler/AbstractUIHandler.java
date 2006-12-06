package com.sun.facelets.compiler;

import javax.faces.component.UIComponent;

import com.sun.facelets.FaceletContext;
import com.sun.facelets.FaceletHandler;
import com.sun.facelets.tag.TextHandler;
import com.sun.facelets.tag.jsf.core.FacetHandler;

public abstract class AbstractUIHandler implements FaceletHandler, TextHandler {

	public void addComponent(FaceletContext ctx, UIComponent parent, UIComponent c) {
		// possible facet scoped
        String facetName = this.getFacetName(ctx, parent);
        if (facetName == null) {
        	parent.getChildren().add(c);
        } else {
        	parent.getFacets().put(facetName, c);
        }
	}
	
	protected final String getFacetName(FaceletContext ctx, UIComponent parent) {
    	return (String) parent.getAttributes().get(FacetHandler.KEY);
    }

}
