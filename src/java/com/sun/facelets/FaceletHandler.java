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

package com.sun.facelets;

import java.io.IOException;

import javax.el.ELException;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;

/**
 * A participant in UIComponent tree building
 * 
 * @author Jacob Hookom
 * @version $Id: FaceletHandler.java,v 1.2 2005/08/24 04:38:59 jhook Exp $
 */
public interface FaceletHandler {

    /**
     * Process changes on a particular UIComponent
     * 
     * @param ctx the current FaceletContext instance for this execution
     * @param parent the parent UIComponent to operate upon
     * @throws IOException
     * @throws FacesException
     * @throws FaceletException
     * @throws ELException
     */
    public void apply(FaceletContext ctx, UIComponent parent)
            throws IOException, FacesException, FaceletException, ELException;
}
