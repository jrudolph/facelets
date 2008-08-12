/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 * 
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License. You can obtain
 * a copy of the License at https://glassfish.dev.java.net/public/CDDL+GPL.html
 * or glassfish/bootstrap/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 * 
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at glassfish/bootstrap/legal/LICENSE.txt.
 * Sun designates this particular file as subject to the "Classpath" exception
 * as provided by Sun in the GPL Version 2 section of the License file that
 * accompanied this code.  If applicable, add the following below the License
 * Header, with the fields enclosed by brackets [] replaced by your own
 * identifying information: "Portions Copyrighted [year]
 * [name of copyright owner]"
 * 
 * Contributor(s):
 * 
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
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
