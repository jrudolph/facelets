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

package com.sun.facelets.compiler;

import java.io.IOException;

import javax.el.ELException;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;

import com.sun.facelets.FaceletContext;
import com.sun.facelets.FaceletException;
import com.sun.facelets.FaceletHandler;
import com.sun.facelets.el.ELText;
import com.sun.facelets.tag.TextHandler;

/**
 * @author Jacob Hookom
 * @version $Id: UITextHandler.java,v 1.6 2005/09/02 04:45:49 jhook Exp $
 */
final class UITextHandler implements FaceletHandler, TextHandler {

    private final ELText txt;
    
    private final String alias;

    public UITextHandler(String alias, ELText txt) {
        this.alias = alias;
        this.txt = txt;
    }

    public void apply(FaceletContext ctx, UIComponent parent)
            throws IOException, FacesException, FaceletException, ELException {
        if (parent != null) {
            try {
                ELText nt = this.txt.apply(ctx.getExpressionFactory(), ctx);
                parent.getChildren().add(new UIText(this.alias, nt));
            } catch (Exception e) {
                throw new ELException(this.alias + ": "+ e.getMessage(), e.getCause());
            }
        }
    }

    public String toString() {
        return this.txt.toString();
    }

    public String getText() {
        return this.txt.toString();
    }
}
