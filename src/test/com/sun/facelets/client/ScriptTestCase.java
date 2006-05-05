package com.sun.facelets.client;

import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import com.sun.facelets.FaceletTestCase;
import com.sun.facelets.util.FastWriter;

public class ScriptTestCase extends FaceletTestCase {

    public void testScripting() throws Exception {
        FacesContext faces = FacesContext.getCurrentInstance();
        
        FastWriter fw = new FastWriter();
        ResponseWriter rw = faces.getResponseWriter().cloneWithWriter(fw);
        faces.setResponseWriter(rw);
        ClientWriter cw = new ClientWriter(rw);
        cw.select("div#body .menuItem", Insertion.replace().start("span").event("click", Element.toggle()).text("Hello World").end("span"), Element.show());
        System.out.println(fw);
    }

}
