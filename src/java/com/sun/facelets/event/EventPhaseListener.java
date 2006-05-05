package com.sun.facelets.event;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;

public class EventPhaseListener implements PhaseListener {

    public static final String EVENT_PARAM = "javax.faces.Event";

    private final Logger log = Logger.getLogger("facelets.Event");

    public EventPhaseListener() {
        super();
    }

    public void afterPhase(PhaseEvent event) {
        FacesContext faces = event.getFacesContext();
        EventCallback c = Events.getEventCallback(faces);
        if (c != null && c.isImmediate()) {
            if (log.isLoggable(Level.FINE)) {
                log.fine(c.toString());
            }
            try {
                c.invoke(faces);
            } catch (AbortProcessingException e) {
                throw e;
            } catch (Exception e) {
                log.log(Level.SEVERE, c.toString() + " threw Exception", e);
            } finally {
                faces.responseComplete();
            }
        }
    }

    public void beforePhase(PhaseEvent event) {
        // do nothing
    }

    public PhaseId getPhaseId() {
        return PhaseId.APPLY_REQUEST_VALUES;
    }

}
