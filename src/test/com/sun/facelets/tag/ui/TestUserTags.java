package com.sun.facelets.tag.ui;

import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

import com.sun.facelets.Facelet;
import com.sun.facelets.FaceletFactory;
import com.sun.facelets.FaceletTestCase;
import com.sun.facelets.mock.MockResponseWriter;
import com.sun.facelets.util.FastWriter;

public class TestUserTags extends FaceletTestCase {

    public void testClientClient() throws Exception {
        FaceletFactory ff = FaceletFactory.getInstance();
        FacesContext faces = FacesContext.getCurrentInstance();
        
        Facelet f = ff.getFacelet("test-tags.xml");
        
        this.servletRequest.setAttribute("test", "foo");
        
        UIViewRoot root = faces.getViewRoot();
        f.apply(faces, root);
        
        FastWriter fw = new FastWriter();
        MockResponseWriter mrw = new MockResponseWriter(fw);
        faces.setResponseWriter(mrw);
        root.encodeAll(faces);
        System.out.println(fw);
    }

}
