package com.sun.facelets.compiler;

import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import com.sun.facelets.Facelet;
import com.sun.facelets.FaceletFactory;
import com.sun.facelets.FaceletTestCase;
import com.sun.facelets.util.FastWriter;

public class WhitespaceTestCase extends FaceletTestCase {

    private UIComponent target;

    public void testSelectOneMenu() throws Exception {
        this.servletRequest.setAttribute("test", this);

        Facelet f = FaceletFactory.getInstance().getFacelet("selectOne.xml");

        FacesContext faces = FacesContext.getCurrentInstance();

        UIViewRoot root = faces.getViewRoot();
        f.apply(faces, root);
        
        assertNotNull("target binding", target);
        assertEquals("children", 2, this.target.getChildCount());

        FastWriter fw = new FastWriter();
        ResponseWriter rw = faces.getResponseWriter();
        rw = rw.cloneWithWriter(fw);
        faces.setResponseWriter(rw);
        root.encodeAll(faces);
        System.out.println(fw);
    }
    
    public void testPanelGrid() throws Exception {
        this.servletRequest.setAttribute("test", this);

        Facelet f = FaceletFactory.getInstance().getFacelet("panelGrid.xml");

        FacesContext faces = FacesContext.getCurrentInstance();

        UIViewRoot root = faces.getViewRoot();
        f.apply(faces, root);
        
        assertNotNull("target binding", target);
        assertEquals("children", 2, this.target.getChildCount());

        FastWriter fw = new FastWriter();
        ResponseWriter rw = faces.getResponseWriter();
        rw = rw.cloneWithWriter(fw);
        faces.setResponseWriter(rw);
        root.encodeAll(faces);
        System.out.println(fw);
    }

    protected void setUp() throws Exception {
        super.setUp();
        this.target = null;
    }

    public UIComponent getTarget() {
        return target;
    }

    public void setTarget(UIComponent target) {
        this.target = target;
    }

}
