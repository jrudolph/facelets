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

package com.sun.facelets.compiler;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import com.sun.facelets.tag.AbstractTagLibrary;
import com.sun.facelets.tag.TagHandler;
import com.sun.facelets.tag.TagLibrary;
import com.sun.facelets.util.Assert;
import com.sun.facelets.util.Classpath;

/**
 * Handles creating a {@link com.sun.facelets.tag.TagLibrary TagLibrary} from a
 * {@link java.net.URL URL} source.
 * 
 * @author Jacob Hookom
 * @version $Id: TagLibraryConfig.java,v 1.2 2005/07/13 02:18:57 adamwiner Exp $
 */
public final class TagLibraryConfig {

    private final static String SUFFIX = ".taglib.xml";

    protected final static Logger log = Logger.getLogger("facelets.compiler");

    private static class TagLibraryImpl extends AbstractTagLibrary {
        public TagLibraryImpl(String namespace) {
            super(namespace);
        }

        public void putConverter(String name, String id) {
            Assert.param("name", name);
            Assert.param("id", id);
            this.addConverter(name, id);
        }

        public void putValidator(String name, String id) {
            Assert.param("name", name);
            Assert.param("id", id);
            this.addValidator(name, id);
        }

        public void putTagHandler(String name, Class type) {
            Assert.param("name", name);
            Assert.param("type", type);
            this.addTagHandler(name, type);
        }

        public void putComponent(String name, String componentId,
                String rendererId) {
            Assert.param("name", name);
            Assert.param("componentId", componentId);
            this.addComponent(name, componentId, rendererId);
        }

        public void putUserTag(String name, URL source) {
            Assert.param("name", name);
            Assert.param("source", source);
            this.addUserTag(name, source);
        }
    }

    private static class LibraryHandler extends DefaultHandler {
        private final String file;

        private final URL source;

        private TagLibrary library;

        private final StringBuffer buffer;

        private Locator locator;

        private String tagName;

        private String componentClassName;

        private String componentType;

        private String rendererType;

        public LibraryHandler(URL source) {
            this.file = source.getFile();
            this.source = source;
            this.buffer = new StringBuffer(64);
        }

        public TagLibrary getLibrary() {
            return this.library;
        }

        public void endElement(String uri, String localName, String qName)
                throws SAXException {
            try {
                if ("facelet-taglib".equals(qName)) {
                  ; // Nothing to do
                }
                else if ("library-class".equals(qName)) {
                    this.processLibraryClass();
                }
                else if ("namespace".equals(qName)) {
                    this.library = new TagLibraryImpl(this.captureBuffer());
                }
                else if ("component-type".equals(qName)) {
                    this.componentType = this.captureBuffer();
                }
                else if ("renderer-type".equals(qName)) {
                    this.rendererType = this.captureBuffer();
                }
                else if ("tag-name".equals(qName)) {
                    this.tagName = this.captureBuffer();
                }
                else
                {
                    // Make sure there we've seen a namespace element
                    // before trying any of the following elements to avoid
                    // obscure NPEs
                    if (this.library == null) {
                        throw new IllegalStateException("No <namespace> element");
                    }
                    
                    TagLibraryImpl impl = (TagLibraryImpl) this.library;
                    
                    if ("handler-class".equals(qName)) {
                        String cName = this.captureBuffer();
                        Class c = this.createClass(TagHandler.class, cName);
                        impl.putTagHandler(this.tagName, c);
                    }
                    else if ("component".equals(qName)) {
                        impl.putComponent(this.tagName,
                                          this.componentType,
                                          this.rendererType);
                    }
                    else if ("converter-id".equals(qName)) {
                        impl.putConverter(this.tagName,
                                          this.captureBuffer());
                    }
                    else if ("validator-id".equals(qName)) {
                        impl.putValidator(this.tagName,
                                          this.captureBuffer());
                    }
                    else  if ("source".equals(qName)) {
                        String path = this.captureBuffer();
                        URL url = new URL(this.source, path);
                        impl.putUserTag(this.tagName, url);
                    }
                }
            } catch (Exception e) {
                SAXException saxe =
                   new SAXException("Error Handling [" + this.source + "@"
                        + this.locator.getLineNumber() + ","
                        + this.locator.getColumnNumber() + "] <" + qName
                                    + ">");
                saxe.initCause(e);
                throw saxe;
            }
        }

