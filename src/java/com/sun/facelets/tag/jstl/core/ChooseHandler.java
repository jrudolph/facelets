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

import com.sun.facelets.tag.TagConfig;
import com.sun.facelets.tag.TagException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.el.ELException;
import javax.faces.FacesException;

import javax.faces.component.UIComponent;

import com.sun.facelets.FaceletContext;
import com.sun.facelets.FaceletException;
import com.sun.facelets.tag.TagHandler;

/**
 * @author Jacob Hookom
 * @version $Id: ChooseHandler.java,v 1.1 2005/07/30 21:35:55 jhook Exp $
 */
public final class ChooseHandler extends TagHandler {
    
    private final ChooseOtherwiseHandler otherwise;
    private final ChooseWhenHandler[] when;
    
    public ChooseHandler(TagConfig config) {
        super(config);
        
        List whenList = new ArrayList();
        Iterator itr = this.findNextByType(ChooseWhenHandler.class);
        while (itr.hasNext()) {
            whenList.add(itr.next());
        }
        if (whenList.isEmpty()) {
            throw new TagException(this.tag, "Choose Tag must have one or more When Tags");
        }
        this.when = (ChooseWhenHandler[]) whenList.toArray(new ChooseWhenHandler[whenList.size()]);
        
        itr = this.findNextByType(ChooseOtherwiseHandler.class);
        if (itr.hasNext()) {
            this.otherwise = (ChooseOtherwiseHandler) itr.next();
        } else {
            this.otherwise = null;
        }
    }

    public void apply(FaceletContext ctx, UIComponent parent)
            throws IOException, FacesException, FaceletException, ELException {
        for (int i = 0; i < this.when.length; i++) {
            if (this.when[i].isTestTrue(ctx)) {
                this.when[i].apply(ctx, parent);
                return;
            }
        }
        if (this.otherwise != null) {
            this.otherwise.apply(ctx, parent);
        }
    }

}
