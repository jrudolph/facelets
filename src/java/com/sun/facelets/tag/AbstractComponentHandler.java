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

package com.sun.facelets.tag;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.el.ELException;
import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.application.Application;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

import com.sun.facelets.FaceletContext;
import com.sun.facelets.el.LegacyValueBinding;
import com.sun.facelets.util.FacesAPI;

/**
 * Base class with utility methods for processing UIComponent instances
 * 
 * @author Jacob Hookom
 * @version $Id: AbstractComponentHandler.java,v 1.2 2005/07/13 02:18:57 adamwiner Exp $
 */
public abstract class AbstractComponentHandler extends ObjectHandler {

    protected final static Logger log = Logger
            .getLogger("facelets.tag.component");

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
     * A lighter-weight version of UIComponent's findChild.
     * 
     * @param parent
     *            parent to start searching from
     * @param id
     *            to match to
     * @return UIComponent found or null
     */
    protected static final UIComponent findChild(UIComponent parent, String id) {
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

    private final TagAttribute binding;

    protected final String componentType;

    private final TagAttribute id;

    protected final String rendererType;

    /**
     * @param cfg
     */
    public AbstractComponentHandler(ComponentConfig cfg) {
        super(cfg);
        this.componentType = cfg.getComponentType();
        this.rendererType = cfg.getRendererType();
        this.id = this.getAttribute("id");
        this.binding = this.getAttribute("binding");
    }

    /**
     * Method handles UIComponent tree creation in accordance with the JSF 1.2
     * spec.
     * <ol>
     * <li>First determines this UIComponent's id by calling
     * {@link #getId(FaceletContext) getId(FaceletContext)}.</li>
     * <li>Search the parent for an existing UIComponent of the id we just
     * grabbed</li>
     * <li>If found, {@link #markForDeletion(UIComponent) mark} its children
     * for deletion.</li>
     * <li>If <i>not</i> found, call
     * {@link #createComponent(FaceletContext) createComponent}.
     * <ol>
     * <li>Only here do we apply
     * {@link ObjectHandler#setAttributes(FaceletContext, Object) attributes}</li>
     * <li>Set the UIComponent's id</li>
     * <li>Set the RendererType of this instance</li>
     * </ol>
     * </li>
     * <li>Now apply the nextHandler, passing the UIComponent we've
     * created/found.</li>
     * <li>Now add the UIComponent to the passed parent</li>
     * <li>Lastly, if the UIComponent already existed (found), then
     * {@link #finalizeForDeletion(UIComponent) finalize} for deletion.</li>
     * </ol>
     * 
     * @see com.sun.facelets.FaceletHandler#apply(com.sun.facelets.FaceletContext,
     *      javax.faces.component.UIComponent)
     * 
     * @throws TagException
     *             if the UIComponent parent is null
     */
    public final void apply(FaceletContext ctx, UIComponent parent)
            throws IOException, FacesException, ELException {
        // make sure our parent is not null
        if (parent == null) {
            throw new TagException(this.tag, "Parent UIComponent was null");
        }

        // our id
        String id = this.getId(ctx);

        // grab our component
        UIComponent c = findChild(parent, id);
        boolean componentFound = false;
        if (c != null) {
            componentFound = true;
            // mark all children for cleaning
            if (log.isLoggable(Level.FINE)) {
                log.fine(this.tag
                        + " Component Found, marking children for cleanup");
            }
            markForDeletion(c);
        } else {
            c = this.createComponent(ctx);
            if (log.isLoggable(Level.FINE)) {
                log.fine(this.tag + " Component Created: "
                        + c.getClass().getName());
            }
            this.setAttributes(ctx, c);
            c.setId(id);
            if (this.rendererType != null) {
           	    c.setRendererType(this.rendererType);
           	}
        }

        // first allow c to get populated
        this.nextHandler.apply(ctx, c);

        // add to the tree afterwards
        // this allows children to determine if it's
        // been part of the tree or not yet
        parent.getChildren().add(c);

        // finish cleaning up orphaned children
        if (componentFound) {
            finalizeForDeletion(c);
        }
    }

    /**
     * If the binding attribute was specified, use that in conjuction with our
     * componentType String variable to call createComponent on the Application,
     * otherwise just pass the componentType String.
     * <p />
     * If the binding was used, then set the ValueExpression "binding" on the
     * created UIComponent.
     * 
     * @see Application#createComponent(javax.faces.el.ValueBinding,
     *      javax.faces.context.FacesContext, java.lang.String)
     * @see Application#createComponent(java.lang.String)
     * @param ctx
     *            FaceletContext to use in creating a component
     * @return
     */
    protected UIComponent createComponent(FaceletContext ctx) {
        UIComponent c = null;
        FacesContext faces = ctx.getFacesContext();
        Application app = faces.getApplication();
        if (this.binding != null) {
            ValueExpression vb = this.binding.getValueExpression(ctx,
                    Object.class);
            if (FacesAPI.getVersion() >= 12) { 
                c = app.createComponent(ve, faces, this.componentType);
                if (c != null) {
                    // Make sure the component supports 1.2
                    if (FacesAPI.getVersion(c) >= 12) {
                        c.setValueExpression("binding", ve);
                    } else {
                        ValueBinding vb = new LegacyValueBinding(ve);
                        c.setValueBinding("binding", vb);
                    }

                }
            }
            else {
                ValueBinding vb = new LegacyValueBinding(ve);
                c = app.createComponent(vb, faces, this.componentType);
                if (c != null) {
                    c.setValueBinding("binding", vb);
                }
            }
        } else {
            c = app.createComponent(this.componentType);
        }
        return c;
    }

    /**
     * If the id TagAttribute was specified, get it's value, otherwise generate
     * a unique id from our tagId.
     * 
     * @see TagAttribute#getValue(FaceletContext)
     * @param ctx
     *            FaceletContext to use
     * @return what should be a unique Id
     */
    protected String getId(FaceletContext ctx) {
        if (this.id != null) {
            return this.id.getValue(ctx);
        }
        return ctx.generateUniqueId(this.tagId);
    }

    /**
     * Both "binding" and "id" are handled
     * 
     * @see com.sun.facelets.tag.ObjectHandler#isAttributeHandled(java.lang.Object,
     *      java.lang.String)
     */
    protected boolean isAttributeHandled(Object obj, String n) {
        if ("binding".equals(n) || "id".equals(n)) {
            return true;
        }
        return super.isAttributeHandled(obj, n);
    }

}
