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

package com.sun.facelets.tag.jsf;

import com.sun.facelets.FaceletContext;
import com.sun.facelets.FaceletHandler;
import com.sun.facelets.el.LegacyValueBinding;
import com.sun.facelets.tag.MetaRuleset;
import com.sun.facelets.tag.MetaTagHandler;
import com.sun.facelets.tag.Tag;
import com.sun.facelets.tag.TagAttribute;
import com.sun.facelets.tag.TagException;
import com.sun.facelets.tag.UserTagHandler;
import com.sun.facelets.tag.ui.*;
import com.sun.facelets.util.FacesAPI;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.el.ELException;
import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.application.Application;
import javax.faces.component.ActionSource;
import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.component.ValueHolder;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

public class Component2Handler extends MetaTagHandler {

    private final static Logger log = Logger
            .getLogger("facelets.tag.component2");
    
    private final TagAttribute binding;

    private final String componentType;

    private final TagAttribute id;
    
    private final String rendererType;

    public final static String Name = "component2";
    
    public Component2Handler(ComponentConfig config) {
        super(config);
        this.componentType = null != this.getAttribute("componentType") ? 
            this.getAttribute("componentType").toString() : Component2Ref.COMPONENT_TYPE;
        this.rendererType = null != this.getAttribute("rendererType") ? 
            this.getAttribute("rendererType").toString() : Component2Ref.RENDERER_TYPE;
        this.id = this.getAttribute("id");
        this.binding = this.getAttribute("binding");
    }
    
    public final void apply(FaceletContext ctx, UIComponent parent)
            throws IOException, FacesException, ELException {
        // make sure our parent is not null
        if (parent == null) {
            throw new TagException(this.tag, "Parent UIComponent was null");
        }

        // our id
        String id = ctx.generateUniqueId(this.tagId);

        // grab our component
        UIComponent c = ComponentSupport.findChildByTagId(parent, id);
        boolean componentFound = false;
        ctx.getFacesContext().getExternalContext().getRequestMap().put("Component2Handler", this);
        if (c != null) {
            componentFound = true;
            // mark all children for cleaning
            if (log.isLoggable(Level.FINE)) {
                log.fine(this.tag
                        + " Component["+id+"] Found, marking children for cleanup");
            }
            ComponentSupport.markForDeletion(c);
            
            try {
                this.tag.setFragmentFaceletHandler((FaceletHandler) ctx.getFacesContext().getELContext().getContext(UserTagHandler.class));
                this.tag.setNextFaceletHandler(this.nextHandler);
                ctx.getFacesContext().getELContext().putContext(FaceletContext.class, ctx);
                this.tag.applyFragment(ctx.getFacesContext(), c);
                this.tag.apply(ctx.getFacesContext(), c);
            }
            finally {
                this.tag.setFragmentFaceletHandler(null);
                this.tag.setNextFaceletHandler(null);
            }
        } else {
            c = this.createComponent(ctx);
            if (log.isLoggable(Level.FINE)) {
                log.fine(this.tag + " Component["+id+"] Created: "
                        + c.getClass().getName());
            }
            this.setAttributes(ctx, c);
            
            // mark it owned by a facelet instance
            c.getAttributes().put(ComponentSupport.MARK_CREATED, id);
            
            // assign our unique id
            String compositeComponentId;
            if (null != (compositeComponentId = getCompositeComponentId(ctx))) {
                c.setId(compositeComponentId);
            } else {
                UIViewRoot root = ComponentSupport.getViewRoot(ctx, parent);
                if (root != null) {
                    String uid = root.createUniqueId();
                    c.setId(uid);
                }
            }
            
            if (this.rendererType != null) {
                c.setRendererType(this.rendererType);
            }
            
            // hook method
            this.onComponentCreated(ctx, c, parent);

            // The next handler is called by the application!
        }
        

        // finish cleaning up orphaned children
        if (componentFound) {
            ComponentSupport.finalizeForDeletion(c);
            parent.getChildren().remove(c);
        }

        this.onComponentPopulated(ctx, c, parent);
        
        // add to the tree afterwards
        // this allows children to determine if it's
        // been part of the tree or not yet
        parent.getChildren().add(c);
        
        // If the component was created on this request...
        if (!componentFound) {
            // Do any attachedObject reparenting, if necessary.
            this.moveAttachedObjectsIfNecessary(ctx, (Component2Ref) c);
        }
        

        ctx.getFacesContext().getExternalContext().getRequestMap().remove("Component2Handler");
        
    }
    
    private TagAttribute getEnclosingTagAttribute(String attrName) {
        TagAttribute result = null;
        Map<String,Object> requestMap = FacesContext.getCurrentInstance().getExternalContext().getRequestMap();
        Tag userTag;

        if (null != (userTag = (Tag) requestMap.get(
                UserTagHandler.USER_TAG_REQUEST_ATTR_NAME))) {
            result = userTag.getAttributes().get(attrName);
        }

        return result;
    }
    
