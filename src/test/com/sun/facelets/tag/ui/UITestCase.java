package com.sun.facelets.tag.ui;

import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

import com.sun.facelets.Facelet;
import com.sun.facelets.FaceletFactory;
import com.sun.facelets.FaceletTestCase;

public class UITestCase extends FaceletTestCase {

    public void testCompositionTemplate() throws Exception {
        FacesContext faces = FacesContext.getCurrentInstance();

        FaceletFactory f = FaceletFactory.getInstance();
        Facelet at = f.getFacelet("composition-template.xml");

        UIViewRoot root = faces.getViewRoot();
        at.apply(faces, root);
        
        
    }

}
