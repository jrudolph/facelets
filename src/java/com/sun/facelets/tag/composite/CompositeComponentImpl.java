/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sun.facelets.tag.composite;

import javax.faces.application.Resource;
import javax.faces.component.CompositeComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;

/**
 *
 * @author edburns
 */
public class CompositeComponentImpl extends UIComponentBase implements CompositeComponent {
    
    public static final String TYPE = "javax.faces.Composite";

    @Override
    public String getFamily() {
        return TYPE;
    }
    
    private Resource resource;

    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    // ----------------------------------------------------- StateHolder Methods


    private Object[] values;

    @Override
    public Object saveState(FacesContext context) {

        if (values == null) {
            values = new Object[3];
        }

        values[0] = super.saveState(context);
        values[1] = getResource().getResourceName();
        values[2] = getResource().getLibraryName();
        return (values);

    }

    @Override
    public void restoreState(FacesContext context, Object state) {

        values = (Object[]) state;
        super.restoreState(context, values[0]);
        String
                resourceName = (String) values[1],
                libraryName = (String) values[2];
        resource = context.getApplication().getResourceHandler().
                createResource(resourceName, libraryName);

    }
    
    
    
}
