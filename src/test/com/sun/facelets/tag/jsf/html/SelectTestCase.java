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
package com.sun.facelets.tag.jsf.html;

import javax.faces.component.UISelectOne;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

import com.sun.facelets.Facelet;
import com.sun.facelets.FaceletFactory;
import com.sun.facelets.FaceletTestCase;

public class SelectTestCase extends FaceletTestCase {

	public void testSelectOne() throws Exception {
		FaceletFactory ff = FaceletFactory.getInstance();
		Facelet f = ff.getFacelet("selectOne.xml");
		this.servletRequest.getSession().setAttribute("test", new TestBean());
		this.servletRequest.setParameter("testForm:alignment", "10");
		FacesContext faces = FacesContext.getCurrentInstance();
		UIViewRoot root = new UIViewRoot();
		f.apply(faces, root);
		UISelectOne one = (UISelectOne) root.findComponent("testForm:alignment");
		root.processDecodes(faces);
		root.processValidators(faces);
		System.out.println(faces.getMessages().hasNext());
	}

}
