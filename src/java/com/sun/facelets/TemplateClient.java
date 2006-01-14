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
 * @author Jacob Hookom
 * @version $Id: TemplateClient.java,v 1.1 2006/01/14 06:46:17 jhook Exp $
 */
public interface TemplateClient {

    public boolean apply(FaceletContext ctx, UIComponent parent, String name)
            throws IOException, FacesException, FaceletException, ELException;;
}
