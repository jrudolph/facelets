/**
 * Copyright 2005 Sun Microsystems, Inc. All rights reserved.
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

package com.sun.facelets.spi;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.el.ELException;
import javax.faces.FacesException;
import javax.faces.context.FacesContext;

import com.sun.facelets.Facelet;
import com.sun.facelets.FaceletException;
import com.sun.facelets.FaceletFactory;
import com.sun.facelets.FaceletHandler;
import com.sun.facelets.compiler.Compiler;
import com.sun.facelets.util.Assert;

/**
 * Default FaceletFactory implementation.
 * 
 * @author Jacob Hookom
 * @version $Id: DefaultFaceletFactory.java,v 1.2 2005/06/20 01:42:24 jhook Exp $
 */
public class DefaultFaceletFactory extends FaceletFactory {

    protected final static Logger log = Logger.getLogger("facelets.factory");

    protected final Compiler compiler;

    protected final Map facelets;

    protected final Map relativeLocations;

    protected final URL location;

    public DefaultFaceletFactory(Compiler compiler, URL url) {
        Assert.param("compiler", compiler);
        Assert.param("url", url);
        this.compiler = compiler;
        this.facelets = new HashMap();
        this.relativeLocations = new HashMap();
        this.location = url;
        log.fine("Using Base Location: " + url);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sun.facelets.FaceletFactory#getFacelet(java.lang.String)
     */
    public Facelet getFacelet(String uri) throws IOException, FaceletException,
            FacesException, ELException {
        URL url = (URL) this.relativeLocations.get(uri);
        if (url == null) {
            url = this.resolveURL(this.location, uri);
            if (url != null) {
            this.relativeLocations.put(uri, url);
            } else {
                throw new IOException("'"+uri+"' not found.");
            }
        }
        return this.getFacelet(url);
    }

    /**
     * Resolves a path based on the passed URL. If the path starts with '/',
     * then resolve the path against
     * {@link javax.faces.context.ExternalContext#getResource(java.lang.String) javax.faces.context.ExternalContext#getResource(java.lang.String)}.
     * Otherwise create a new URL via
     * {@link URL#URL(java.net.URL, java.lang.String) URL(URL, String)}.
     * 
     * @param source
     *            base to resolve from
     * @param path
     *            relative path to the source
     * @return resolved URL
     * @throws IOException
     */
    public URL resolveURL(URL source, String path) throws IOException {
        if (path.startsWith("/")) {
            return FacesContext.getCurrentInstance().getExternalContext()
                    .getResource(path);
        } else {
            return new URL(source, path);
        }
    }

    /**
     * Create a Facelet from the passed URL. This method checks if the cached
     * Facelet needs to be refreshed before returning. If so, uses the passed
     * URL to build a new instance;
     * 
     * @param url
     *            source url
     * @return Facelet instance
     * @throws IOException
     * @throws FaceletException
     * @throws FacesException
     * @throws ELException
     */
    public Facelet getFacelet(URL url) throws IOException, FaceletException,
            FacesException, ELException {
        Assert.param("url", url);
        DefaultFacelet f = (DefaultFacelet) this.facelets.get(url);
        if (f == null || this.needsToBeRefreshed(f)) {
            f = this.createFacelet(url);
            this.facelets.put(url, f);
        }
        return f;
    }

    /**
     * Template method for determining if the Facelet needs to be refreshed.
     * 
     * @param facelet
     *            Facelet that could have expired
     * @return true if it needs to be refreshed
     */
    protected boolean needsToBeRefreshed(DefaultFacelet facelet) {
        return false;
    }

    /**
     * Uses the internal Compiler reference to build a Facelet given the passed
     * URL.
     * 
     * @param url
     *            source
     * @return a Facelet instance
     * @throws IOException
     * @throws FaceletException
     * @throws FacesException
     * @throws ELException
     */
    protected DefaultFacelet createFacelet(URL url) throws IOException,
            FaceletException, FacesException, ELException {
        if (log.isLoggable(Level.FINE)) {
            log.fine("Creating Facelet for: " + url);
        }
        String alias = url.getFile();
        FaceletHandler h = this.compiler.compile(url, alias);
        DefaultFacelet f = new DefaultFacelet(this, this.compiler
                .createExpressionFactory(), url, alias, h);
        return f;
    }

    /**
     * Compiler this factory uses
     * 
     * @return final Compiler instance
     */
    public Compiler getCompiler() {
        return this.compiler;
    }
}
