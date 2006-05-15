package com.sun.facelets.webapp;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
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

import com.sun.facelets.component.AsyncResponse;
import com.sun.facelets.component.UIFacelet;
import com.sun.facelets.resource.ClasspathResource;
import com.sun.facelets.resource.Resource;
import com.sun.facelets.util.FastWriter;

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

    public void doResource(FilterChain chain, HttpServletRequest httpReq,
            HttpServletResponse httpResp, String path) throws IOException,
            ServletException {
        // should first let this pass through, if not found, return ours
        String resource = path.substring(this.resourceDir.length());

        // our var
        Resource rsc = this.resourceCache.get(resource);

        // if not cached or in development mode
        if (rsc == null || this.faceletConfig.isDevelopmentMode()) {

            // try to see if the taglibs have it
            rsc = this.faceletConfig.createTagLibrary()
                    .createResource(resource);

            // check our default path of "FACES-INF"
            if (rsc == null) {
                ClassLoader cl = Thread.currentThread().getContextClassLoader();
                URL url = cl.getResource(path.substring(1));
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
            long ifModifiedSince = httpReq.getDateHeader("If-Modified-Since");
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
        } else {
            chain.doFilter(httpReq, httpResp);
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
            this.doResource(chain, httpReq, httpResp, realPath);
        } else if (httpReq.getHeader(UIFacelet.PARAM_ASYNC) != null) {
            this.doAsync(chain, httpReq, httpResp);
        } else {
            chain.doFilter(req, resp);
        }
    }

    public void doAsync(FilterChain chain, HttpServletRequest httpReq,
            HttpServletResponse httpResp) throws IOException, ServletException {
        // correct content type for mozilla, can override later
        httpResp.setContentType("text/xml");
        httpResp.setHeader("Cache-Control", "no-cache");

        // need to buffer so headers can be written before body is committed
        try {
            AsyncResponse async = AsyncResponse.getInstance();
            ByteArrayOutputStream baos = new ByteArrayOutputStream(128);
            AsyncResponseWrapper w = new AsyncResponseWrapper(httpResp, baos);
            chain.doFilter(httpReq, w);

            w.closeBuffer();

            // write out response
            byte[] b = baos.toByteArray();

            if (!async.getEncoded().isEmpty()) {
                httpResp.setHeader("javax.faces.Async", "true");
                httpResp.setContentType("text/xml");
                baos.reset();
                baos.write("<async-resp state=\"".getBytes());
                baos.write(async.getViewState().getBytes());
                baos.write("\">".getBytes());
                for (Map.Entry<String, String> e : async.getEncoded()
                        .entrySet()) {
                    baos.write("<encode id=\"".getBytes());
                    baos.write(e.getKey().getBytes());
                    baos.write("\"><![CDATA[".getBytes());
                    baos.write(e.getValue().getBytes());
                    baos.write("]]></encode>".getBytes());
                }
                if (b.length > 0) {
                    baos.write("<data><![CDATA[".getBytes());
                    baos.write(b);
                    baos.write("]]></data>".getBytes());
                }
                baos.write("</async-resp>".getBytes());
                b = baos.toByteArray();
            }
            httpResp.setContentLength(b.length);
            httpResp.getOutputStream().write(b);
        } finally {
            AsyncResponse.clearInstance();
        }
    }

    public void destroy() {
        // TODO Auto-generated method stub

    }

}