        private String captureBuffer() throws Exception {
            String s = this.buffer.toString().trim();
            if (s.length() == 0) {
                throw new Exception("Value Cannot be Empty");
            }
            this.buffer.setLength(0);
            return s;
        }

        private Class createClass(Class type, String name) throws Exception {
            Class factory = Class.forName(name);
            if (!type.isAssignableFrom(factory)) {
                throw new Exception(name + " must be an instance of "
                        + type.getName());
            }
            return factory;
        }

        private void processLibraryClass() throws Exception {
            String name = this.captureBuffer();
            Class type = this.createClass(TagLibrary.class, name);
            this.library = (TagLibrary) type.newInstance();
        }

        public InputSource resolveEntity(String publicId, String systemId)
                throws SAXException {
            if ("-//Sun Microsystems, Inc.//DTD Facelet Taglib 1.0//EN"
                    .equals(publicId)) {
                URL url = Thread.currentThread().getContextClassLoader()
                        .getResource("facelet-taglib_1_0.dtd");
                return new InputSource(url.toExternalForm());
            }
            return null;
        }

        public void characters(char[] ch, int start, int length)
                throws SAXException {
            this.buffer.append(ch, start, length);
        }

        public void startElement(String uri, String localName, String qName,
                Attributes attributes) throws SAXException {
            this.buffer.setLength(0);
            if ("tag".equals(qName)) {
                this.componentClassName = null;
                this.componentType = null;
                this.rendererType = null;
                this.tagName = null;
            }
        }

        public void error(SAXParseException e) throws SAXException {
            SAXException saxe = 
                new SAXException("Error Handling [" + this.source + "@"
                      + e.getLineNumber() + "," + e.getColumnNumber() + "]");
            saxe.initCause(e);
            throw saxe;
        }

        public void setDocumentLocator(Locator locator) {
            this.locator = locator;
        }
    }

    public TagLibraryConfig() {
        super();
    }

    public static TagLibrary create(URL url) throws IOException {
        InputStream is = null;
        try {
            is = url.openStream();
            LibraryHandler handler = new LibraryHandler(url);
            SAXParser parser = createSAXParser(handler);
            parser.parse(is, handler);
            return handler.getLibrary();
        } catch (SAXException e) {
          IOException ioe =
            new IOException("Error parsing [" + url + "]: ");
          ioe.initCause(e);
          throw ioe;
        } catch (ParserConfigurationException e) {
          IOException ioe =
            new IOException("Error parsing [" + url + "]: ");
          ioe.initCause(e);
          throw ioe;
        } finally {
            if (is != null)
                is.close();
        }
    }

    public void loadImplicit(Compiler compiler) throws IOException {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        URL[] urls = Classpath.search(cl, "META-INF/", SUFFIX);
        for (int i = 0; i < urls.length; i++) {
            try {
                compiler.addTagLibrary(create(urls[i]));
                log.info("Added Library from: " + urls[i]);
            } catch (Exception e) {
                log.log(Level.SEVERE, "Error Loading Library: " + urls[i], e);
            }
        }
    }

    private static final SAXParser createSAXParser(LibraryHandler handler)
            throws SAXException, ParserConfigurationException {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(false);
        factory.setFeature("http://xml.org/sax/features/validation", true);
        factory.setValidating(true);
        SAXParser parser = factory.newSAXParser();
        XMLReader reader = parser.getXMLReader();
        reader.setErrorHandler(handler);
        reader.setEntityResolver(handler);
        return parser;
    }

}
