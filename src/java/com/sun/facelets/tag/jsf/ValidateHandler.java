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

import java.io.IOException;

import javax.el.ELException;
import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIComponent;
import javax.faces.validator.Validator;

import com.sun.facelets.FaceletContext;
import com.sun.facelets.FaceletException;
import com.sun.facelets.tag.MetaTagHandler;
import com.sun.facelets.tag.TagAttribute;
import com.sun.facelets.tag.TagConfig;
import com.sun.facelets.tag.TagException;
import com.sun.facelets.tag.MetaRuleset;
import com.sun.facelets.tag.composite.AttachedObjectTargetHandler;
import com.sun.facelets.tag.composite.RetargetableAttachedObjectHandler;
import javax.faces.application.Resource;

/**
 * Handles setting a Validator instance on a EditableValueHolder. Will wire all
 * attributes set to the Validator instance created/fetched. Uses the "binding"
 * attribute for grabbing instances to apply attributes to. <p/> Will only
 * set/create Validator is the passed UIComponent's parent is null, signifying
 * that it wasn't restored from an existing tree.
 * 
 * @author Jacob Hookom
 * @version $Id: ValidateHandler.java,v 1.3.26.2 2008/05/21 15:04:39 edburns Exp $
 */
public class ValidateHandler extends MetaTagHandler implements RetargetableAttachedObjectHandler {

    private final TagAttribute binding;
    
    private String validatorId;

    /**
     * 
     * @param config
     * @deprecated
     */
    public ValidateHandler(TagConfig config) {
        super(config);
        this.binding = this.getAttribute("binding");
    }
    
    public ValidateHandler(ValidatorConfig config) {
        this((TagConfig) config);
        this.validatorId = config.getValidatorId();
    }

    /**
     * TODO
     * 
     * @see com.sun.facelets.FaceletHandler#apply(com.sun.facelets.FaceletContext,
     *      javax.faces.component.UIComponent)
     */
    public final void apply(FaceletContext ctx, UIComponent parent)
            throws IOException, FacesException, FaceletException, ELException {

        // only process if it's been created
        if (null == parent || !(null == parent.getParent()))  {
            return;
        }
        
        if (parent instanceof EditableValueHolder) {
            applyAttachedObjectToComponent(ctx, parent);
        } else if (parent.getAttributes().containsKey(Resource.COMPONENT_RESOURCE_KEY)) {
            if (null == getId()) {
                // PENDING(): I18N
                throw new TagException(this.tag,
                        "validator tags nested within composite components must have a non-null ID attribute");
            }
            // Allow the composite component to know about the target
            // component.
            AttachedObjectTargetHandler.getRetargetableHandlers(parent).add(this);
        } else {
            throw new TagException(this.tag,
                    "Parent not an instance of EditableValueHolder: " + parent);
        }

    }

    /**
     * Template method for creating a Validator instance
     * 
     * @param ctx
     *            FaceletContext to use
     * @return a new Validator instance
     */
    protected Validator createValidator(FaceletContext ctx) {
        if (this.validatorId == null) {
            throw new TagException(
                    this.tag,
                    "Default behavior invoked of requiring a validator-id passed in the constructor, must override ValidateHandler(ValidatorConfig)");
        }
        return ctx.getFacesContext().getApplication().createValidator(
                this.validatorId);
    }

    @Override
    protected MetaRuleset createMetaRuleset(Class type) {
        return super.createMetaRuleset(type).ignore("binding");
    }

    public void applyAttachedObjectToComponent(FaceletContext ctx, UIComponent parent) {
            // cast to a ValueHolder
            EditableValueHolder evh = (EditableValueHolder) parent;
            ValueExpression ve = null;
            Validator v = null;
            if (this.binding != null) {
                ve = this.binding.getValueExpression(ctx, Validator.class);
                v = (Validator) ve.getValue(ctx);
            }
            if (v == null) {
                v = this.createValidator(ctx);
                if (ve != null) {
                    ve.setValue(ctx, v);
                }
            }
            if (v == null) {
                throw new TagException(this.tag, "No Validator was created");
            }
            this.setAttributes(ctx, v);
            evh.addValidator(v);
    }

    public String getId() {
        String result = null;
        TagAttribute attr = this.getAttribute("id");
        
        if (null != attr) {
            result = attr.getValue();
        }
        return result;
    }
    
    

}
