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
import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ValueChangeEvent;
import javax.faces.event.ValueChangeListener;

import com.sun.facelets.FaceletContext;
import com.sun.facelets.FaceletException;
import com.sun.facelets.tag.TagAttribute;
import com.sun.facelets.tag.TagAttributeException;
import com.sun.facelets.tag.TagConfig;
import com.sun.facelets.tag.TagException;
import com.sun.facelets.tag.TagHandler;
import com.sun.facelets.tag.jsf.ComponentSupport;
import com.sun.facelets.tag.ui.Component2Ref;
import com.sun.facelets.util.ReflectionUtil;

/**
 * Register an ValueChangeListener instance on the UIComponent associated with
 * the closest parent UIComponent custom action.<p/> See <a target="_new"
 * href="http://java.sun.com/j2ee/javaserverfaces/1.1_01/docs/tlddocs/f/valueChangeListener.html">tag
 * documentation</a>.
 * 
 * @author Jacob Hookom
 * @version $Id: ValueChangeListenerHandler.java,v 1.2 2005/08/24 04:38:50 jhook
 *          Exp $
 */
public final class ValueChangeListenerHandler extends TagHandler {

	private static class LazyValueChangeListener implements
			ValueChangeListener, Serializable {
		private transient ValueChangeListener instance;

		private final String type;

		private final ValueExpression binding;

		public LazyValueChangeListener(String type, ValueExpression binding) {
			this.type = type;
			this.binding = binding;
		}

		public void processValueChange(ValueChangeEvent event)
				throws AbortProcessingException {
			if (this.instance == null) {
				FacesContext faces = FacesContext.getCurrentInstance();
				if (faces == null)
					return;
				if (this.binding != null) {
					this.instance = (ValueChangeListener) binding
							.getValue(faces.getELContext());
				}
				if (this.instance == null && this.type != null) {
					try {
						this.instance = (ValueChangeListener) ReflectionUtil
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
				this.instance.processValueChange(event);
			}
		}
	}

	private final TagAttribute type;

	private final TagAttribute binding;

	private final String listenerType;

	public ValueChangeListenerHandler(TagConfig config) {
		super(config);
		this.binding = this.getAttribute("binding");
		this.type = this.getAttribute("type");
		if (this.type != null) {
			if (!this.type.isLiteral()) {
				throw new TagAttributeException(this.type,
						"Must be a literal class name of type ValueChangeListener");
			} else {
				// test it out
				try {
					Class c = ReflectionUtil.forName(this.type.getValue());
				} catch (ClassNotFoundException e) {
					throw new TagAttributeException(this.type,
							"Couldn't qualify ValueChangeListener", e);
				}
			}
			this.listenerType = this.type.getValue();
		} else {
			this.listenerType = null;
		}
	}

	/**
	 * See taglib documentation.
	 * 
	 * @see com.sun.facelets.FaceletHandler#apply(com.sun.facelets.FaceletContext,
	 *      javax.faces.component.UIComponent)
	 */
	public void apply(FaceletContext ctx, UIComponent parent)
			throws IOException, FacesException, FaceletException, ELException {
		if (parent instanceof EditableValueHolder) {
			if (ComponentSupport.isNew(parent)) {
				EditableValueHolder evh = (EditableValueHolder) parent;
				ValueExpression b = null;
				if (this.binding != null) {
					b = this.binding.getValueExpression(ctx, ValueChangeListener.class);
				}
				ValueChangeListener listener = new LazyValueChangeListener(
						this.listenerType, b);
				evh.addValueChangeListener(listener);
                                if (parent instanceof Component2Ref) {
                                    String attachedObjectId = this.getAttribute("id").getValue(ctx);
                                    ((Component2Ref)parent).storeNamedAttachedObject(attachedObjectId, listener);
                                }
			}
		} else {
			throw new TagException(this.tag,
					"Parent is not of type EditableValueHolder, type is: "
							+ parent);
		}
	}

}
