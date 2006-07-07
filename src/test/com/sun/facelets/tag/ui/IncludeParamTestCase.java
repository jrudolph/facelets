package com.sun.facelets.tag.ui;

import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import com.sun.facelets.Facelet;
import com.sun.facelets.FaceletFactory;
import com.sun.facelets.FaceletTestCase;
import com.sun.facelets.FaceletViewHandler;
import com.sun.facelets.tag.jsf.ComponentSupport;
import com.sun.facelets.util.FastWriter;

public class IncludeParamTestCase extends FaceletTestCase {

    public void testCaching() throws Exception {
        Facelet f = FaceletFactory.getInstance().getFacelet("test1.xml");
        
        
        FacesContext faces = FacesContext.getCurrentInstance();
        
        UIViewRoot root = faces.getViewRoot();
        
        this.servletRequest.setAttribute("test", "test2.xml");
        f.apply(faces, root);
        
        FastWriter fw = new FastWriter();
        ResponseWriter rw = faces.getResponseWriter();
        rw = rw.cloneWithWriter(fw);
        faces.setResponseWriter(rw);
        root.encodeAll(faces);
        System.out.println(fw);
        
        ComponentSupport.removeTransient(root);
        
        this.servletRequest.setAttribute("test", "test3.xml");
        f.apply(faces, root);
        
        fw = new FastWriter();
        rw = faces.getResponseWriter();
        rw = rw.cloneWithWriter(fw);
        faces.setResponseWriter(rw);
        root.encodeAll(faces);
        System.out.println(fw);
    }

}
