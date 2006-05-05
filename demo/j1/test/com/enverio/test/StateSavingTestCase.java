package com.enverio.test;

import java.util.concurrent.ThreadPoolExecutor;

import javax.faces.application.StateManager;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;

import com.enverio.jsf.EmpNameConverter;
import com.enverio.model.Company;
import com.sun.facelets.Facelet;
import com.sun.facelets.FaceletFactory;
import com.sun.facelets.FaceletTestCase;
import com.sun.facelets.event.EventPhaseListener;
import com.sun.facelets.util.FastWriter;
import com.sun.faces.lifecycle.LifecycleImpl;

public class StateSavingTestCase extends FaceletTestCase {

    public void testSuggest() throws Exception {
        FaceletFactory ff = FaceletFactory.getInstance();
        Facelet f = ff.getFacelet("suggest.xhtml");

        FacesContext faces = FacesContext.getCurrentInstance();

        Company c = new Company();
        this.servletRequest.setAttribute("company", c);
        this.servletContext
                .setAttribute("employeeName", new EmpNameConverter());

        UIViewRoot root = faces.getViewRoot();
        f.apply(faces, root);

        FastWriter fw = new FastWriter();
        ResponseWriter rw = faces.getResponseWriter().cloneWithWriter(fw);
        // MockResponseWriter mrw = new MockResponseWriter(fw);
        faces.setResponseWriter(rw);
        root.encodeAll(faces);
        System.out.println(fw);

        fw.reset();

        StateManager sm = faces.getApplication().getStateManager();
        
        long total = 403;
        for (int i = 0; i < 5000; i++) {
            Object obj = sm.saveView(faces);
            
        }
    }

}
