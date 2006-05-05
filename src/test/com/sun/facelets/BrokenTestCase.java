package com.sun.facelets;

import javax.faces.application.ViewHandler;
import javax.faces.context.FacesContext;

public class BrokenTestCase extends FaceletTestCase {

    public void testBroken() throws Exception {
        FacesContext faces = FacesContext.getCurrentInstance();
        ViewHandler vh = faces.getApplication().getViewHandler();
        System.out.println(vh.getActionURL(faces, "/org/dojotoolkit/dojo.core.jsf"));
    }

}
