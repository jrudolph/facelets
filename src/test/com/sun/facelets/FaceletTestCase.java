package com.sun.facelets;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;

import javax.faces.FactoryFinder;
import javax.faces.application.Application;
import javax.faces.application.ApplicationFactory;
import javax.faces.application.StateManager;
import javax.faces.application.ViewHandler;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextFactory;
import javax.faces.mock.MockExternalContext;
import javax.faces.mock.MockHttpServletRequest;
import javax.faces.mock.MockHttpServletResponse;
import javax.faces.mock.MockHttpSession;
import javax.faces.mock.MockJspFactory;
import javax.faces.mock.MockLifecycle;
import javax.faces.mock.MockResponseWriter;
import javax.faces.mock.MockServletConfig;
import javax.faces.mock.MockServletContext;
import javax.faces.render.RenderKitFactory;
import javax.servlet.ServletContextEvent;
import javax.servlet.jsp.JspFactory;

import com.sun.facelets.FaceletFactory;
import com.sun.facelets.FaceletViewHandler;
import com.sun.facelets.compiler.Compiler;
import com.sun.facelets.compiler.SAXCompiler;
import com.sun.faces.config.ConfigureListener;
import com.sun.faces.context.FacesContextFactoryImpl;

import junit.framework.TestCase;

public abstract class FaceletTestCase extends TestCase {

    protected final String filePath = this.getDirectory();

    // Mock object instances for our tests
    protected Application application = null;

    protected MockServletConfig config = null;

    protected MockExternalContext externalContext = null;

    protected FacesContext facesContext = null;

    protected MockLifecycle lifecycle = null;

    protected MockHttpServletRequest request = null;

    protected MockHttpServletResponse response = null;

    protected MockServletContext servletContext = null;

    protected MockHttpSession session = null;

    protected Compiler compiler = null;

    protected StringWriter writer = null;

    public FaceletTestCase() {
        super();
    }

    public FaceletTestCase(String arg0) {
        super(arg0);
    }

    protected URL getLocalFile(String name) throws Exception {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        URL url = cl.getResource(this.filePath + "/" + name);
        if (url == null) {
            throw new FileNotFoundException(cl.getResource("").getFile() + name
                    + " was not found");
        }
        return url;
    }

    protected String getDirectory() {
        return this.getClass().getName().substring(0,
                this.getClass().getName().lastIndexOf('.')).replace('.', '/');
    }

    protected void write(UIComponent c) throws IOException {
        this.writer = new StringWriter(2560);
        this.facesContext
                .setResponseWriter(new MockResponseWriter(this.writer));
        c.encodeAll(this.facesContext);
    }

    protected void setUp() throws Exception {
        super.setUp();

        JspFactory.setDefaultFactory(new MockJspFactory());

        // setup UISettings
        this.compiler = new SAXCompiler();
        //this.compiler.addTagDecorator(HtmlDecorator.Instance);
        this.compiler.setValidating(false);
        this.compiler.setTrimmingWhitespace(false);
        this.compiler.setTrimmingComments(false);

        // Set up Servlet API Objects
        this.servletContext = new MockServletContext();
        this.servletContext.addInitParameter(
                StateManager.STATE_SAVING_METHOD_PARAM_NAME, "client");
        this.servletContext.addInitParameter(
                ViewHandler.DEFAULT_SUFFIX_PARAM_NAME, ".xhtml");
        this.config = new MockServletConfig(this.servletContext);
        this.session = new MockHttpSession();
        this.request = new MockHttpServletRequest(this.session);
        this.response = new MockHttpServletResponse();

        ConfigureListener listener = new ConfigureListener();
        listener
                .contextInitialized(new ServletContextEvent(this.servletContext));

        ApplicationFactory appFactory = (ApplicationFactory) FactoryFinder
                .getFactory(FactoryFinder.APPLICATION_FACTORY);
        this.application = appFactory.getApplication();
        this.application
                .setDefaultRenderKitId(RenderKitFactory.HTML_BASIC_RENDER_KIT);
        this.application.setViewHandler(new FaceletViewHandler(this.application
                .getViewHandler()));

        FacesContextFactory facesFactory = new FacesContextFactoryImpl();
        this.facesContext = facesFactory.getFacesContext(this.servletContext,
                this.request, this.response, new MockLifecycle());

        this.writer = new StringWriter(512);
        this.facesContext
                .setResponseWriter(new MockResponseWriter(this.writer));
        
        FaceletFactory.setInstance(new TestFaceletFactory(this.getClass(), this.compiler));
        
        UIViewRoot root = new UIViewRoot();
        this.facesContext.setViewRoot(root);
        root.setRenderKitId(RenderKitFactory.HTML_BASIC_RENDER_KIT);
        root.setViewId("/postback.xhtml");
    }

    protected void tearDown() throws Exception {
        super.tearDown();

        this.compiler = null;
        this.application = null;
        this.config = null;
        this.externalContext = null;
        this.facesContext = null;
        this.lifecycle = null;
        this.request = null;
        this.response = null;
        this.servletContext = null;
        this.session = null;
    }

}
