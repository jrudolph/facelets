/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
 * @version $Id: FaceletHandler.java,v 1.2.28.1 2008/08/14 21:12:07 rlubke Exp $
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
