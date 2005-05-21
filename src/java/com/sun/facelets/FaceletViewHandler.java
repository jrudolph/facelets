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

package com.sun.facelets;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.FacesException;
import javax.faces.FactoryFinder;
import javax.faces.application.StateManager;
import javax.faces.application.ViewHandler;
import javax.faces.application.ViewHandlerWrapper;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.RenderKit;
import javax.faces.render.RenderKitFactory;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.sun.facelets.compiler.Compiler;
import com.sun.facelets.compiler.SAXCompiler;
import com.sun.facelets.compiler.TagLibraryConfig;
import com.sun.facelets.spi.RefreshableFaceletFactory;
import com.sun.facelets.tag.TagLibrary;

/**
 * ViewHandler implementation for Facelets
 * 
 * @author Jacob Hookom
 * @version $Id: FaceletViewHandler.java,v 1.2 2005/05/21 19:48:06 jhook Exp $
 */
public class FaceletViewHandler extends ViewHandlerWrapper {

    protected final static Logger log = Logger.getLogger("facelets.viewHandler");

    public final static long DEFAULT_REFRESH_PERIOD = 2;

    public final static String REFRESH_PERIOD_PARAM_NAME = "facelet.REFRESH_PERIOD";

    public final static String LIBRARIES_PARAM_NAME = "facelet.LIBRARIES";

    protected final static String STATE_KEY = "com.sun.facelets.VIEW_STATE";

    private final ViewHandler parent;

    private boolean initialized = false;

    private String defaultSuffix;

    protected static void removeTransient(UIComponent c) {
        UIComponent d;
        for (Iterator itr = c.getChildren().iterator(); itr.hasNext();) {
            d = (UIComponent) itr.next();
            if (d.getFacets().size() > 0) {
                UIComponent e;
                for (Iterator jtr = d.getFacets().values().iterator(); jtr
                        .hasNext();) {
                    e = (UIComponent) jtr.next();
                    if (e.isTransient()) {
                        jtr.remove();
                    } else {
                        removeTransient(e);
                    }
                }
            }
            if (d.isTransient()) {
                itr.remove();
            } else {
                removeTransient(d);
            }
        }
    }

    /**
     * 
     */
    public FaceletViewHandler(ViewHandler parent) {
        this.parent = parent;
    }

    protected void initialize() {
        synchronized (this) {
            if (!this.initialized) {
                log.fine("Initializing");
                Compiler c = this.createCompiler();
                this.initializeCompiler(c);
                FaceletFactory f = this.createFaceletFactory(c);
                FaceletFactory.setInstance(f);
                this.initialized = true;
                log.fine("Initialization Successful");
            }
        }
    }

    protected FaceletFactory createFaceletFactory(Compiler c) {
        long refreshPeriod = DEFAULT_REFRESH_PERIOD;
        FacesContext ctx = FacesContext.getCurrentInstance();
        String userPeriod = ctx.getExternalContext().getInitParameter(
                REFRESH_PERIOD_PARAM_NAME);
        if (userPeriod != null && userPeriod.length() > 0) {
            refreshPeriod = Long.parseLong(userPeriod);
        }
        log.fine("Using Refresh Period: " + refreshPeriod + " sec");
        try {
            return new RefreshableFaceletFactory(c, ctx.getExternalContext()
                    .getResource("/"), refreshPeriod);
        } catch (MalformedURLException e) {
            throw new FaceletException("Error Creating FaceletFactory", e);
        }
    }

    protected Compiler createCompiler() {
        return new SAXCompiler();
    }

    protected void initializeCompiler(Compiler c) {
        FacesContext ctx = FacesContext.getCurrentInstance();
        ExternalContext ext = ctx.getExternalContext();
        String libParam = ext.getInitParameter(LIBRARIES_PARAM_NAME);
        if (libParam != null) {
            libParam = libParam.trim();
            String[] libs = libParam.split(";");
            URL src;
            TagLibrary libObj;
            for (int i = 0; i < libs.length; i++) {
                try {
                    src = ext.getResource(libs[i]);
                    if (src == null) {
                        throw new FileNotFoundException(libs[i]);
                    }
                    libObj = TagLibraryConfig.create(src);
                    c.addTagLibrary(libObj);
                    log.fine("Successfully Loaded Library: " + libs[i]);
                } catch (IOException e) {
                    log.log(Level.SEVERE, "Error Loading Library: " + libs[i], e);
                }
            }
        }
    }

