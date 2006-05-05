package com.enverio.test;

import java.util.Map;

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
import com.sun.facelets.mock.MockResponseWriter;
import com.sun.facelets.util.FastWriter;
import com.sun.faces.lifecycle.LifecycleImpl;

public class SuggestTestCase extends FaceletTestCase {

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

        this.servletRequest.setParameter("sug", "ee");
        this.servletRequest.setHeader(EventPhaseListener.EVENT_PARAM,
                "sug,suggest");

        root.findComponent("sug").decode(faces);

        new EventPhaseListener().afterPhase(new PhaseEvent(faces,
                PhaseId.APPLY_REQUEST_VALUES, new LifecycleImpl()));
        System.out.println(fw);
    }

}
