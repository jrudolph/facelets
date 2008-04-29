/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sun.facelets.tag.renderkit;

import com.sun.facelets.tag.*;
import com.sun.facelets.tag.jsf.ComponentConfig;
import com.sun.facelets.tag.jsf.ComponentConfig;
import java.lang.reflect.Method;
import java.util.Set;
import javax.faces.FacesException;
import javax.faces.context.FacesContext;

/**
 *
 * @author edburns
 */
public class TemplateRendererTagLibrary extends AbstractTagLibrary {
    
    public TemplateRendererTagLibrary(String ns) {
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

        if (ns.equals(this.ns)) {
            FacesContext context = FacesContext.getCurrentInstance();
            String renderKitResourceId = getRenderKitResourceId(this.ns);
            if (null != renderKitResourceId) {
                String rendererResourceName = renderKitResourceId +
                        "/" + localName + ".xhtml";
                // PENDING: there has to be a cheaper way to test for existence
                java.io.InputStream rendererStream = 
                        context.getExternalContext().getResourceAsStream(rendererResourceName);
                result = null != rendererStream;
            }
        }
        return result;
    }

    public Method createFunction(String ns, String name) {
        return null;
    }

    public TagHandler createTagHandler(String ns, String localName, TagConfig tag) throws FacesException {
        TagHandler result = null;
        
        assert(containsTagHandler(ns, localName));
        int underscore = localName.indexOf("_");
        if (-1 != underscore && (underscore+1) < localName.length()) {
            String componentType = localName.substring(0, underscore);
            String rendererType = localName.substring(underscore+1);
            ComponentConfig componentConfig = new ComponentConfigWrapper(tag, 
                    componentType, rendererType);
            result = new TemplateRendererTagHandler(componentConfig);
        }
        return result;
    }
    
    private static final String NS_RENDER_KIT_PREFIX = "/render-kit/";
    
    public static boolean tagLibraryForNSExists(String toTest) {
        boolean result = false;
        
        String renderKitId = null;
        int renderKitIdIndex;
        if (-1 != (renderKitIdIndex = toTest.indexOf(NS_RENDER_KIT_PREFIX))) {
            renderKitIdIndex += NS_RENDER_KIT_PREFIX.length();
            result = null != getRenderKitResourceId(toTest);
        }
        
        return result;
    }
    
    private static String getRenderKitResourceId(String toTest) {
        String renderKitId = null;
        String renderKitResourceId = null;
        int renderKitIdIndex;
        if (-1 != (renderKitIdIndex = toTest.indexOf(NS_RENDER_KIT_PREFIX))) {
            renderKitIdIndex += NS_RENDER_KIT_PREFIX.length();
            if (renderKitIdIndex < toTest.length()) {
                renderKitId = toTest.substring(renderKitIdIndex);
                FacesContext context = FacesContext.getCurrentInstance();
                renderKitResourceId = "/resources" + NS_RENDER_KIT_PREFIX + 
                        renderKitId;
                Set<String> renderKitResources = context.getExternalContext().getResourcePaths(renderKitResourceId);
                if (null == renderKitResources || renderKitResources.isEmpty()) {
                    renderKitResourceId = null;
                }
            }
        }
        return renderKitResourceId;
    }

}
