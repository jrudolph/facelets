package com.sun.facelets.tag.jsf.html;

import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import com.sun.facelets.Facelet;
import com.sun.facelets.FaceletFactory;
import com.sun.facelets.FaceletTestCase;
import com.sun.facelets.bean.Example;
import com.sun.facelets.util.FastWriter;

public class DataTableTestCase extends FaceletTestCase {

    public void testDataTable() throws Exception {
Facelet f = FaceletFactory.getInstance().getFacelet("dataTable.xml");
        
        FacesContext faces = FacesContext.getCurrentInstance();
        faces.getExternalContext().getRequestMap().put("company", Example.createCompany());
        
        
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
