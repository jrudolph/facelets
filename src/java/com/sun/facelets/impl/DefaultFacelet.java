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

package com.sun.facelets.impl;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.WeakHashMap;

import javax.el.ELException;
import javax.el.ExpressionFactory;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import com.sun.facelets.Facelet;
import com.sun.facelets.FaceletContext;
import com.sun.facelets.FaceletException;
import com.sun.facelets.FaceletHandler;
import com.sun.facelets.tag.jsf.ComponentSupport;

/**
 * Default Facelet implementation.
 * 
 * @author Jacob Hookom
 * @version $Id: DefaultFacelet.java,v 1.2 2005/08/24 04:38:58 jhook Exp $
 */
final class DefaultFacelet extends Facelet {

    private final String alias;

    private final ExpressionFactory elFactory;

    private final DefaultFaceletFactory factory;

    private final long createTime;

    private final Map relativePaths;

    private final FaceletHandler root;

    private final URL src;

    public DefaultFacelet(DefaultFaceletFactory factory, ExpressionFactory el,
            URL src, String alias, FaceletHandler root) {
        this.factory = factory;
        this.elFactory = el;
        this.src = src;
        this.root = root;
        this.alias = alias;
        this.createTime = System.currentTimeMillis();
        this.relativePaths = new WeakHashMap();
    }

    /**
     * @see com.sun.facelets.Facelet#apply(javax.faces.context.FacesContext,
     *      javax.faces.component.UIComponent)
     */
    public void apply(FacesContext facesContext, UIComponent parent)
            throws IOException, FacesException, FaceletException, ELException {
        DefaultFaceletContext ctx = new DefaultFaceletContext(facesContext,
                this);
        ComponentSupport.markForDeletion(parent);
        this.root.apply(ctx, parent);
        ComponentSupport.finalizeForDeletion(parent);
    }

    /**
     * Return the alias name for error messages and logging
     * 
     * @return alias name
     */
    public String getAlias() {
        return this.alias;
    }

    /**
     * Return this Facelet's ExpressionFactory instance
     * 
     * @return internal ExpressionFactory instance
     */
    public ExpressionFactory getExpressionFactory() {
        return this.elFactory;
    }

    /**
     * The time when this Facelet was created, NOT the URL source code
     * 
     * @return final timestamp of when this Facelet was created
     */
    public long getCreateTime() {
        return this.createTime;
    }

    /**
     * Delegates resolution to DefaultFaceletFactory reference. Also, caches
     * URLs for relative paths.
     * 
     * @param path
     *            a relative url path
     * @return URL pointing to destination
     * @throws IOException
     *             if there is a problem creating the URL for the path specified
     */
    private URL getRelativePath(String path) throws IOException {
        URL url = (URL) this.relativePaths.get(path);
        if (url == null) {
            url = this.factory.resolveURL(this.src, path);
            this.relativePaths.put(path, url);
        }
        return url;
    }

    /**
     * The URL this Facelet was created from.
     * 
     * @return the URL this Facelet was created from
     */
    public URL getSource() {
        return this.src;
    }

    /**
     * Given the passed FaceletContext, apply our child FaceletHandlers to the
     * passed parent
     * 
     * @see FaceletHandler#apply(FaceletContext, UIComponent)
     * @param ctx
     *            the FaceletContext to use for applying our FaceletHandlers
     * @param parent
     *            the parent component to apply changes to
     * @throws IOException
     * @throws FacesException
     * @throws FaceletException
     * @throws ELException
     */
    private void include(FaceletContext ctx, UIComponent parent)
            throws IOException, FacesException, FaceletException, ELException {
        // should we reset the context?
        // FaceletContext nCtx = new
        // DefaultFaceletContext(ctx.getFacesContext(), this);
        // nCtx.setFunctionMapper(ctx.getFunctionMapper());
        // nCtx.setVariableMapper(ctx.getVariableMapper());
        this.root.apply(ctx, parent);
    }

    /**
     * Used for delegation by the DefaultFaceletContext. First pulls the URL
     * from {@link #getRelativePath(String) getRelativePath(String)}, then
     * calls
     * {@link #include(FaceletContext, UIComponent, URL) include(FaceletContext, UIComponent, URL)}.
     * 
     * @see FaceletContext#includeFacelet(UIComponent, String)
     * @param ctx
     *            FaceletContext to pass to the included Facelet
     * @param parent
     *            UIComponent to apply changes to
     * @param path
     *            relative path to the desired Facelet from the FaceletContext
     * @throws IOException
     * @throws FacesException
     * @throws FaceletException
     * @throws ELException
     */
    public void include(FaceletContext ctx, UIComponent parent, String path)
            throws IOException, FacesException, FaceletException, ELException {
        URL url = this.getRelativePath(path);
        this.include(ctx, parent, url);
    }

    /**
     * Grabs a DefaultFacelet from referenced DefaultFaceletFacotry
     * 
     * @see DefaultFaceletFactory#getFacelet(URL)
     * @param ctx
     *            FaceletContext to pass to the included Facelet
     * @param parent
     *            UIComponent to apply changes to
     * @param url
     *            URL source to include Facelet from
     * @throws IOException
     * @throws FacesException
     * @throws FaceletException
     * @throws ELException
     */
    public void include(FaceletContext ctx, UIComponent parent, URL url)
            throws IOException, FacesException, FaceletException, ELException {
        DefaultFacelet f = (DefaultFacelet) this.factory.getFacelet(url);
        f.include(ctx, parent);
    }

}
