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


import java.io.IOException;
import java.io.Writer;

import javax.faces.component.UIComponent;
import javax.faces.context.ResponseWriter;


/**
 * @author Jacob Hookom
 *
 */
public class MockResponseWriter extends ResponseWriter {

    private final Writer writer;
    private boolean startOpen;
    
    public MockResponseWriter(Writer writer) {
        this.writer = writer;
    }
    
    /* (non-Javadoc)
     * @see javax.faces.context.ResponseWriter#getContentType()
     */
    public String getContentType() {
        return "text/html";
    }

    /* (non-Javadoc)
     * @see javax.faces.context.ResponseWriter#getCharacterEncoding()
     */
    public String getCharacterEncoding() {
        return "UTF-8";
    }

    /* (non-Javadoc)
     * @see javax.faces.context.ResponseWriter#flush()
     */
    public void flush() throws IOException {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see javax.faces.context.ResponseWriter#startDocument()
     */
    public void startDocument() throws IOException {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see javax.faces.context.ResponseWriter#endDocument()
     */
    public void endDocument() throws IOException {
        // TODO Auto-generated method stub

    }

    private void closeStart() throws IOException {
        if (this.startOpen) {
            this.writer.write('>');
        }
        this.startOpen = false;
    }
    
    /* (non-Javadoc)
     * @see javax.faces.context.ResponseWriter#startElement(java.lang.String, javax.faces.component.UIComponent)
     */
    public void startElement(String arg0, UIComponent arg1) throws IOException {
        this.closeStart();
        this.writer.write('<');
        this.writer.write(arg0);
        this.startOpen = true;
    }

    /* (non-Javadoc)
     * @see javax.faces.context.ResponseWriter#endElement(java.lang.String)
     */
    public void endElement(String arg0) throws IOException {
        if (this.startOpen) {
            this.writer.write("/>");
            this.startOpen = false;
        } else {
            this.writer.write("</");
            this.writer.write(arg0);
            this.writer.write('>');
        }

    }

    /* (non-Javadoc)
     * @see javax.faces.context.ResponseWriter#writeAttribute(java.lang.String, java.lang.Object, java.lang.String)
     */
    public void writeAttribute(String arg0, Object arg1, String arg2)
            throws IOException {
        if (arg1 != null) {
            writer.write(' ');
            writer.write(arg0);
            writer.write("=\"");
            writer.write(arg1.toString());
            writer.write('"');
        }
    }

    /* (non-Javadoc)
     * @see javax.faces.context.ResponseWriter#writeURIAttribute(java.lang.String, java.lang.Object, java.lang.String)
     */
    public void writeURIAttribute(String arg0, Object arg1, String arg2)
            throws IOException {
        this.writeAttribute(arg0, arg1, arg2);
    }

    /* (non-Javadoc)
     * @see javax.faces.context.ResponseWriter#writeComment(java.lang.Object)
     */
    public void writeComment(Object arg0) throws IOException {
        this.closeStart();
        this.writer.write("<!-- ");
        this.writer.write(arg0 != null ? arg0.toString() : "null");
        this.writer.write(" -->");
    }

    /* (non-Javadoc)
     * @see javax.faces.context.ResponseWriter#writeText(java.lang.Object, java.lang.String)
     */
    public void writeText(Object arg0, String arg1) throws IOException {
        this.closeStart();
        this.writer.write(arg0 != null ? arg0.toString() : "null");
    }

    /* (non-Javadoc)
     * @see javax.faces.context.ResponseWriter#writeText(char[], int, int)
     */
    public void writeText(char[] arg0, int arg1, int arg2) throws IOException {
        this.closeStart();
        this.writer.write(arg0, arg1, arg2);
    }

    /* (non-Javadoc)
     * @see javax.faces.context.ResponseWriter#cloneWithWriter(java.io.Writer)
     */
    public ResponseWriter cloneWithWriter(Writer arg0) {
        return new MockResponseWriter(arg0);
    }

    /* (non-Javadoc)
     * @see java.io.Writer#write(char[], int, int)
     */
    public void write(char[] cbuf, int off, int len) throws IOException {
        this.writer.write(cbuf, off, len);
    }

    /* (non-Javadoc)
     * @see java.io.Writer#close()
     */
    public void close() throws IOException {
        // TODO Auto-generated method stub

    }

}
