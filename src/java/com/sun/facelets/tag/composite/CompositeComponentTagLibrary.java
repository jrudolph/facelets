/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sun.facelets.tag.composite;

import com.sun.facelets.tag.*;
import com.sun.facelets.tag.jsf.ComponentConfig;
import com.sun.faces.scripting.GroovyHelper;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.FacesException;
import javax.faces.application.Resource;
import javax.faces.application.ResourceHandler;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;


/**
 *
 * @author edburns
 */
public class CompositeComponentTagLibrary extends AbstractTagLibrary {
    
    public CompositeComponentTagLibrary(String ns) {
        super(ns);
        if (null == ns) {
            throw new NullPointerException();
        }
        this.ns = ns;
    }
    
    private String ns = null;

    public boolean containsFunction(String ns, String name) {
        return false;
    }

    public boolean containsNamespace(String ns) {
        return (null != ns && ns.equals(this.ns));
    }

    public boolean containsTagHandler(String ns, String localName) {
        boolean result = false;

        Resource compositeComponentResource = null;
                        
        if (null != (compositeComponentResource = 
                getCompositeComponentResource(ns, localName))) {
            InputStream componentStream = null;
            try {
                componentStream = compositeComponentResource.getInputStream();
            } catch (IOException ex) {
                Logger.getLogger(CompositeComponentTagLibrary.class.getName()).log(Level.SEVERE, null, ex);
            }
            result = null != componentStream;
        }
        return result;
    }
    
    private Resource getCompositeComponentResource(String ns, String localName) {
        Resource compositeComponentResource = null;
        if (ns.equals(this.ns)) {
            FacesContext context = FacesContext.getCurrentInstance();
            String libraryName = getCompositeComponentLibraryName(this.ns);
            if (null != libraryName) {
                String compositeComponentName = localName + ".xhtml";
                // PENDING: there has to be a cheaper way to test for existence
                ResourceHandler resourceHandler = context.getApplication().getResourceHandler();
                compositeComponentResource = resourceHandler.
                        createResource(compositeComponentName, libraryName);
            }
        }
        return compositeComponentResource;
    }

    public Method createFunction(String ns, String name) {
        return null;
    }

    public TagHandler createTagHandler(String ns, String localName, TagConfig tag) throws FacesException {
        TagHandler result = null;
        
        ComponentConfig componentConfig = new ComponentConfigWrapper(tag,
                CompositeComponent.TYPE, null);
        result = new CompositeComponentTagHandler(
                getCompositeComponentResource(ns, localName),
                componentConfig);

        return result;
    }
    
    private static final String NS_COMPOSITE_COMPONENT_PREFIX = 
            "http://java.sun.com/jsf/composite/";
    
    public static boolean tagLibraryForNSExists(String toTest) {
        boolean result = false;
        
        String resourceId = null;
        if (null != (resourceId = getCompositeComponentLibraryName(toTest))) {
            result = FacesContext.getCurrentInstance().getApplication().
                    getResourceHandler().libraryExists(resourceId);
        }
        
        return result;
    }
    
    public static boolean scriptComponentForResourceExists(FacesContext context,
            Resource componentResource) {
        boolean result = false;

        Resource scriptComponentResource = getScriptComponentResource(context, 
                componentResource);
        try {
            result = (null != scriptComponentResource) && (null != scriptComponentResource.getInputStream());
        } catch (IOException ex) {
            Logger.getLogger(CompositeComponentTagLibrary.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return result;
    }
    
    static Resource getScriptComponentResource(FacesContext context,
            Resource componentResource) {
        Resource result = null;

        String resourceName = componentResource.getResourceName();
        if (resourceName.endsWith(".xhtml")) {
            resourceName = resourceName.substring(0, 
                    resourceName.length() - 6) + ".groovy";
            ResourceHandler resourceHandler = context.getApplication().getResourceHandler();
            result = resourceHandler.createResource(resourceName, 
                    componentResource.getLibraryName());
        }
        
        return result;
    }
    
    private static String getCompositeComponentLibraryName(String toTest) {
        String resourceId = null;
        int resourceIdIndex;
        if (-1 != (resourceIdIndex = toTest.indexOf(NS_COMPOSITE_COMPONENT_PREFIX))) {
            resourceIdIndex += NS_COMPOSITE_COMPONENT_PREFIX.length();
            if (resourceIdIndex < toTest.length()) {
                resourceId = toTest.substring(resourceIdIndex);
            }
        }
        
        return resourceId;
    }

}
