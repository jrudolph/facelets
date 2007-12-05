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

package com.sun.facelets.tag.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.el.MethodExpression;
import javax.faces.component.ActionSource;
import javax.faces.component.ActionSource2;
import javax.faces.component.EditableValueHolder;
import javax.faces.component.StateHolder;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.el.MethodBinding;
import javax.faces.event.ActionListener;
import javax.faces.event.FacesEvent;
import javax.faces.event.FacesListener;
import javax.faces.event.ValueChangeListener;
import javax.faces.validator.Validator;

public final class Component2Ref extends UIInput implements ActionSource2, StateHolder {

    public final static String COMPONENT_TYPE = "facelets.ui.Component2Ref";
    public final static String COMPONENT_FAMILY = "facelets";
    public final static String RENDERER_TYPE = "javax.faces.Text";
    
    public Component2Ref() {
        super();
    }

    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    public String getRendererType() {
        return RENDERER_TYPE;
    }

    public void queueEvent(FacesEvent event) {

        if (event == null) {
            throw new NullPointerException();
        }
        UIComponent parent = getParent();
        UIComponent source = event.getComponent();
        if (parent == null) {
            throw new IllegalStateException();
        } else {
            // If this event originated from a source whose listener was 
            // reparented, take no action
            if (!getNoChangeSourceClientIds().contains(source.getClientId(FacesContext.getCurrentInstance())))
{
                event.setSource(this);
            }
            parent.queueEvent(event);
        }

    }
    
    
    
    // ActionSource2
    
    private MethodExpression actionExpression;

    public void setActionExpression(MethodExpression actionExpression) {
        this.actionExpression = actionExpression;
    }

    public MethodExpression getActionExpression() {
        return actionExpression;
    }
    
    // ActionSource

    @SuppressWarnings("deprecation")
    public MethodBinding getAction() {
        throw new IllegalStateException("Method Not Supported in JSF 2.0");
    }

    @SuppressWarnings("deprecation")
    public void setAction(MethodBinding action) {
        throw new IllegalStateException("Method Not Supported in JSF 2.0");
    }

    private MethodBinding actionListener;

    @SuppressWarnings("deprecation")
    public MethodBinding getActionListener() {
        return this.actionListener;
    }

    @SuppressWarnings("deprecation")
    public void setActionListener(MethodBinding actionListener) {
        this.actionListener = actionListener;
    }

    private boolean immediate;

    public boolean isImmediate() {
        return this.immediate;
    }

    public void setImmediate(boolean immediate) {
        this.immediate = immediate;
    }

    
    public void addActionListener(ActionListener listener) {

        addFacesListener(listener);

    }
    
    public ActionListener[] getActionListeners() {

        ActionListener al[] = (ActionListener [])
	    getFacesListeners(ActionListener.class);
        return (al);

    }

    public void removeActionListener(ActionListener listener) {

        removeFacesListener(listener);

    }

    public Object getSubmittedValue() {
        Object result = new CompositeComponentValue(this);
        return result;
    }
    
    private transient Map<String, Object> namedAttachedObjects;
    
    private Map<String, Object> getNamedAttachedObjects() {
        if (null == namedAttachedObjects) {
            namedAttachedObjects = new HashMap<String, Object>();
        }
        return namedAttachedObjects;
    }
    
    public void storeNamedAttachedObject(String id, Object attachedObject) {
        Map<String, Object> map = getNamedAttachedObjects();
        map.put(id , attachedObject);
    }
    
    private List<String> noChangeSourceClientIds;
    
    private List<String> getNoChangeSourceClientIds() {
        if (null == noChangeSourceClientIds) {
            noChangeSourceClientIds = new ArrayList<String>();
        }
        return noChangeSourceClientIds;
    }
    
    public void moveAttachedObjectToTarget(String attachedObjectId, 
            UIComponent target) {
        Map<String, Object> map = getNamedAttachedObjects();
        FacesContext context = FacesContext.getCurrentInstance();
        Object attachedObject = map.get(attachedObjectId);
        map.remove(attachedObjectId);
        ValueChangeListener valueChangeListener;
        ActionListener actionListener;
        Validator validator;
        Converter converter;
        EditableValueHolder editableValueHolder;
        boolean didMoveAttachedObject = false;
        
        if (null != attachedObject) {
            // Take appropriate action based on the type of the attached object
            
            if (attachedObject instanceof FacesListener) {
                FacesListener[] listeners =
                    this.getFacesListeners(FacesListener.class);
                
                if (attachedObject instanceof ValueChangeListener) {
                    valueChangeListener = (ValueChangeListener) attachedObject;
                    for (FacesListener cur : listeners) {
                        if (cur == attachedObject) {
                            this.removeValueChangeListener(valueChangeListener);
                            if (target instanceof EditableValueHolder) {
                                ((EditableValueHolder)target).addValueChangeListener(valueChangeListener);
                                didMoveAttachedObject = true;
                            }
                        }
                    }
                }
                
                if (attachedObject instanceof ActionListener) {
                    actionListener = (ActionListener) attachedObject;
                    for (FacesListener cur : listeners) {
                        if (cur == attachedObject) {
                            this.removeActionListener(actionListener);
                            if (target instanceof ActionSource) {
                                ((ActionSource)target).addActionListener(actionListener);
                                didMoveAttachedObject = true;
                            }
                        }
                    }
                }
            }
            else {
                if (target instanceof EditableValueHolder) {
                    editableValueHolder = (EditableValueHolder) target;
                    
                    if (attachedObject instanceof Validator) {
                        validator = (Validator) attachedObject;
                        this.removeValidator(validator);
                        editableValueHolder.addValidator(validator);
                        didMoveAttachedObject = true;
                    }
                    if (attachedObject instanceof Converter) {
                        converter = (Converter) attachedObject;
                        this.setConverter(null);
                        editableValueHolder.setConverter(converter);
                        didMoveAttachedObject = true;
                    }
                }
            }
        }
        if (didMoveAttachedObject) {
            getNoChangeSourceClientIds().add(target.getClientId(context));
        }
    }
    
    private Object[] values;

    @Override
    public void restoreState(FacesContext context, Object state) {
        values = (Object[]) state;
        super.restoreState(context, values[0]);    
        actionExpression =
	    (MethodExpression) restoreAttachedState(context, values[1]);
        immediate = (Boolean) values[2];          
        noChangeSourceClientIds = (List<String>) restoreAttachedState(context, 
                values[3]);
    }

    @Override
    public Object saveState(FacesContext context) {
        if (values == null) {
             values = new Object[4];
        }
      
        values[0] = super.saveState(context);
        values[1] = saveAttachedState(context, actionExpression);
        values[2] = immediate;
        values[3] = saveAttachedState(context, getNoChangeSourceClientIds());
        
        return (values);

    }
    
    
    
}
