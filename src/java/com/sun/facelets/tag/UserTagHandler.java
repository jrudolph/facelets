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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;

import javax.el.ELException;
import javax.el.VariableMapper;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;

import com.sun.facelets.FaceletContext;
import com.sun.facelets.FaceletException;
import com.sun.facelets.FaceletHandler;
import com.sun.facelets.TemplateClient;
import com.sun.facelets.el.VariableMapperWrapper;

/**
 * A Tag that is specified in a FaceletFile. Takes all attributes specified and
 * sets them on the FaceletContext before including the targeted Facelet file.
 * 
 * @author Jacob Hookom
 * @version $Id: UserTagHandler.java,v 1.7 2006/03/29 04:10:13 jhook Exp $
 */
final class UserTagHandler extends TagHandler implements TemplateClient {

    protected final TagAttribute[] vars;

    protected final URL location;

    /**
     * @param config
     */
    public UserTagHandler(TagConfig config, URL location) {
        super(config);
        this.vars = this.tag.getAttributes().getAll();
        this.location = location;
    }

    /**
     * Iterate over all TagAttributes and set them on the FaceletContext's
     * VariableMapper, then include the target Facelet. Finally, replace the old
     * VariableMapper.
     * 
     * @see TagAttribute#getValueExpression(FaceletContext, Class)
     * @see VariableMapper
     * @see com.sun.facelets.FaceletHandler#apply(com.sun.facelets.FaceletContext,
     *      javax.faces.component.UIComponent)
     */
    public void apply(FaceletContext ctx, UIComponent parent)
            throws IOException, FacesException, FaceletException, ELException {
        VariableMapper orig = ctx.getVariableMapper();
        
        // setup a variable map
        if (this.vars.length > 0) {
            VariableMapper varMapper = new VariableMapperWrapper(orig);
            for (int i = 0; i < this.vars.length; i++) {
                varMapper.setVariable(this.vars[i].getLocalName(), this.vars[i]
                        .getValueExpression(ctx, Object.class));
            }
            ctx.setVariableMapper(varMapper);
        }
        
        // eval include
        try {
            ctx.pushClient(this);
            ctx.includeFacelet(parent, this.location);
        } catch (FileNotFoundException e) {
            throw new TagException(this.tag, e.getMessage());
        } finally {
            
            // make sure we undo our changes
            ctx.popClient(this);
            ctx.setVariableMapper(orig);
        }
    }

    public boolean apply(FaceletContext ctx, UIComponent parent, String name) throws IOException, FacesException, FaceletException, ELException {
        if (name == null) {
            this.nextHandler.apply(ctx, parent);
            return true;
        }
        return false;
    }

}
