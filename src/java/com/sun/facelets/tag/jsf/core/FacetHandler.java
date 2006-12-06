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

import javax.el.ELException;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;

import com.sun.facelets.FaceletContext;
import com.sun.facelets.FaceletException;
import com.sun.facelets.tag.TagAttribute;
import com.sun.facelets.tag.TagConfig;
import com.sun.facelets.tag.TagException;
import com.sun.facelets.tag.TagHandler;

/**
 * Register a named facet on the UIComponent associated with the closest parent
 * UIComponent custom action. <p/> See <a target="_new"
 * href="http://java.sun.com/j2ee/javaserverfaces/1.1_01/docs/tlddocs/f/facet.html">tag
 * documentation</a>.
 * 
 * @author Jacob Hookom
 * @version $Id: FacetHandler.java,v 1.7 2006/12/06 14:26:12 jhook Exp $
 */
public final class FacetHandler extends TagHandler {

	public static final String KEY = "facelets.FACET_NAME";

	protected final TagAttribute name;

	public FacetHandler(TagConfig config) {
		super(config);
		this.name = this.getRequiredAttribute("name");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sun.facelets.FaceletHandler#apply(com.sun.facelets.FaceletContext,
	 *      javax.faces.component.UIComponent)
	 */
	public void apply(FaceletContext ctx, UIComponent parent)
			throws IOException, FacesException, FaceletException, ELException {
		if (parent == null) {
			throw new TagException(this.tag, "Parent UIComponent was null");
		}
		parent.getAttributes().put(KEY, this.name.getValue(ctx));
		try {
			this.nextHandler.apply(ctx, parent);
		} finally {
			parent.getAttributes().remove(KEY);
		}
	}
}
