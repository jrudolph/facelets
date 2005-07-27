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

package com.sun.facelets.tag.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;

import com.sun.facelets.FaceletContext;
import com.sun.facelets.FaceletHandler;
import com.sun.facelets.tag.TagAttribute;
import com.sun.facelets.tag.TagAttributeException;

/**
 * 
 * @author Jacob Hookom
 * @version $Id: ComponentSupport.java,v 1.1 2005/07/27 04:33:03 jhook Exp $
 */
public final class ComponentSupport {

    private final static String MARK_DELETED = "com.sun.facelets.MARK_DELETED";
    
    /**
     * Used in conjunction with markForDeletion where any UIComponent marked
     * will be removed.
     * 
     * @param c
     *            UIComponent to finalize
     */
    public static final void finalizeForDeletion(UIComponent c) {
        // remove any existing marks of deletion
        c.getAttributes().remove(MARK_DELETED);

        // finally remove any children marked as deleted
        int sz = c.getChildCount();
        if (sz > 0) {
            UIComponent cc = null;
            List cl = c.getChildren();
            while (--sz >= 0) {
                cc = (UIComponent) cl.get(sz);
                if (cc.getAttributes().containsKey(MARK_DELETED)) {
                    cl.remove(cc);
                }
            }
        }

        // remove any facets marked as deleted
        if (c.getFacets().size() > 0) {
            Collection col = c.getFacets().values();
            UIComponent fc;
            for (Iterator itr = col.iterator(); itr.hasNext();) {
                fc = (UIComponent) itr.next();
                if (fc.getAttributes().containsKey(MARK_DELETED)) {
                    itr.remove();
                }
            }
        }
    }
    

    /**
     * A lighter-weight version of UIComponent's findChild.
     * 
     * @param parent
     *            parent to start searching from
     * @param id
     *            to match to
     * @return UIComponent found or null
     */
    public static final UIComponent findChild(UIComponent parent, String id) {
        int sz = parent.getChildCount();
        if (sz > 0) {
            UIComponent c = null;
            List cl = parent.getChildren();
            while (--sz >= 0) {
                c = (UIComponent) cl.get(sz);
                if (id.equals(c.getId())) {
                    return c;
                }
            }
        }
        return null;
    }

    /**
     * According to JSF 1.2 tag specs, this helper method will use the
     * TagAttribute passed in determining the Locale intended.
     * 
     * @param ctx
     *            FaceletContext to evaluate from
     * @param attr
     *            TagAttribute representing a Locale
     * @return Locale found
     * @throws TagAttributeException
     *             if the Locale cannot be determined
     */
    public static final Locale getLocale(FaceletContext ctx, TagAttribute attr)
            throws TagAttributeException {
        Object obj = attr.getObject(ctx);
        if (obj instanceof Locale) {
            return (Locale) obj;
        }
        if (obj instanceof String) {
            String s = (String) obj;
            if (s.length() == 2) {
                return new Locale(s);
            }
            if (s.length() == 5) {
                return new Locale(s.substring(0, 2), s.substring(3, 5)
                        .toUpperCase());
            }
            if (s.length() >= 7) {
                return new Locale(s.substring(0, 2), s.substring(3, 5)
                        .toUpperCase(), s.substring(6, s.length()));
            }
            throw new TagAttributeException(attr, "Invalid Locale Specified: "
                    + s);
        } else {
            throw new TagAttributeException(attr,
                    "Attribute did not evaluate to a String or Locale: " + obj);
        }
    }

    /**
     * Tries to walk up the parent to find the UIViewRoot, if not found, then go
     * to FaceletContext's FacesContext for the view root.
     * 
     * @param ctx
     *            FaceletContext
     * @param parent
     *            UIComponent to search from
     * @return UIViewRoot instance for this evaluation
     */
    public static final UIViewRoot getViewRoot(FaceletContext ctx,
            UIComponent parent) {
        UIComponent c = parent;
        do {
            if (c instanceof UIViewRoot) {
                return (UIViewRoot) c;
            } else {
                c = c.getParent();
            }
        } while (c != null);
        return ctx.getFacesContext().getViewRoot();
    }

    /**
     * Marks all direct children and Facets with an attribute for deletion.
     * 
     * @see #finalizeForDeletion(UIComponent)
     * @param c
     *            UIComponent to mark
     */
    public static final void markForDeletion(UIComponent c) {
        // flag this component as deleted
        c.getAttributes().put(MARK_DELETED, Boolean.TRUE);

        // mark all children to be deleted
        int sz = c.getChildCount();
        if (sz > 0) {
            UIComponent cc = null;
            List cl = c.getChildren();
            while (--sz >= 0) {
                cc = (UIComponent) cl.get(sz);
                cc.getAttributes().put(MARK_DELETED, Boolean.TRUE);
            }
        }

        // mark all facets to be deleted
        if (c.getFacets().size() > 0) {
            Collection col = c.getFacets().values();
            UIComponent fc;
            for (Iterator itr = col.iterator(); itr.hasNext();) {
                fc = (UIComponent) itr.next();
                fc.getAttributes().put(MARK_DELETED, Boolean.TRUE);
            }
        }
    }
    
    /**
     * Determine if the passed component is not null and if it's new
     * to the tree.  This operation can be used for determining if attributes
     * should be wired to the component.
     * 
     * @param component the component you wish to modify
     * @return true if it's new
     */
    public final static boolean isNew(UIComponent component) {
        return component != null && component.getParent() == null;
    }
}
