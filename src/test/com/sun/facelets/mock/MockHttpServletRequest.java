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

package com.sun.facelets.mock;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.security.Principal;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * 
 * @author Jacob Hookom
 * @version $Id: MockHttpServletRequest.java,v 1.3 2008/07/13 19:01:48 rlubke Exp $
 */
public class MockHttpServletRequest implements HttpServletRequest {

    private final ServletContext servletContext;

    private final URI uri;

    private final String method;

    private Cookie[] cookies = new Cookie[0];

    private final Hashtable headers = new Hashtable();

    private String remoteUser;

    private String servletPath;

    private HttpSession session;

    private final Hashtable attributes = new Hashtable();

    private final Properties param = new Properties();

    private String characterEncoding = "ISO-8859-1";

    private String contentType = "text/html";

    private int contentLength = 0;

    private String protocol = "HTTP/1.1";

    private String localName = "localhost";

    private int localPort = 80;

    private String remoteAddr = "127.0.0.1";

    private String remoteHost = "localhost";

    private Locale locale = Locale.getDefault();

    private Vector locales = new Vector(Arrays.asList(Locale
            .getAvailableLocales()));
    
    private boolean secure = false;
    
    private int remotePort = 1024;
    
    private String localAddr = "127.0.0.1";

    private ServletInputStream inputStream = new MockServletInputStream();

    public MockHttpServletRequest(ServletContext servletContext, URI uri) {
        this(servletContext, "GET", uri);
    }

    public MockHttpServletRequest(ServletContext servletContext, String uri) {
        this(servletContext, "GET", uri);
    }

    public MockHttpServletRequest(ServletContext servletContext, String method,
            String uri) {
        this(servletContext, method, URI.create(uri));
    }

    public MockHttpServletRequest(ServletContext servletContext, String method,
            URI uri) {
        this.servletContext = servletContext;
        this.uri = uri;
        this.method = method;

        String q = this.uri.getRawQuery();
        if (q != null) {
            String[] p = q.split("(&|=)");
            for (int i = 0; i < p.length; i += 2) {
                this.param.put(p[i], p[i + 1]);
            }
        }
    }

    public String getAuthType() {
        return BASIC_AUTH;
    }

    public Cookie[] getCookies() {
        return this.cookies;
    }

    public long getDateHeader(String name) {
        String hdr = this.getHeader(name);
        if (hdr != null) {
            try {
                return DateFormat.getDateInstance(DateFormat.FULL).parse(hdr)
                        .getTime();
            } catch (ParseException e) {
                throw new IllegalArgumentException("Header " + name + ": "
                        + hdr);
            }
        }
        return -1;
    }

    public String getHeader(String name) {
        Object obj = this.headers.get(name);
        if (obj instanceof List) {
            return ((List) obj).get(0).toString();
        } else if (obj instanceof String) {
            return (String) obj;
        }
        return null;
    }

    public Enumeration getHeaders(String name) {
        Object obj = this.headers.get(name);
        if (obj instanceof Vector) {
            return ((Vector) obj).elements();
        } else if (obj instanceof String) {
            Vector v = new Vector();
            v.add(obj);
            return v.elements();
        }
        return null;
    }

    public Enumeration getHeaderNames() {
        return this.headers.keys();
    }

    public int getIntHeader(String name) {
        String hdr = this.getHeader(name);
        if (hdr != null) {
            try {
                return Integer.parseInt(hdr);
            } catch (Exception e) {
                throw new IllegalArgumentException("Header " + name + ": "
                        + hdr);
            }
        }
        return -1;
    }

    public String getMethod() {
        return this.method;
    }

    public String getPathInfo() {
        return this.uri.getPath();
    }

    public String getPathTranslated() {
        return this.servletContext.getRealPath(this.uri.getPath());
    }

    public String getContextPath() {
        return this.uri.getPath();
    }

    public String getQueryString() {
        return this.uri.getQuery();
    }

    public String getRemoteUser() {
        return this.remoteUser;
    }

    public boolean isUserInRole(String role) {
        throw new UnsupportedOperationException();
    }

    public Principal getUserPrincipal() {
        throw new UnsupportedOperationException();
    }

    public String getRequestedSessionId() {
        return this.getParameter("jsessionid");
    }

    public String getRequestURI() {
        return this.uri.getPath();
    }

    public StringBuffer getRequestURL() {
        return new StringBuffer(this.uri.toString());
    }

    public String getServletPath() {
        return this.servletPath;
    }

    public HttpSession getSession(boolean create) {
        if (this.session == null && create) {
            this.session = new MockHttpSession(this.servletContext);
        }
        return this.session;
    }

    public HttpSession getSession() {
        return this.getSession(true);
    }

    public boolean isRequestedSessionIdValid() {
        throw new UnsupportedOperationException();
    }

    public boolean isRequestedSessionIdFromCookie() {
        throw new UnsupportedOperationException();
    }

    public boolean isRequestedSessionIdFromURL() {
        throw new UnsupportedOperationException();
    }

    public boolean isRequestedSessionIdFromUrl() {
        throw new UnsupportedOperationException();
    }

    public Object getAttribute(String name) {
        return this.attributes.get(name);
    }

    public Enumeration getAttributeNames() {
        return this.attributes.keys();
    }

    public String getCharacterEncoding() {
        return this.characterEncoding;
    }

    public void setCharacterEncoding(String characterEncoding)
            throws UnsupportedEncodingException {
        this.characterEncoding = characterEncoding;
    }

    public int getContentLength() {
        return this.contentLength;
    }

    public String getContentType() {
        return this.contentType;
    }

    public ServletInputStream getInputStream() throws IOException {
        return this.inputStream;
    }

    public String getParameter(String name) {
        return this.param.getProperty(name);
    }

    public Enumeration getParameterNames() {
        return this.param.keys();
    }

    public String[] getParameterValues(String name) {
        String p = this.param.getProperty(name);
        if (p != null) {
            return p.split(",");
        }
        return null;
    }
    
    public void setParameter(String name, String value) {
    	this.param.put(name, value);
    }

    public Map getParameterMap() {
        return Collections.unmodifiableMap(this.param);
    }

    public String getProtocol() {
        return this.protocol;
    }

    public String getScheme() {
        return this.uri.getScheme();
    }

    public String getServerName() {
        return this.localName;
    }

    public int getServerPort() {
        return this.localPort;
    }

    public BufferedReader getReader() throws IOException {
        if (this.inputStream != null) {
            Reader sourceReader = (this.characterEncoding != null) ? new InputStreamReader(
                    this.inputStream, this.characterEncoding)
                    : new InputStreamReader(this.inputStream);
            return new BufferedReader(sourceReader);
        } else {
            return null;
        }
    }

    public String getRemoteAddr() {
        return this.remoteAddr;
    }

    public String getRemoteHost() {
        return this.remoteHost;
    }

    public void setAttribute(String name, Object value) {
        this.attributes.put(name, value);
    }

    public void removeAttribute(String name) {
        this.attributes.remove(name);
    }

    public Locale getLocale() {
        return this.locale;
    }

    public Enumeration getLocales() {
        return this.locales.elements();
    }

    public boolean isSecure() {
        return this.secure;
    }

    public RequestDispatcher getRequestDispatcher(String path) {
        return this.servletContext.getRequestDispatcher(path);
    }

    public String getRealPath(String path) {
        return this.servletContext.getRealPath(path);
    }

    public int getRemotePort() {
        return this.remotePort;
    }

    public String getLocalName() {
        return this.localName;
    }

    public String getLocalAddr() {
        return this.localAddr;
    }

    public int getLocalPort() {
        return this.localPort;
    }

}
