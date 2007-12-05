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
import java.util.logging.Level;
import javax.el.ELContext;
import javax.faces.component.StateHolder;
import javax.faces.context.FacesContext;
import javax.faces.convert.ConverterException;
import javax.faces.validator.ValidatorException;

/**
 * Handles setting a Validator instance on a EditableValueHolder. Will wire all
 * attributes set to the Validator instance created/fetched. Uses the "binding"
 * attribute for grabbing instances to apply attributes to. <p/> Will only
 * set/create Validator is the passed UIComponent's parent is null, signifying
 * that it wasn't restored from an existing tree.
 * 
 * @author Jacob Hookom
 * @version $Id: ValidateHandler.java,v 1.3.16.1 2007/12/05 07:38:01 edburns Exp $
 */
public class ValidateHandler extends MetaTagHandler {

    private final TagAttribute binding;
    
    private TagAttribute validatorId;

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
        this.validatorId = this.getAttribute("validatorId");
    }

    /**
     * TODO
     * 
     * @see com.sun.facelets.FaceletHandler#apply(com.sun.facelets.FaceletContext,
     *      javax.faces.component.UIComponent)
     */
    public final void apply(FaceletContext ctx, UIComponent parent)
            throws IOException, FacesException, FaceletException, ELException {

        if (parent == null || !(parent instanceof EditableValueHolder)) {
            throw new TagException(this.tag,
                    "Parent not an instance of EditableValueHolder: " + parent);
        }

        // only process if it's been created
        if (parent.getParent() == null) {
            // cast to a ValueHolder
            EditableValueHolder evh = (EditableValueHolder) parent;
            ValueExpression bindingExpression = null;
            ValueExpression validatorIdExpression = null;
            Validator v = null;
            if (this.binding != null) {
                bindingExpression = this.binding.getValueExpression(ctx, 
                        Validator.class);
            }
            if (this.validatorId != null) {
                validatorIdExpression = this.validatorId.getValueExpression(ctx, 
                        String.class);
            }
            v = new BindingValidator(validatorIdExpression, bindingExpression);
            if (v == null) {
                throw new TagException(this.tag, "No Validator was created");
            }
            this.setAttributes(ctx, v);
            evh.addValidator(v);
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
        ValueExpression bindingExpression = null;
        ValueExpression validatorIdExpression = null;
        Validator v = null;
        if (this.binding != null) {
            bindingExpression = this.binding.getValueExpression(ctx,
                    Validator.class);
        }
        if (this.validatorId != null) {
            validatorIdExpression = this.validatorId.getValueExpression(ctx,
                    String.class);
        }
        if (validatorIdExpression != null && validatorIdExpression.isLiteralText()) {
            return createValidator(validatorIdExpression,
                                   bindingExpression,
                                   FacesContext.getCurrentInstance());
        } else {
            v = new BindingValidator(validatorIdExpression, bindingExpression);
        }
        return v;
    }

    protected MetaRuleset createMetaRuleset(Class type) {
        return super.createMetaRuleset(type).ignore("binding");
    }
    
    
    protected static Validator createValidator(ValueExpression validatorId,
                                               ValueExpression binding,
                                               FacesContext facesContext) {

        ELContext elContext = facesContext.getELContext();
        Validator validator = null;

        // If "binding" is set, use it to create a validator instance.
        if (binding != null) {
            try {
                validator = (Validator) binding.getValue(elContext);
                if (validator != null) {
                    return validator;
                }
            } catch (Exception e) {
                throw new FacesException(e);
            }
        }

        // If "validatorId" is set, use it to create the validator
        // instance.  If "validatorId" and "binding" are both set, store the
        // validator instance in the value of the property represented by
        // the ValueExpression 'binding'.
        if (validatorId != null) {
            try {
                String validatorIdVal = (String)
                     validatorId.getValue(elContext);
                validator = facesContext.getApplication()
                     .createValidator(validatorIdVal);
                if (validator != null && binding != null) {
                    binding.setValue(elContext, validator);
                }
            } catch (Exception e) {
                throw new FacesException(e);
            }
        }

        if (validator == null) {
            /***
            if (LOGGER.isLoggable(Level.WARNING)) {
                LOGGER.log(Level.WARNING,
                     MessageUtils.getExceptionMessageString(
                          MessageUtils.CANNOT_VALIDATE_ID,
                          validatorId != null ? validatorId.getExpressionString() : "",
                          binding != null ? binding.getExpressionString() : ""));
            }
             * ***/
        }

        return validator;

    }
    
    
    public static class BindingValidator implements Validator, StateHolder {

        private ValueExpression binding;
        private ValueExpression validatorId;

        // -------------------------------------------------------- Constructors

        /**
         * <p>Only used during state restoration</p>
         */
        public BindingValidator() { }


        public BindingValidator(ValueExpression validatorId,
                                ValueExpression binding) {

            this.validatorId = validatorId;
            this.binding = binding;

        }


        // -------------------------------------------- Methods from StateHolder

        private Object[] state;
        public Object saveState(FacesContext context) {

            if (state == null) {
                state = new Object[2];
            }
            state[0] = validatorId;
            state[1] = binding;

            return state;
            
        }

        public void restoreState(FacesContext context, Object state) {

            this.state = (Object[]) state;
            if (this.state != null) {
                this.validatorId = (ValueExpression) this.state[0];
                this.binding = (ValueExpression) this.state[1];
            }

        }

        public boolean isTransient() {

            return false;

        }

        public void setTransient(boolean newTransientValue) {
            //no-op
        }


        // ---------------------------------------------- Methods from Validator


        /**
         * <p>Perform the correctness checks implemented by this
         * {@link javax.faces.validator.Validator} against the specified {@link javax.faces.component.UIComponent}.
         * If any violations are found, a {@link javax.faces.validator.ValidatorException}
         * will be thrown containing the {@link javax.faces.application.FacesMessage} describing
         * the failure.
         *
         * @param context   FacesContext for the request we are processing
         * @param component UIComponent we are checking for correctness
         * @param value     the value to validate
         * @throws javax.faces.validator.ValidatorException
         *                              if validation fails
         * @throws NullPointerException if <code>context</code>
         *                              or <code>component</code> is <code>null</code>
         */
        public void validate(FacesContext context,
                             UIComponent component,
                             Object value)
        throws ValidatorException {


           Validator instance = createValidator(validatorId, binding, context);


            if (instance != null) {
                instance.validate(context, component, value);
            } else {
                throw new ConverterException(
                     "Can't create validator");
            }

        }

    }
    

}
