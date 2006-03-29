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

package com.sun.facelets.tag.jsf.core;

import java.io.IOException;
import java.util.logging.Logger;

import javax.el.ELException;
import javax.el.MethodExpression;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.event.PhaseEvent;

import com.sun.facelets.FaceletContext;
import com.sun.facelets.FaceletException;
import com.sun.facelets.tag.TagAttribute;
import com.sun.facelets.tag.TagConfig;
import com.sun.facelets.tag.TagHandler;
import com.sun.facelets.tag.jsf.ComponentSupport;

/**
 * Container for all JavaServer Faces core and custom component actions used on
 * a page. <p/> See <a target="_new"
 * href="http://java.sun.com/j2ee/javaserverfaces/1.1_01/docs/tlddocs/f/view.html">tag
 * documentation</a>.
 * 
 * @author Jacob Hookom
 * @version $Id: ViewHandler.java,v 1.3 2006/03/29 04:10:09 jhook Exp $
 */
public final class ViewHandler extends TagHandler {

    private final static Class[] LISTENER_SIG = new Class[] { PhaseEvent.class };

    private final TagAttribute locale;

    private final TagAttribute renderKitId;
    
    private final TagAttribute contentType;

    private final TagAttribute beforePhaseListener;

    private final TagAttribute afterPhaseListener;

    /**
     * @param config
     */
    public ViewHandler(TagConfig config) {
        super(config);
        this.locale = this.getAttribute("locale");
        this.renderKitId = this.getAttribute("renderKitId");
        this.contentType = this.getAttribute("contentType");
        this.beforePhaseListener = this.getAttribute("beforePhaseListener");
        this.afterPhaseListener = this.getAttribute("afterPhaseListener");
    }

    /**
     * See taglib documentation.
     * 
     * @see com.sun.facelets.FaceletHandler#apply(com.sun.facelets.FaceletContext,
     *      javax.faces.component.UIComponent)
     */
    public void apply(FaceletContext ctx, UIComponent parent)
            throws IOException, FacesException, FaceletException, ELException {
        UIViewRoot root = ComponentSupport.getViewRoot(ctx, parent);
        if (root != null) {
            if (this.locale != null) {
                root.setLocale(ComponentSupport.getLocale(ctx,
                        this.locale));
            }
            if (this.renderKitId != null) {
                String v = this.renderKitId.getValue(ctx);
                root.setRenderKitId(v);
            }
            if (this.contentType != null) {
                String v = this.contentType.getValue(ctx);
                root.getAttributes().put("contentType", v);
            }
            if (this.beforePhaseListener != null) {
                MethodExpression m = this.beforePhaseListener
                        .getMethodExpression(ctx, null, LISTENER_SIG);
                root.setBeforePhaseListener(m);
            }
            if (this.afterPhaseListener != null) {
                MethodExpression m = this.afterPhaseListener
                        .getMethodExpression(ctx, null, LISTENER_SIG);
                root.setAfterPhaseListener(m);
            }
        }
        this.nextHandler.apply(ctx, parent);
    }

}
