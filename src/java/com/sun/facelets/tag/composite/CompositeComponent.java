/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sun.facelets.tag.composite;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.Resource;
import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;

/**
 *
 * @author edburns
 */
public class CompositeComponent extends UIComponentBase {
    
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

    @Override
    public void decode(FacesContext context) {
        boolean didDelegate = false;
        if (CompositeComponentTagLibrary.scriptComponentForResourceExists(context, 
                getResource())) {
            if (CompositeComponentTagLibrary.
                    scriptComponentForResourceExists(context, getResource())) {
                UIComponent delegate = CompositeComponentTagLibrary.
                        getScriptComponent(context, getResource());
                if (null != delegate) {
                    Class [] args = {FacesContext.class};
                    try {
                        if (null != delegate.getClass().getDeclaredMethod("decode", args)) {
                            delegate.decode(context);
                            didDelegate = true;
                        }
                    } catch (NoSuchMethodException ex) {
                        Logger.getLogger(CompositeComponent.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (SecurityException ex) {
                        Logger.getLogger(CompositeComponent.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
        if (!didDelegate) {
            super.decode(context);
        }
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
