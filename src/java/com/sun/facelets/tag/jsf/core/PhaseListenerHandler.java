package com.sun.facelets.tag.jsf.core;

import java.io.IOException;
import java.io.Serializable;

import javax.el.ELException;
import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
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

public class PhaseListenerHandler extends TagHandler {

	private final static class LazyPhaseListener implements PhaseListener,
			Serializable {

		private transient PhaseListener instance;

		private final String type;

		private final ValueExpression binding;

		public LazyPhaseListener(String type, ValueExpression binding) {
			this.type = type;
			this.binding = binding;
		}

		private PhaseListener getInstance() {
			if (this.instance == null) {
				FacesContext faces = FacesContext.getCurrentInstance();
				if (faces == null)
					return null;
				if (this.binding != null) {
					this.instance = (PhaseListener) binding.getValue(faces
							.getELContext());
				}
				if (this.instance == null && type != null) {
					try {
						this.instance = (PhaseListener) ReflectionUtil.forName(
								this.type).newInstance();
					} catch (Exception e) {
						throw new AbortProcessingException(
								"Couldn't Lazily instantiate PhaseListener", e);
					}
					if (this.binding != null) {
						binding.setValue(faces.getELContext(), this.instance);
					}
				}
			}
			return this.instance;
		}

		public void afterPhase(PhaseEvent event) {
			PhaseListener pl = this.getInstance();
			if (pl != null) {
				pl.afterPhase(event);
			}
		}

		public void beforePhase(PhaseEvent event) {
			PhaseListener pl = this.getInstance();
			if (pl != null) {
				pl.beforePhase(event);
			}
		}

		public PhaseId getPhaseId() {
			PhaseListener pl = this.getInstance();
			return (pl != null) ? pl.getPhaseId() : PhaseId.ANY_PHASE;
		}

	}

	private final TagAttribute type;

	private final TagAttribute binding;

	private final String listenerType;

	public PhaseListenerHandler(TagConfig config) {
		super(config);
		this.type = this.getAttribute("type");
		this.binding = this.getAttribute("binding");
		if (this.type != null) {
			if (!this.type.isLiteral()) {
				throw new TagAttributeException(this.type,
						"Must be a literal class name of type PhaseListener");
			} else {
				// test it out
				try {
					Class c = ReflectionUtil.forName(this.type.getValue());
				} catch (ClassNotFoundException e) {
					throw new TagAttributeException(this.type,
							"Couldn't qualify PhaseListener", e);
				}
			}
			this.listenerType = this.type.getValue();
		} else {
			this.listenerType = null;
		}
	}

	public void apply(FaceletContext ctx, UIComponent parent)
			throws IOException, FacesException, FaceletException, ELException {
		if (ComponentSupport.isNew(parent)) {
			UIViewRoot root = ComponentSupport.getViewRoot(ctx, parent);
			if (root == null) {
				throw new TagException(this.tag, "UIViewRoot not available");
			}
			ValueExpression b = null;
			if (this.binding != null) {
				b = this.binding.getValueExpression(ctx, PhaseListener.class);
			}

			PhaseListener pl = new LazyPhaseListener(this.listenerType, b);

			root.addPhaseListener(pl);
		}
	}
}
