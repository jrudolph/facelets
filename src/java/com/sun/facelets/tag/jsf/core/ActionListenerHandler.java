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

package com.sun.facelets.tag.jsf.core;

import java.io.IOException;
import java.io.Serializable;

import javax.el.ELException;
import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.component.ActionSource;
import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;
import javax.faces.event.ValueChangeListener;

import com.sun.facelets.FaceletContext;
import com.sun.facelets.FaceletException;
import com.sun.facelets.tag.TagAttribute;
import com.sun.facelets.tag.TagAttributeException;
import com.sun.facelets.tag.TagConfig;
import com.sun.facelets.tag.TagException;
import com.sun.facelets.tag.TagHandler;
import com.sun.facelets.tag.jsf.ComponentSupport;
import com.sun.facelets.util.ReflectionUtil;

/**
 * Register an ActionListener instance on the UIComponent associated with the
 * closest parent UIComponent custom action. <p/> See <a target="_new"
 * href="http://java.sun.com/j2ee/javaserverfaces/1.1_01/docs/tlddocs/f/actionListener.html">tag
 * documentation</a>.
 * 
 * @see javax.faces.event.ActionListener
 * @see javax.faces.component.ActionSource
 * @author Jacob Hookom
 * @version $Id: ActionListenerHandler.java,v 1.3.6.1 2006/11/10 06:26:05 jhook Exp $
 */
public final class ActionListenerHandler extends TagHandler {
	
	private final static class LazyActionListener implements ActionListener, Serializable {
		private transient ActionListener instance;
		private final String type;
		private final ValueExpression binding;
		
		public LazyActionListener(String type, ValueExpression binding) {
			this.type = type;
			this.binding = binding;
		}

		public void processAction(ActionEvent event) throws AbortProcessingException {
			if (this.instance == null) {
				FacesContext faces = FacesContext.getCurrentInstance();
				if (faces == null)
					return;
				if (this.binding != null) {
					this.instance = (ActionListener) binding
							.getValue(faces.getELContext());
				}
				if (this.instance == null && this.type != null) {
					try {
						this.instance = (ActionListener) ReflectionUtil
								.forName(this.type).newInstance();
					} catch (Exception e) {
						throw new AbortProcessingException(
								"Couldn't Lazily instantiate ValueChangeListener",
								e);
					}
					if (this.binding != null) {
						binding.setValue(faces.getELContext(), this.instance);
					}
				}
			}
			if (this.instance != null) {
				this.instance.processAction(event);
			}
		}
	}

	private final TagAttribute type;

	private final TagAttribute binding;

	private final String listenerType;

    /**
     * @param config
     */
    public ActionListenerHandler(TagConfig config) {
        super(config);
        this.binding = this.getAttribute("binding");
        this.type = this.getAttribute("type");
        if (this.type != null) {
			if (!this.type.isLiteral()) {
				throw new TagAttributeException(this.type,
						"Must be a literal class name of type ActionListener");
			} else {
				// test it out
				try {
					Class c = ReflectionUtil.forName(this.type.getValue());
				} catch (ClassNotFoundException e) {
					throw new TagAttributeException(this.type,
							"Couldn't qualify ActionListener", e);
				}
			}
			this.listenerType = this.type.getValue();
		} else {
			this.listenerType = null;
		}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sun.facelets.FaceletHandler#apply(com.sun.facelets.FaceletContext,
     *      javax.faces.component.UIComponent)
     */
    public void apply(FaceletContext ctx, UIComponent parent)
            throws IOException, FacesException, FaceletException, ELException {
        if (parent instanceof ActionSource) {
        	if (ComponentSupport.isNew(parent)) {
				ActionSource as = (ActionSource) parent;
				ValueExpression b = null;
				if (this.binding != null) {
					b = this.binding.getValueExpression(ctx, ActionListener.class);
				}
				ActionListener listener = new LazyActionListener(this.listenerType, b);
				as.addActionListener(listener);
			}
        } else {
            throw new TagException(this.tag,
                    "Parent is not of type ActionSource, type is: " + parent);
        }
    }
}
