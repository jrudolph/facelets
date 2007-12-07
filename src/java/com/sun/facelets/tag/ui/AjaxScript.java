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

package com.sun.facelets.tag.ui;

import java.io.IOException;
import java.util.Map;

import javax.faces.application.Resource;
import javax.faces.application.ResourceHandler;
import javax.faces.component.UIOutput;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

/**
 * PENDING (rogerk) - spec 
 */
public final class AjaxScript extends UIOutput {

    /**
     * <p>Request attribute that indicates a <code>script</code> tag
     * pointing to JSFAjax.js has been rendered already.</p>
     */
    private static final String AJAX_JS_LINKED = "com.sun.faces.facelets.LINKED/JSFAjax.js";

    public final static String COMPONENT_TYPE = "facelets.ui.AjaxScript";
    public final static String COMPONENT_FAMILY = "facelets";
    
    public AjaxScript() {
        super();
        this.setTransient(true);
        this.setRendered(true);
        this.setRendererType(null);
    }

    /**
     * <p>Return the family for this component.</p>
     */
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    /**
     * PENDING (rogerk) - Spec doc
     */
    public void encodeBegin(FacesContext context) throws IOException {

        // If we've already rendered the script.

        Map requestMap = context.getExternalContext().getRequestMap();
        if (requestMap.containsKey(AJAX_JS_LINKED)) {
            return;
        }

        ResourceHandler handler = getFacesContext().getApplication().getResourceHandler();
        Resource resource = handler.createResource("JSFAjax.js", "javascript");
        ResponseWriter writer = context.getResponseWriter();
        writer.startElement("script", this); 
        writer.writeAttribute("type", "text/javascript", "type");
        writer.writeAttribute("src", resource.getURI(), "src");
        writer.endElement("script"); 
        requestMap.put(AJAX_JS_LINKED, Boolean.TRUE);
    }
}
