package com.sun.facelets.tag.jstl.core;

import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

import com.sun.facelets.Facelet;
import com.sun.facelets.FaceletFactory;
import com.sun.facelets.FaceletTestCase;
import com.sun.facelets.bean.Employee;

public final class JstlCoreTestCase extends FaceletTestCase {

    public void testIf() throws Exception {
        FacesContext faces = FacesContext.getCurrentInstance();
        Map session = faces.getExternalContext().getSessionMap();
        Employee e = new Employee();
        session.put("employee", e);

        FaceletFactory f = FaceletFactory.getInstance();
        Facelet at = f.getFacelet("if.xml");
        
        UIViewRoot root = faces.getViewRoot();

        // make sure the form is there
        e.setManagement(true);
        at.apply(faces, root);
        UIComponent c = root.findComponent("form");
        assertNotNull("form is null", c);
        
        // now make sure it isn't
        e.setManagement(false);
        at.apply(faces, root);
        c = root.findComponent("form");
        assertNull("form is not null", c);
    }

}
