/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sun.facelets.impl;

import com.sun.facelets.Facelet;
import com.sun.facelets.FaceletContext;
import com.sun.facelets.FaceletException;
import com.sun.facelets.FaceletFactory;
import com.sun.facelets.FaceletViewHandler;
import com.sun.facelets.el.VariableMapperWrapper;
import com.sun.facelets.tag.composite.CompositeComponentBeanInfo;
import com.sun.facelets.tag.jsf.CompositeComponentTagHandler;
import java.beans.BeanInfo;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.el.ELException;
import javax.el.ValueExpression;
import javax.el.VariableMapper;
import javax.faces.FacesException;
import javax.faces.application.Resource;
import javax.faces.application.ResourceHandler;
import javax.faces.component.UIComponent;
import javax.faces.component.UIPanel;
import javax.faces.context.FacesContext;
import javax.faces.webapp.pdl.PageDeclarationLanguage;

/**
 *
 * @author edburns
 */
public class PageDeclarationLanguageImpl extends PageDeclarationLanguage {
    
    
    

    @Override
    public BeanInfo getComponentMetadata(FacesContext context, 
            Resource compositeComponentResource) {
        // PENDING this implementation is terribly wasteful.
        // Must find a better way.
        CompositeComponentBeanInfo result = null;
        FaceletContext ctx = (FaceletContext)
                context.getAttributes().get(FaceletContext.FACELET_CONTEXT_KEY);
        FaceletViewHandler faceletViewHandler = (FaceletViewHandler) context.getApplication().getViewHandler();
        FaceletFactory factory = faceletViewHandler.getFaceletFactory();
        VariableMapper orig = ctx.getVariableMapper();
        UIComponent tmp = context.getApplication().createComponent("javax.faces.NamingContainer");
        UIPanel facetComponent = (UIPanel)
                context.getApplication().createComponent("javax.faces.Panel");
        facetComponent.setRendererType("javax.faces.Group");
        tmp.getFacets().put(UIComponent.COMPOSITE_FACET_NAME, facetComponent);
        tmp.getAttributes().put(Resource.COMPONENT_RESOURCE_KEY, 
                compositeComponentResource);
        
        Facelet f;

        try {
            f = factory.getFacelet(compositeComponentResource.getURL());
            VariableMapper wrapper = new VariableMapperWrapper(orig) {

                @Override
                public ValueExpression resolveVariable(String variable) {
                    return super.resolveVariable(variable);
                }
                
            };
            ctx.setVariableMapper(wrapper);
            f.apply(context, facetComponent);
        } catch (IOException ex) {
            Logger.getLogger(CompositeComponentTagHandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FaceletException ex) {
            Logger.getLogger(CompositeComponentTagHandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FacesException ex) {
            Logger.getLogger(CompositeComponentTagHandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ELException ex) {
            Logger.getLogger(CompositeComponentTagHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally {
            ctx.setVariableMapper(orig);
        }
        result = (CompositeComponentBeanInfo) 
                tmp.getAttributes().get(UIComponent.BEANINFO_KEY);
        
        return result;
    }

    public Resource getScriptComponentResource(FacesContext context,
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
    
    
}
