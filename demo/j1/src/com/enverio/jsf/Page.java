package com.enverio.jsf;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;

import com.sun.facelets.Facelets;
import com.sun.facelets.client.ClientWriter;
import com.sun.facelets.client.Effect;

public class Page {

    private final static Logger log = Logger.getLogger("demo.Page");

    public void highlight(ValueChangeEvent event) {
        FacesContext faces = FacesContext.getCurrentInstance();
        UIComponent c = event.getComponent();

        try {
            ClientWriter cw = Facelets.getClientWriter();
            cw.startScript().select(c, Effect.highlight()).endScript().close();
        } catch (Exception e) {
            log.log(Level.SEVERE, "Error in ValueChangeListener", e);
        } finally {
            faces.responseComplete();
        }
    }
}
