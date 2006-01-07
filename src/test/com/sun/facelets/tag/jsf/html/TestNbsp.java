package com.sun.facelets.tag.jsf.html;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

import javax.faces.component.UISelectOne;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

import com.sun.facelets.Facelet;
import com.sun.facelets.FaceletFactory;
import com.sun.facelets.FaceletTestCase;
import com.sun.facelets.mock.MockResponseWriter;

public class TestNbsp extends FaceletTestCase {

    public void testNbsp() throws Exception {
        FaceletFactory ff = FaceletFactory.getInstance();
        Facelet f = ff.getFacelet("nbsp.xml");
        FacesContext faces = FacesContext.getCurrentInstance();
        UIViewRoot root = new UIViewRoot();
        f.apply(faces, root);
        
        PrintWriter pw = new PrintWriter(System.out);
        MockResponseWriter rw = new MockResponseWriter(pw);
        faces.setResponseWriter(rw);
        
        root.encodeAll(faces);
        
        pw.close();
    }
}
