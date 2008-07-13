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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

/**
 * 
 * @author Jacob Hookom
 * @version $Id: MockServletContext.java,v 1.2 2008/07/13 19:01:48 rlubke Exp $
 */
public class MockServletContext implements ServletContext {

    protected final Properties initParams = new Properties();

    protected final Logger log = Logger
            .getLogger("facelets.mock.ServletContext");

    protected final Hashtable attributes = new Hashtable();

    protected final URI base;

    public MockServletContext(URI base) {
        this.base = base;
        File f = new File(base);
        if (!f.exists()) {
            throw new IllegalArgumentException("File: " + base.getPath()
                    + " doesn't exist");
        }
    }

    public ServletContext getContext(String name) {
        throw new UnsupportedOperationException();
    }

    public int getMajorVersion() {
        return 2;
    }

    public int getMinorVersion() {
        return 3;
    }

    public String getMimeType(String path) {
        throw new UnsupportedOperationException();
    }

    public Set getResourcePaths(String path) {
        URI uri = this.resolve(path);
        if (uri != null) {
            File f = new File(uri);
            if (f.exists() && f.isDirectory()) {
                File[] c = f.listFiles();
                Set s = new HashSet();
                int start = f.getAbsolutePath().length();
                for (int i = 0; i < c.length; i++) {
                    s.add(c[i].getAbsolutePath().substring(start));
                }
                return s;
            }
        }
        return Collections.EMPTY_SET;
    }

    public URL getResource(String path) throws MalformedURLException {
        URI uri = this.resolve(path);
        if (uri != null) {
            File f = new File(uri);
            if (f.exists()) {
                return uri.toURL();
            }
        }
        return null;
    }

    public InputStream getResourceAsStream(String path) {
        URI uri = this.resolve(path);
        if (uri != null) {
            try {
                File f = new File(uri);
                if (f.exists()) {
                    return uri.toURL().openStream();
                }
            } catch (MalformedURLException e) {
                this.log.severe(e.getMessage());
                return null;
            } catch (IOException e) {
                this.log.severe(e.getMessage());
                return null;
            }
        }
        return null;
    }

    public RequestDispatcher getRequestDispatcher(String path) {
        URI uri = this.resolve(path);
        if (uri != null) {
            File f = new File(uri);
            if (f.exists()) {
                try {
                    return new MockRequestDispatcher(uri.toURL());
                } catch (MalformedURLException e) {
                    this.log.severe(e.getMessage());
                    return null;
                }
            }

        }
        return null;
    }

    public RequestDispatcher getNamedDispatcher(String fileName) {
        throw new UnsupportedOperationException();
    }

    public Servlet getServlet(String name) throws ServletException {
        throw new UnsupportedOperationException();
    }

    public Enumeration getServlets() {
        throw new UnsupportedOperationException();
    }

    public Enumeration getServletNames() {
        throw new UnsupportedOperationException();
    }

    public void log(String message) {
        this.log.info(message);
    }

    public void log(Exception error, String message) {
        this.log.log(Level.INFO, message, error);

    }

    public void log(String message, Throwable error) {
        this.log.log(Level.INFO, message, error);
    }

    public String getRealPath(String path) {
        URI uri = this.resolve(path);
        if (uri != null) {
            File f = new File(uri);
            if (f.exists()) {
                return f.getAbsolutePath();
            }
        }
        return null;
    }

    private final URI resolve(String path) {
        if (path == null) {
            throw new NullPointerException("Path cannot be null");
        }
        if (path.charAt(0) == '/') {
            if (path.length() > 1) {
                return this.base.resolve(path.substring(1));
            }
            return this.base;
        }
        return null;
    }

    public String getServerInfo() {
        return this.getClass().getName();
    }

    public String getInitParameter(String name) {
        return this.initParams.getProperty(name);
    }

    public Enumeration getInitParameterNames() {
        return this.initParams.keys();
    }
    
    public void setInitParameter(String name, String value) {
        this.initParams.setProperty(name, value);
    }

    public Object getAttribute(String name) {
        return this.attributes.get(name);
    }

    public Enumeration getAttributeNames() {
        return this.attributes.keys();
    }

    public void setAttribute(String name, Object value) {
        this.attributes.put(name, value);
    }

    public void removeAttribute(String name) {
        this.attributes.remove(name);
    }

    public String getServletContextName() {
        return this.getClass().getName();
    }

}
