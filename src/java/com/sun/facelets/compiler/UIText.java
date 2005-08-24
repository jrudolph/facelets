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
import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import com.sun.facelets.el.ELAdaptor;
import com.sun.facelets.el.ELText;

/**
 * @author Jacob Hookom
 * @version $Id: UIText.java,v 1.5 2005/08/24 04:38:55 jhook Exp $
 */
final class UIText extends UILeaf {

    private final ELText txt;
    
    private final String alias;

    public UIText(String alias, ELText txt) {
        this.txt = txt;
        this.alias = alias;
    }

    public String getFamily() {
        return null;
    }

    public void encodeBegin(FacesContext context) throws IOException {
        ResponseWriter out = context.getResponseWriter();
        try {
            txt.write(out, ELAdaptor.getELContext(context));
        } catch (Exception e) {
            throw new ELException(this.alias + ": " + e.getMessage(), e.getCause());
        }
    }

    public String getRendererType() {
        return null;
    }

    public boolean getRendersChildren() {
        return true;
    }

    public String toString() {
        return this.txt.toString();
    }
}
