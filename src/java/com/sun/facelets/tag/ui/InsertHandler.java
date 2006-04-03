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

import javax.el.ELException;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;

import com.sun.facelets.FaceletContext;
import com.sun.facelets.FaceletException;
import com.sun.facelets.FaceletHandler;
import com.sun.facelets.TemplateClient;
import com.sun.facelets.tag.TagAttribute;
import com.sun.facelets.tag.TagAttributeException;
import com.sun.facelets.tag.TagConfig;
import com.sun.facelets.tag.TagHandler;

/**
 * @author Jacob Hookom
 * @version $Id: InsertHandler.java,v 1.6 2006/04/03 06:25:36 jhook Exp $
 */
public final class InsertHandler extends TagHandler implements TemplateClient {

    private final String name;

    /**
     * @param config
     */
    public InsertHandler(TagConfig config) {
        super(config);
        TagAttribute attr = this.getAttribute("name");
        if (attr != null) {
            if (!attr.isLiteral()) {
                throw new TagAttributeException(this.tag, attr, "Must be Literal");
            }
            this.name = attr.getValue();
        } else {
            this.name = null;
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
        
        ctx.extendClient(this);
        boolean found = false;
        try {
            found = ctx.includeDefinition(parent, this.name);
        } finally {
            ctx.popClient(this);
        }
        if (!found) {
            this.nextHandler.apply(ctx, parent);
        }
    }

    public boolean apply(FaceletContext ctx, UIComponent parent, String name) throws IOException, FacesException, FaceletException, ELException {
        if (this.name == name || this.name != null && this.name.equals(name)) {
            this.nextHandler.apply(ctx, parent);
            return true;
        }
        return false;
    }
}
