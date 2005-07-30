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

package com.sun.facelets.tag.jstl.core;

import javax.faces.component.UIComponent;

import com.sun.facelets.tag.TagConfig;
import java.io.IOException;

import javax.el.ELException;
import javax.faces.FacesException;

import com.sun.facelets.FaceletContext;
import com.sun.facelets.FaceletException;
import com.sun.facelets.tag.TagHandler;

/**
 * @author Jacob Hookom
 * @version $Id: ChooseOtherwiseHandler.java,v 1.1 2005/07/30 21:35:55 jhook Exp $
 */
public final class ChooseOtherwiseHandler extends TagHandler {

    public ChooseOtherwiseHandler(TagConfig config) {
        super(config);
    }

    public void apply(FaceletContext ctx, UIComponent parent)
            throws IOException, FacesException, FaceletException, ELException {
        this.nextHandler.apply(ctx, parent);
    }

}
