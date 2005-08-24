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

import javax.faces.component.UIComponent;

import java.io.IOException;

import javax.el.ELException;
import javax.faces.FacesException;

import com.sun.facelets.FaceletContext;
import com.sun.facelets.FaceletException;
import com.sun.facelets.FaceletHandler;

final class UILiteralTextHandler implements FaceletHandler {
    
    protected final String txtString;
    
    public UILiteralTextHandler(String txtString) {
        this.txtString = txtString;
    }

    public void apply(FaceletContext ctx, UIComponent parent)
            throws IOException, FacesException, FaceletException, ELException {
        if (parent != null) {
            parent.getChildren().add(new UILiteralText(this.txtString));
        }
    }
}
