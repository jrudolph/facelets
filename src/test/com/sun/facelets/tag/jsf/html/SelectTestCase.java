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
