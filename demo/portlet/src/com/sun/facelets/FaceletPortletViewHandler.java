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

package com.sun.facelets;

import java.io.IOException;

import javax.faces.FacesException;
import javax.faces.application.ViewHandler;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.RenderKit;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

/**
 * ViewHandler implementation for Facelets used in Portlets
 * 
 */
public class FaceletPortletViewHandler extends FaceletViewHandler {

    public FaceletPortletViewHandler(ViewHandler parent) {
        super(parent);
    }

    protected ResponseWriter createResponseWriter(FacesContext context)
            throws IOException, FacesException {
        ExternalContext extContext = context.getExternalContext();
        RenderKit renderKit = context.getRenderKit();

        RenderRequest request = (RenderRequest) extContext.getRequest();
        RenderResponse response = (RenderResponse) extContext.getResponse();

        String contenttype = request.getResponseContentType();
        if (contenttype == null) {
            contenttype = "text/html";
        }

        String encoding = response.getCharacterEncoding();
        if (encoding == null) {
            encoding = "ISO-8859-1";
        }

        ResponseWriter writer = renderKit.createResponseWriter(NullWriter.Instance, contenttype, encoding);

        contenttype = writer.getContentType();

        // apply them to the response
        response.setContentType(contenttype);

        // Now, clone with the real writer
        writer = writer.cloneWithWriter(response.getWriter());

        return writer;
    }
}