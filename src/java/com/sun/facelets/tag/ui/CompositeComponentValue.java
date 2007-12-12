/*
 * CompositeComponentValue.java
 */

/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License. You can obtain
 * a copy of the License at https://glassfish.dev.java.net/public/CDDL+GPL.html
 * or glassfish/bootstrap/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at glassfish/bootstrap/legal/LICENSE.txt.
 * Sun designates this particular file as subject to the "Classpath" exception
 * as provided by Sun in the GPL Version 2 section of the License file that
 * accompanied this code.  If applicable, add the following below the License
 * Header, with the fields enclosed by brackets [] replaced by your own
 * identifying information: "Portions Copyrighted [year]
 * [name of copyright owner]"
 *
 * Contributor(s):
 *
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */


package com.sun.facelets.tag.ui;

import com.sun.faces.util.Util;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.component.ValueHolder;
import com.sun.faces.util.Util.TreeTraversalCallback;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.context.FacesContext;

/**
 *
 * @author edburns
 */
public class CompositeComponentValue implements Serializable {
    
    transient UIComponent compositeRoot;
    
    /** Creates a new instance of CompositeComponentValue */
    public CompositeComponentValue(UIComponent compositeRoot) {
        this.compositeRoot = compositeRoot;
    }
    
    void setCompositeRoot(UIComponent compositeRoot) {
        this.compositeRoot = compositeRoot;
    }
    
    public Object getSubcomponentValue(String id) {
        Object result = null;
        UIComponent sub = getSubComponent(id);
        if (null != sub && sub instanceof ValueHolder) {
            result = ((ValueHolder)sub).getValue();
        }
        return result;
    }
    
    public UIComponent getSubComponent(String id) {
        UIComponent sub = compositeRoot.findComponent(id);
        return sub;
    }
    
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        
        TreeTraversalCallback cb = new TreeTraversalCallback() {

            public boolean takeActionOnNode(FacesContext context, UIComponent comp) throws FacesException {
                if ((comp != CompositeComponentValue.this.compositeRoot) && 
                    comp instanceof ValueHolder) {
                    sb.append((String) ((ValueHolder)comp).getValue() + " ");
                }
                return true;
            }
        };
        FacesContext context = FacesContext.getCurrentInstance();
        Util.prefixViewTraversal(context, compositeRoot, cb);
        
        return sb.toString();
        
    }

    /**
     * 
     * Two CompositeComponentValue instances are considered equal if
     * the list of child VauleHolder instances are the same instances.
     * 
     * This implementation uses a naieve algorithm due to battery life 
     * constraints.
     * 
     * @return
     */
    @Override
    public boolean equals(Object obj) {
        boolean result = false;
        
        final CompositeComponentValue other = (CompositeComponentValue) obj;
        final List<ValueHolder> thisValueHolders = new ArrayList<ValueHolder>();

        // Collect valueHolder list from this
        TreeTraversalCallback cb = new TreeTraversalCallback() {

            public boolean takeActionOnNode(FacesContext context, UIComponent comp) throws FacesException {
                if ((comp != CompositeComponentValue.this.compositeRoot) && 
                    comp instanceof ValueHolder) {
                    thisValueHolders.add((ValueHolder) comp);
                }
                return true;
            }
        };
        FacesContext context = FacesContext.getCurrentInstance();
        Util.prefixViewTraversal(context, compositeRoot, cb);
        
        cb = new TreeTraversalCallback() {

            public boolean takeActionOnNode(FacesContext context, UIComponent comp) throws FacesException {
                boolean keepGoing = true;
                
                if ((comp != other.compositeRoot) && 
                    comp instanceof ValueHolder) {
                    keepGoing = !thisValueHolders.contains((ValueHolder) comp);
                }
                
                return keepGoing;
            }
        };
        
        result = Util.prefixViewTraversal(context, compositeRoot, cb);

        return result;
    }
   
}
