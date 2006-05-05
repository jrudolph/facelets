package com.sun.facelets.webapp;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.EnumerationIterator;
import org.apache.commons.collections.IteratorEnumeration;

import com.sun.facelets.component.UIFacelet;
import com.sun.facelets.resource.ClasspathResource;
import com.sun.facelets.resource.Resource;

public class FaceletFilter implements Filter {

    private final Logger log = Logger.getLogger("facelets.filter");

    private WebAppConfig faceletConfig;

    private ServletContext servletContext;

    private String resourceDir;

    private final Map<String, Resource> resourceCache = new ConcurrentHashMap<String, Resource>();

    public FaceletFilter() {
        super();

    }

    public void init(FilterConfig cfg) throws ServletException {
        this.servletContext = cfg.getServletContext();
        this.faceletConfig = new WebAppConfig(this.servletContext);
        WebAppConfig.setInstance(this.servletContext, this.faceletConfig);

        // TODO make this better
        // figure out the resource path
        this.resourceDir = this.faceletConfig.getResourcePath();
        if (this.resourceDir.charAt(0) != '/') {
            throw new ServletException("Resource Path must start with a '/'");
        }

        if (this.resourceDir.endsWith("/")) {
            this.resourceDir = this.resourceDir.substring(0, this.resourceDir
                    .length() - 1);
        }
    }

    public void doFilter(ServletRequest req, ServletResponse resp,
            FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpReq = (HttpServletRequest) req;
        HttpServletResponse httpResp = (HttpServletResponse) resp;
        
        // get actual path
        String uri = httpReq.getRequestURI();
        String ctx = httpReq.getContextPath();
        int len = ctx.length();
        String realPath = uri.substring(len);

        // check if it's a resource
        if (realPath.startsWith(this.resourceDir)) {

            // should first let this pass through, if not found, return ours
            String resource = realPath.substring(this.resourceDir.length());

            // our var
            Resource rsc = this.resourceCache.get(resource);

            // if not cached or in development mode
            if (rsc == null || this.faceletConfig.isDevelopmentMode()) {

                // try to see if the taglibs have it
                rsc = this.faceletConfig.createTagLibrary().createResource(
                        resource);

                // check our default path of "FACES-INF"
                if (rsc == null) {
                    ClassLoader cl = Thread.currentThread()
                            .getContextClassLoader();
                    URL url = cl.getResource(realPath.substring(1));
                    if (url != null) {
                        rsc = new ClasspathResource(url, this.servletContext
                                .getMimeType(resource));
                    }
                }

                // cache it if we found it
                if (rsc != null) {
                    this.resourceCache.put(resource, rsc);
                }
            }

            // now respond
            if (rsc != null) {

                // stop now if not modified
                long ifModifiedSince = httpReq
                        .getDateHeader("If-Modified-Since");
                long lastModified = rsc.getLastModified();
                if (ifModifiedSince != -1 && lastModified >= ifModifiedSince) {
                    httpResp.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
                    return;
                }

                // set the mime-type
                String mimetype = rsc.getMimeType();
                if (mimetype == null) {
                    mimetype = this.servletContext.getMimeType(resource);
                }
                httpResp.setContentType(mimetype);

                // write to response
                OutputStream os = httpResp.getOutputStream();
                BufferedOutputStream bos = new BufferedOutputStream(os, 512);
                try {
                    rsc.write(bos);
                } finally {
                    bos.close();
                    if (os != null) {
                        os.close();
                    }
                }
                return;
            } else {
                if (log.isLoggable(Level.WARNING)) {
                    log.warning("Couldn't load Resource: " + resource);
                }
            }
        }
        
        // if fall through to pass on
        // check if this is an Async request
        if (httpReq.getHeader(UIFacelet.PARAM_ASYNC) != null) {
            
            // need to buffer so headers can be written before body is committed
            ByteArrayOutputStream baos = new ByteArrayOutputStream(128);
            AsyncResponseWrapper w = new AsyncResponseWrapper(httpResp, baos);
            chain.doFilter(httpReq, w);
            w.closeBuffer();
            
            // write out response
            httpResp.getOutputStream().write(baos.toByteArray());
            
        } else {
            
            // don't bother with anything
            chain.doFilter(req, resp);
        }
    }

    public void destroy() {
        // TODO Auto-generated method stub

    }

}
