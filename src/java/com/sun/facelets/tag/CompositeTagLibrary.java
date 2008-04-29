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

package com.sun.facelets.tag;

import com.sun.facelets.tag.renderkit.TemplateRendererTagLibrary;
import java.lang.reflect.Method;

import javax.faces.FacesException;

import com.sun.facelets.util.ParameterCheck;

/**
 * A TagLibrary that is composed of 1 or more TagLibrary children. Uses the
 * chain of responsibility pattern to stop searching as soon as one of the
 * children handles the requested method.
 * 
 * @author Jacob Hookom
 * @version $Id: CompositeTagLibrary.java,v 1.3.26.1 2008/04/29 19:56:47 edburns Exp $
 */
public final class CompositeTagLibrary implements TagLibrary {

    private TagLibrary[] libraries;

    public CompositeTagLibrary(TagLibrary[] libraries) {
        ParameterCheck.notNull("libraries", libraries);
        this.libraries = libraries;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sun.facelets.tag.TagLibrary#containsNamespace(java.lang.String)
     */
    public boolean containsNamespace(String ns) {
        boolean result = true;
        for (int i = 0; i < this.libraries.length; i++) {
            if (this.libraries[i].containsNamespace(ns)) {
                return true;
            }
        }
        if (TemplateRendererTagLibrary.tagLibraryForNSExists(ns)) {
            TagLibrary [] librariesPlusOne = new TagLibrary[libraries.length+1];
            System.arraycopy(this.libraries, 0, librariesPlusOne, 
                    0, libraries.length);
            librariesPlusOne[libraries.length] = 
                    new TemplateRendererTagLibrary(ns);
            for (int i = 0; i < this.libraries.length; i++) {
                libraries[i] = null;
            }
            libraries = librariesPlusOne;
            return true;
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sun.facelets.tag.TagLibrary#containsTagHandler(java.lang.String,
     *      java.lang.String)
     */
    public boolean containsTagHandler(String ns, String localName) {
        for (int i = 0; i < this.libraries.length; i++) {
            if (this.libraries[i].containsTagHandler(ns, localName)) {
                return true;
            }
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sun.facelets.tag.TagLibrary#createTagHandler(java.lang.String,
     *      java.lang.String, com.sun.facelets.tag.TagConfig)
     */
    public TagHandler createTagHandler(String ns, String localName,
            TagConfig tag) throws FacesException {
        for (int i = 0; i < this.libraries.length; i++) {
            if (this.libraries[i].containsTagHandler(ns, localName)) {
                return this.libraries[i].createTagHandler(ns, localName, tag);
            }
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sun.facelets.tag.TagLibrary#containsFunction(java.lang.String,
     *      java.lang.String)
     */
    public boolean containsFunction(String ns, String name) {
        for (int i = 0; i < this.libraries.length; i++) {
            if (this.libraries[i].containsFunction(ns, name)) {
                return true;
            }
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sun.facelets.tag.TagLibrary#createFunction(java.lang.String,
     *      java.lang.String)
     */
    public Method createFunction(String ns, String name) {
        for (int i = 0; i < this.libraries.length; i++) {
            if (this.libraries[i].containsFunction(ns, name)) {
                return this.libraries[i].createFunction(ns, name);
            }
        }
        return null;
    }
}
