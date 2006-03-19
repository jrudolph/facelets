package com.sun.facelets;

import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import com.sun.facelets.mock.MockResponseWriter;
import com.sun.facelets.util.FastWriter;

public class InstructionsTestCase extends FaceletTestCase {

    public void testInstructions() throws Exception {
        Facelet f = FaceletFactory.getInstance().getFacelet("instructions.xhtml");
        
        FacesContext faces = FacesContext.getCurrentInstance();
        UIViewRoot root = faces.getViewRoot();
        f.apply(faces, root);
        
        FastWriter fw = new FastWriter();
        ResponseWriter rw = faces.getResponseWriter();
        rw = rw.cloneWithWriter(fw);
        faces.setResponseWriter(rw);
        root.encodeAll(faces);
        System.out.println(fw);
    }

}
