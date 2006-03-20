package com.sun.facelets.tag.ui;

import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import com.sun.facelets.Facelet;
import com.sun.facelets.FaceletFactory;
import com.sun.facelets.FaceletTestCase;
import com.sun.facelets.bean.Company;
import com.sun.facelets.bean.Example;
import com.sun.facelets.util.FastWriter;

public class RepeatTestCase extends FaceletTestCase {

    public void testRepeat() throws Exception {
        Facelet f = FaceletFactory.getInstance().getFacelet("repeat.xml");
        FacesContext faces = FacesContext.getCurrentInstance();
        
        Company c = Example.createCompany();
        faces.getExternalContext().getRequestMap().put("company", c);
        
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
