/**
 * Licensed under the Common Development and Distribution License,
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.sun.com/cddl/
 *   
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or 
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package com.sun.facelets;

import java.io.IOException;
import java.net.URL;

import javax.el.ELContext;
import javax.el.ELException;
import javax.el.ExpressionFactory;
import javax.el.FunctionMapper;
import javax.el.VariableMapper;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

/**
 * Context representative of a single request from a Facelet
 * 
 * @author Jacob Hookom
 * @version $Id: FaceletContext.java,v 1.6.20.1 2008/07/01 16:51:09 edburns Exp $
 */
public abstract class FaceletContext extends ELContext {
    
    // The key in the FacesContext attribute map
    // for the FaceletContext instance.
    public static final String FACLET_CONTEXT_KEY = 
            "com.sun.facelets.FACELET_CONTEXT";

    /**
     * The current FacesContext bound to this "request"
     * 
     * @return cannot be null
     */
    public abstract FacesContext getFacesContext();

    /**
     * Generate a unique ID for the passed String
     * 
     * @param base
     * @return a unique ID given the passed base
     */
    public abstract String generateUniqueId(String base);

    /**
     * The ExpressionFactory to use within the Facelet this context is executing
     * upon.
     * 
     * @return cannot be null
     */
    public abstract ExpressionFactory getExpressionFactory();

    /**
     * Set the VariableMapper to use in EL evaluation/creation
     * 
     * @param varMapper
     */
    public abstract void setVariableMapper(VariableMapper varMapper);

    /**
     * Set the FunctionMapper to use in EL evaluation/creation
     * 
     * @param fnMapper
     */
    public abstract void setFunctionMapper(FunctionMapper fnMapper);

    /**
     * Support method which is backed by the current VariableMapper
     * 
     * @param name
     * @param value
     */
    public abstract void setAttribute(String name, Object value);

    /**
     * Support method which is backed by the current VariableMapper
     * 
     * @param name
     * @return an Object specified for that name
     */
    public abstract Object getAttribute(String name);

    /**
     * Include another Facelet defined at some path, relative to the executing
     * context, not the current Facelet (same as include directive in JSP)
     * 
     * @param parent
     * @param relativePath
     * @throws IOException
     * @throws FaceletException
     * @throws FacesException
     * @throws ELException
     */
    public abstract void includeFacelet(UIComponent parent, String relativePath)
            throws IOException, FaceletException, FacesException, ELException;

    /**
     * Include another Facelet defined at some path, absolute to this
     * ClassLoader/OS
     * 
     * @param parent
     * @param absolutePath
     * @throws IOException
     * @throws FaceletException
     * @throws FacesException
     * @throws ELException
     */
    public abstract void includeFacelet(UIComponent parent, URL absolutePath)
            throws IOException, FaceletException, FacesException, ELException;
    
    /**
     * Push the passed TemplateClient onto the stack for Definition Resolution
     * @param client
     * @see TemplateClient
     */
    public abstract void pushClient(TemplateClient client);
    
    /**
     * Pop the last added TemplateClient
     * @see TemplateClient
     */
    public abstract void popClient(TemplateClient client);
    
    
    public abstract void extendClient(TemplateClient client);
    
    /**
     * This method will walk through the TemplateClient stack to resolve and
     * apply the definition for the passed name.
     * If it's been resolved and applied, this method will return true.
     * 
     * @param parent the UIComponent to apply to
     * @param name name or null of the definition you want to apply
     * @return true if successfully applied, otherwise false
     * @throws IOException
     * @throws FaceletException
     * @throws FacesException
     * @throws ELException
     */
    public abstract boolean includeDefinition(UIComponent parent, String name) throws IOException, FaceletException, FacesException, ELException ;
}
