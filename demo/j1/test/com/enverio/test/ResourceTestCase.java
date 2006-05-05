package com.enverio.test;

import java.sql.Date;

import javax.faces.application.ViewHandler;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

import com.sun.facelets.FaceletFactory;
import com.sun.facelets.FaceletTestCase;
import com.sun.facelets.resource.Resource;

public class ResourceTestCase extends FaceletTestCase {

    public void testResource() throws Exception {
        FaceletFactory ff = FaceletFactory.getInstance();
        FacesContext faces = FacesContext.getCurrentInstance();
        ViewHandler vh = faces.getApplication().getViewHandler();
        UIViewRoot root = faces.getApplication().getViewHandler().restoreView(faces, "/foo.jsp");
        
        Resource rsc = this.faceletConfig.createTagLibrary().createResource("/com/enverio/jsf/rsc/com.enverio.js");
        System.out.println(rsc);
        System.out.println(new Date(rsc.getLastModified()).toLocaleString());
    }

}
