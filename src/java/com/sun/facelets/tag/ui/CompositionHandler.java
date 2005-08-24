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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.el.ELException;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;

import com.sun.facelets.FaceletContext;
import com.sun.facelets.FaceletException;
import com.sun.facelets.FaceletHandler;
import com.sun.facelets.tag.TagAttribute;
import com.sun.facelets.tag.TagAttributeException;
import com.sun.facelets.tag.TagConfig;
import com.sun.facelets.tag.TagHandler;

/**
 * @author Jacob Hookom
 * @version $Id: CompositionHandler.java,v 1.6 2005/08/24 04:38:55 jhook Exp $
 */
public final class CompositionHandler extends TagHandler implements
        TemplateClient {

    private final Logger log = Logger.getLogger("facelets.tag.ui.composition");
    
    public final static String Name = "composition";

    protected final TagAttribute template;

    protected final Map handlers;

    /**
     * @param config
     */
    public CompositionHandler(TagConfig config) {
        super(config);
        this.template = this.getAttribute("template");
        this.handlers = new HashMap();
        if (this.template != null) {
            Iterator itr = this.findNextByType(DefineHandler.class);
            DefineHandler d = null;
            while (itr.hasNext()) {
                d = (DefineHandler) itr.next();
                this.handlers.put(d.getName(), d);
                if (log.isLoggable(Level.FINE)) {
                    log.fine(tag + " found Define["+d.getName()+"]");
                }
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sun.facelets.FaceletHandler#apply(com.sun.facelets.FaceletContext,
     *      javax.faces.component.UIComponent)
     */
    public void apply(FaceletContext ctx, UIComponent parent)
            throws IOException, FacesException, FaceletException, ELException {
        if (this.template != null) {
            TemplateManager mngr = TemplateManager.getInstance(ctx);
            mngr.pushClient(this);
            try {
                ctx.includeFacelet(parent, this.template.getValue(ctx));
            } finally {
                mngr.popClient();
            }
        } else {
            this.nextHandler.apply(ctx, parent);
        }
    }

    public FaceletHandler getHandler(FaceletContext ctx, String name) {
        if (name != null) {
            return (FaceletHandler) this.handlers.get(name);
        } else {
            return this.nextHandler;
        }
    }

}