    public UIViewRoot restoreView(FacesContext context, String viewId) {
        UIViewRoot root = null;
        try {
            root = super.restoreView(context, viewId);
        } catch (Exception e) {
            log.log(Level.WARNING, "Error Restoring View: " + viewId, e);
        }
        if (root != null) {
            log.fine("View Restored: " + root.getViewId());
        } else {
            log.fine("Unable to restore View");
        }
        return root;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.faces.application.ViewHandlerWrapper#getWrapped()
     */
    protected ViewHandler getWrapped() {
        return this.parent;
    }

    public void renderView(FacesContext context, UIViewRoot viewToRender)
            throws IOException, FacesException {
        if (!this.initialized) {
            this.initialize();
        }

        // exit if the view is not to be rendered
        if (!viewToRender.isRendered()) {
            return;
        }

        if (log.isLoggable(Level.FINE)) {
            log.fine("Rendering View: " + viewToRender.getViewId());
        }

        // setup our viewId
        String renderedViewId = this.getRenderedViewId(context, viewToRender
                .getViewId());
        viewToRender.setViewId(renderedViewId);

        // grab our FaceletFactory and create a Facelet
        FaceletFactory factory = FaceletFactory.getInstance();
        Facelet f = factory.getFacelet(viewToRender.getViewId());

        // populate UIViewRoot
        f.apply(context, viewToRender);

        // setup writer
        ExternalContext extContext = context.getExternalContext();
        ResponseWriter writer = context.getResponseWriter();
        if (writer == null) {
            RenderKitFactory renderFactory = (RenderKitFactory) FactoryFinder
                    .getFactory(FactoryFinder.RENDER_KIT_FACTORY);
            RenderKit renderKit = renderFactory.getRenderKit(context,
                    viewToRender.getRenderKitId());
            ServletRequest request = (ServletRequest) extContext.getRequest();
            ServletResponse response = (ServletResponse) extContext
                    .getResponse();
            response.setBufferSize(8192);
            writer = renderKit.createResponseWriter(response.getWriter(),
                    "text/html", request.getCharacterEncoding());
            context.setResponseWriter(writer);
        }

        // save the state
        StateManager stateMgr = context.getApplication().getStateManager();
        Object state = stateMgr.saveView(context);
        extContext.getRequestMap().put(STATE_KEY, state);

        // write the state
        writer.startDocument();
        viewToRender.encodeAll(context);
        writer.endDocument();

        // finally clean up transients if viewState = true
        if (extContext.getRequestMap().containsKey(STATE_KEY)) {
            removeTransient(viewToRender);
        }
    }

    public String getDefaultSuffix(FacesContext context) throws FacesException {
        if (this.defaultSuffix == null) {
            ExternalContext extCtx = context.getExternalContext();
            String viewSuffix = extCtx
                    .getInitParameter(ViewHandler.DEFAULT_SUFFIX_PARAM_NAME);
            this.defaultSuffix = (viewSuffix != null) ? viewSuffix
                    : ViewHandler.DEFAULT_SUFFIX;
        }
        return this.defaultSuffix;
    }

    protected String getRenderedViewId(FacesContext context, String actionId) {
        ExternalContext extCtx = context.getExternalContext();
        String viewId = extCtx.getRequestPathInfo();
        if (extCtx.getRequestPathInfo() == null) {
            String facesSuffix = actionId.substring(actionId.lastIndexOf('.'));
            String viewSuffix = this.getDefaultSuffix(context);
            viewId = actionId.replaceFirst(facesSuffix, viewSuffix);
        }
        if (log.isLoggable(Level.FINE)) {
            log.fine("ActionId -> ViewId: " + actionId + " -> " + viewId);
        }
        return viewId;
    }

    public void writeState(FacesContext context) throws IOException {
        StateManager stateMgr = context.getApplication().getStateManager();
        if (stateMgr.isSavingStateInClient(context)) {
            Object state = context.getExternalContext().getRequestMap().get(
                    STATE_KEY);
            if (state != null) {
                stateMgr.writeState(context, state);
            }
        }
    }
}
