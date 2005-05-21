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

package com.sun.facelets;

import java.io.IOException;

import javax.el.ELException;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

/**
 * The parent or root object in a FaceletHandler composition. The Facelet will
 * take care of populating the passed UIComponent parent in relation to the
 * create/restore lifecycle of JSF.
 * 
 * @author Jacob Hookom
 * @version $Id: Facelet.java,v 1.1 2005/05/21 17:54:59 jhook Exp $
 */
public abstract class Facelet {

    /**
     * The passed UIComponent parent will be populated/restored in accordance
     * with the JSF 1.2 specification.
     * 
     * @param facesContext
     *            The current FacesContext (Should be the same as
     *            FacesContext.getInstance())
     * @param parent
     *            The UIComponent to populate in a compositional fashion. In
     *            most cases a Facelet will be base a UIViewRoot.
     * @throws IOException
     * @throws FacesException
     * @throws FaceletException
     * @throws ELException
     */
    public abstract void apply(FacesContext facesContext, UIComponent parent)
            throws IOException, FacesException, FaceletException, ELException;
}
