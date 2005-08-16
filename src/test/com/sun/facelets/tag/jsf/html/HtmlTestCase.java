package com.sun.facelets.tag.jsf.html;

import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

import com.sun.facelets.Facelet;
import com.sun.facelets.FaceletFactory;
import com.sun.facelets.FaceletTestCase;

public class HtmlTestCase extends FaceletTestCase {
    
    public void testCommandButton() throws Exception {
        FacesContext faces = FacesContext.getCurrentInstance();

        FaceletFactory f = FaceletFactory.getInstance();
        Facelet at = f.getFacelet("commandButton.xml");
        
        UIViewRoot root = faces.getViewRoot();
        at.apply(faces, root);
        
        UIComponent c = root.findComponent("form:button");
        assertNotNull("button", c);
        
        Object v = c.getAttributes().get("id");
        assertEquals("id", "button", v);
    }

    public void testPanelGrid() throws Exception {
        FacesContext faces = FacesContext.getCurrentInstance();

        FaceletFactory f = FaceletFactory.getInstance();
        Facelet at = f.getFacelet("panelGrid.xml");

        UIViewRoot root = faces.getViewRoot();
        at.apply(faces, root);
    }

}
