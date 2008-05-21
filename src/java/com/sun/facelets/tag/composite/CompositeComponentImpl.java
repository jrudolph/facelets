/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sun.facelets.tag.composite;

import javax.faces.application.Resource;
import javax.faces.component.UINamingContainer;
import javax.faces.context.FacesContext;

/**
 *
 * @author edburns
 */
public class CompositeComponentImpl extends UINamingContainer {
    
    public static final String TYPE = "javax.faces.Composite";

    @Override
    public String getFamily() {
        return TYPE;
    }
    
    // ----------------------------------------------------- StateHolder Methods


    private Object[] values;

    @Override
    public Object saveState(FacesContext context) {

        if (values == null) {
            values = new Object[3];
        }
        Resource compositeComponentResource = 
                this.getResource();
        this.clearResource();

        values[0] = super.saveState(context);
        values[1] = compositeComponentResource.getResourceName();
        values[2] = compositeComponentResource.getLibraryName();
        return (values);

    }
    
    public Resource getResource() {
        Resource result = (Resource)
                this.getAttributes().get(Resource.COMPONENT_RESOURCE_KEY);
        return result;
    }
    
    public void setResource(Resource resource) {
        this.getAttributes().put(Resource.COMPONENT_RESOURCE_KEY,
                resource);
    }
    
    private void clearResource() {
        this.getAttributes().remove(Resource.COMPONENT_RESOURCE_KEY);
    }
    

    @Override
    public void restoreState(FacesContext context, Object state) {

        values = (Object[]) state;
        super.restoreState(context, values[0]);
        String
                resourceName = (String) values[1],
                libraryName = (String) values[2];
        Resource resource = context.getApplication().getResourceHandler().
                createResource(resourceName, libraryName);
        this.setResource(resource);
    }
    
    
    
}