    private String getCompositeComponentId(FaceletContext ctx) {
        String result = null;
        TagAttribute innerId = getEnclosingTagAttribute("innerComponentId");
        // if one exists, see what is its value.
        if (null != innerId) {
            result = innerId.getValue(ctx);
        }
        if (null == result) {
            // If the component author assigned a suggested name for this
            // composite component
            if (this.id != null) {
                result = this.id.getValue(ctx);
            }
        }
        return result;
    }
    
    private Map<String, UIComponent> attachedObjectTargetMap;
    public Map<String, UIComponent> getAttachedObjectTargetMap() {
        if (null == attachedObjectTargetMap) {
            attachedObjectTargetMap = new HashMap<String, UIComponent>();
        }
        return attachedObjectTargetMap;
    } 
    
    private void moveAttachedObjectsIfNecessary(FaceletContext ctx, Component2Ref composite) {
        Map<String, UIComponent> map = getAttachedObjectTargetMap();
        Iterator<String> iter = map.keySet().iterator();
        UIComponent target;
        String outerId;
        while (iter.hasNext()) {
            target = map.get(outerId = iter.next());
            composite.moveAttachedObjectToTarget(outerId, target);
        }
        map.clear();
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
        this.tag.setFragmentFaceletHandler((FaceletHandler) ctx.getFacesContext().getELContext().getContext(UserTagHandler.class));
        this.tag.setNextFaceletHandler(this.nextHandler);
        ctx.getFacesContext().getELContext().putContext(FaceletContext.class, ctx);
        try {
            if (this.binding != null) {
                ValueExpression ve = this.binding.getValueExpression(ctx,
                        Object.class);
                if (FacesAPI.getVersion() >= 12) {
                    c = app.createComponent(ve, faces, this.componentType, this.tag);
                    if (c != null) {
                        // Make sure the component supports 1.2
                        if (FacesAPI.getComponentVersion(c) >= 12) {
                            c.setValueExpression("binding", ve);
                        } else {
                            ValueBinding vb = new LegacyValueBinding(ve);
                            c.setValueBinding("binding", vb);
                        }

                    }
                }
            } else {
                c = app.createComponent(this.componentType, this.tag);
            }
            TagAttribute enclosingValue = getEnclosingTagAttribute("targetValue");
            if (null != enclosingValue) {
                if (enclosingValue.isLiteral()) {
                    throw new TagException(this.tag, "Literal value not allowed for composite components");
                }
                try {
                    ValueExpression enclosingValueExpression = 
                            (ValueExpression) enclosingValue.getValueExpression(ctx, Object.class);
                    c.setValueExpression("targetValue", enclosingValueExpression);
                }
                catch (ClassCastException cce) {
                    throw new TagException(this.tag, "Unable to get ValueExpression from `value' attribute");
                }
            }
        }
        finally {
            this.tag.setFragmentFaceletHandler(null);
            this.tag.setNextFaceletHandler(null);
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

    protected MetaRuleset createMetaRuleset(Class type) {
        MetaRuleset m = super.createMetaRuleset(type);
        
        // ignore standard component attributes
        m.ignore("binding").ignore("id");
        
        // add auto wiring for attributes
        m.addRule(ComponentRule.Instance);
        
        // if it's an ActionSource
        if (ActionSource.class.isAssignableFrom(type)) {
            m.addRule(ActionSourceRule.Instance);
        }
        
        // if it's a ValueHolder
        if (ValueHolder.class.isAssignableFrom(type)) {
            m.addRule(ValueHolderRule.Instance);
            
            // if it's an EditableValueHolder
            if (EditableValueHolder.class.isAssignableFrom(type)) {
                m.ignore("submittedValue");
                m.ignore("valid");
                m.addRule(EditableValueHolderRule.Instance);
            }
        }
        
        return m;
    }
    
    /**
     * A hook method for allowing developers to do additional processing once Facelets
     * creates the component.  The 'setAttributes' method is still perferred, but this
     * method will provide the parent UIComponent before it's been added to the tree and
     * before any children have been added to the newly created UIComponent.
     * 
     * @param ctx
     * @param c
     * @param parent
     */
    protected void onComponentCreated(FaceletContext ctx, UIComponent c, UIComponent parent) {
        // do nothing
    }

    protected void onComponentPopulated(FaceletContext ctx, UIComponent c, UIComponent parent) {
        // do nothing
    }

    protected void applyNextHandler(FaceletContext ctx, UIComponent c) 
            throws IOException, FacesException, ELException {
        // first allow c to get populated
        this.nextHandler.apply(ctx, c);
    }
    

}
