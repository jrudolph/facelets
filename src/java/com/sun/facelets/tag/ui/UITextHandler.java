/**
 * Copyright 2005 Sun Microsystems, Inc. All rights reserved.
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
import com.sun.facelets.el.ELText;

/**
 * @author Jacob Hookom
 * @version $Id: UITextHandler.java,v 1.1 2005/05/21 17:54:57 jhook Exp $
 */
public final class UITextHandler implements FaceletHandler {

    private final ELText txt;

    public UITextHandler(ELText txt) {
        this.txt = txt;
    }

    public void apply(FaceletContext ctx, UIComponent parent)
            throws IOException, FacesException, FaceletException, ELException {
        if (parent != null) {
            ELText nt = this.txt.apply(ctx.getExpressionFactory(), ctx);
            parent.getChildren().add(new UIText(nt));
        }
    }

    public String toString() {
        return this.txt.toString();
    }
}
