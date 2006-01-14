package com.sun.facelets.tag.ui;

import java.io.StringWriter;

import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

import com.sun.facelets.Facelet;
import com.sun.facelets.FaceletFactory;
import com.sun.facelets.FaceletTestCase;
import com.sun.facelets.mock.MockResponseWriter;

public class DefineIncludeTestCase extends FaceletTestCase {

    public void testDefineInclude() throws Exception {
        FacesContext faces = FacesContext.getCurrentInstance();

        FaceletFactory f = FaceletFactory.getInstance();
        Facelet at = f.getFacelet("defineInclude.xml");

        UIViewRoot root = faces.getViewRoot();
        at.apply(faces, root);
        
        StringWriter sw = new StringWriter();
        MockResponseWriter mrw = new MockResponseWriter(sw);
        faces.setResponseWriter(mrw);
        root.encodeAll(faces);
        sw.flush();
        
        System.out.println("************************");
        System.out.println(sw.toString());
        System.out.println("************************");
    }

}
