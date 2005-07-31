package com.sun.facelets.tag.ui;

import java.util.HashMap;
import java.util.Map;

import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

import com.sun.facelets.Facelet;
import com.sun.facelets.FaceletFactory;
import com.sun.facelets.FaceletTestCase;
import com.sun.faces.util.DebugUtil;

public class UITestCase extends FaceletTestCase {

    public void testCompositionTemplate() throws Exception {
        FacesContext faces = FacesContext.getCurrentInstance();

        FaceletFactory f = FaceletFactory.getInstance();
        Facelet at = f.getFacelet("composition-template.xml");

        UIViewRoot root = faces.getViewRoot();
        at.apply(faces, root);
    }
    
    public void testCompositionTemplateSimple() throws Exception {
        FacesContext faces = FacesContext.getCurrentInstance();

        FaceletFactory f = FaceletFactory.getInstance();
        Facelet at = f.getFacelet("composition-template-simple.xml");

        UIViewRoot root = faces.getViewRoot();
        at.apply(faces, root);
    }
    
    public void testComponent() throws Exception {
        FacesContext faces = FacesContext.getCurrentInstance();
        Map map = new HashMap();
        faces.getExternalContext().getRequestMap().put("map", map);

        FaceletFactory f = FaceletFactory.getInstance();
        Facelet at = f.getFacelet("component.xml");

        UIViewRoot root = faces.getViewRoot();
        at.apply(faces, root);
        
        assertEquals("only one child, the component", 1, root.getChildCount());
        assertNotNull("bound to map", map.get("c"));
    }
    
    public void testComponentClient() throws Exception {
        FacesContext faces = FacesContext.getCurrentInstance();
        Map map = new HashMap();
        faces.getExternalContext().getRequestMap().put("map", map);

        FaceletFactory f = FaceletFactory.getInstance();
        Facelet at = f.getFacelet("component-client.xml");

        UIViewRoot root = faces.getViewRoot();
        at.apply(faces, root);
        
        assertEquals("only one child, the component", 1, root.getChildCount());
        assertNotNull("bound to map", map.get("c"));
    }

}
