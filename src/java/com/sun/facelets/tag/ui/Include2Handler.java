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

import com.sun.facelets.tag.ui.*;
import java.io.IOException;

import javax.el.ELException;
import javax.el.VariableMapper;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;

import com.sun.facelets.FaceletContext;
import com.sun.facelets.FaceletException;
import com.sun.facelets.el.VariableMapperWrapper;
import com.sun.facelets.tag.TagAttribute;
import com.sun.facelets.tag.TagConfig;
import com.sun.facelets.tag.TagHandler;
import java.util.Map;

/**
 * @author Jacob Hookom
 * @version $Id: Include2Handler.java,v 1.1.2.3 2007/12/21 14:59:04 edburns Exp $
 */
public final class Include2Handler extends TagHandler {

    private final TagAttribute src;
    
    public static final String INCLUDE2_TAG_REQUEST_ATTR_NAME = "com.sun.facelets.InnerComponentId";

    /**
     * @param config
     */
    public Include2Handler(TagConfig config) {
        super(config);
        this.src = this.getRequiredAttribute("src");
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sun.facelets.FaceletHandler#apply(com.sun.facelets.FaceletContext,
     *      javax.faces.component.UIComponent)
     */
    public void apply(FaceletContext ctx, UIComponent parent)
            throws IOException, FacesException, FaceletException, ELException {
        String path = this.src.getValue(ctx);
        VariableMapper orig = ctx.getVariableMapper();
        ctx.setVariableMapper(new VariableMapperWrapper(orig));
        Map<String,Object> requestMap = ctx.getFacesContext().getExternalContext().getRequestMap();
        Object oldTag = null;

        try {
            oldTag = requestMap.put(INCLUDE2_TAG_REQUEST_ATTR_NAME, this.tag);
            ctx.getFacesContext().getELContext().putContext(Include2Handler.class, this.nextHandler);
            ctx.includeFacelet(parent, path); // This causes nextHandler.apply to be called
        } finally {
            // uncomment-out when 179 is fixed
            // https://jsp-spec-public.dev.java.net/issues/show_bug.cgi?id=179
            // ctx.getFacesContext().getELContext().putContext(Include2Handler.class, 
            //        null);
            if (null != oldTag) {
                requestMap.put(INCLUDE2_TAG_REQUEST_ATTR_NAME, oldTag);
            }
            else {
                requestMap.remove(INCLUDE2_TAG_REQUEST_ATTR_NAME);
            }

            ctx.setVariableMapper(orig);
        }
    }
}
