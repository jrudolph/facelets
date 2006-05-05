package com.sun.facelets.event;

import java.util.Map;

import javax.faces.context.FacesContext;

public class Events {

    public Events() {
        super();
    }
    
    public static EventCallback getEventCallback(FacesContext faces) {
        Map<String, String> p = faces.getExternalContext()
        .getRequestHeaderMap();

        String de = p.get("javax.faces.Event");
        if (de != null) {
            String[] ep = de.split(",");
            String clientId = ep[0];
            String method = (ep.length > 1) ? ep[1] : null;
            boolean immediate = (ep.length > 2) ? "immediate".equals(ep[2]) : false;
            
            return new EventCallback(clientId, method, immediate);
        }
        return null;
    }
}
