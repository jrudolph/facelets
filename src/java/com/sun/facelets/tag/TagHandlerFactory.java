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

import javax.el.ELException;
import javax.faces.FacesException;

/**
 * Delegate class for TagLibraries
 * 
 * @see TagLibrary
 * @author Jacob Hookom
 * @version $Id: TagHandlerFactory.java,v 1.3 2005/08/24 04:38:48 jhook Exp $
 */
interface TagHandlerFactory {
    /**
     * A new TagHandler instantiated with the passed TagConfig
     * 
     * @param cfg
     *            TagConfiguration information
     * @return a new TagHandler
     * @throws FacesException
     * @throws ELException
     */
    public TagHandler createHandler(TagConfig cfg) throws FacesException,
            ELException;
}
