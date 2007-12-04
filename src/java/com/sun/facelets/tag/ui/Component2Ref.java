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

import javax.el.MethodExpression;
import javax.faces.component.ActionSource2;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.el.MethodBinding;
import javax.faces.event.ActionListener;
import javax.faces.event.FacesEvent;

public final class Component2Ref extends UIInput implements ActionSource2 {

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
        if (parent == null) {
            throw new IllegalStateException();
        } else {
            event.setSource(this);
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
    
}
