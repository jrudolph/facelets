package com.sun.facelets.component;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.faces.FacesException;
import javax.faces.component.ContextCallback;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

public class UIFacelet extends UIViewRoot {

    private static final Logger log = Logger.getLogger("facelets.Avatar");

    public static final ContextCallback Update = new ContextCallback() {
        public void invokeContextCallback(FacesContext faces, UIComponent c) {
            c.processUpdates(faces);
        }
    };

    public static final ContextCallback Validate = new ContextCallback() {
        public void invokeContextCallback(FacesContext faces, UIComponent c) {
            c.processValidators(faces);
        }
    };

    public static final ContextCallback Encode = new ContextCallback() {
        public void invokeContextCallback(FacesContext faces, UIComponent c) {
            try {
                c.encodeAll(faces);
            } catch (IOException e) {
                throw new FacesException(e);
            }
        }
    };

    public static final String PARAM_ENCODE = "javax.faces.Encode";

    public static final String PARAM_UPDATE = "javax.faces.Update";

    private transient Set<String> encode = new HashSet<String>();

    private transient Set<String> update = new HashSet<String>();

    public UIFacelet() {
        super();
    }

    private void process(FacesContext faces, Set<String> ids, ContextCallback c) {
        UIViewRoot root = faces.getViewRoot();
        boolean success = false;
        for (String id : ids) {
            success = root.invokeOnComponent(faces, id, c);
            if (!success) {
                log.warning(id + " not found");
            }
        }
    }

    public void processUpdates(FacesContext faces) {
        if (!this.update.isEmpty()) {
            this.process(faces, this.update, Update);
        } else {
            super.processUpdates(faces);
        }
    }

    public void processValidators(FacesContext faces) {
        if (!this.update.isEmpty()) {
            this.process(faces, this.update, Validate);
        } else {
            super.processValidators(faces);
        }
    }

    public void encodeAll(FacesContext faces) throws IOException {
        if (!this.encode.isEmpty()) {
            ExternalContext ctx = faces.getExternalContext();
            Object resp = ctx.getResponse();
            if (resp instanceof HttpServletResponse) {
                ((HttpServletResponse) resp).addHeader("Cache-Control", "no-cache");
            }
            
            UIViewRoot root = faces.getViewRoot();
            boolean success = false;
            ResponseWriter rw = faces.getResponseWriter();
            rw.startElement("response", this);
            try {
                for (String id : this.encode) {
                    rw.startElement("encode", this);
                    rw.writeAttribute("id", id, "id");
                    success = root.invokeOnComponent(faces, id, Encode);
                    rw.endElement("encode");
                    if (!success) {
                        log.warning(id + " not found");
                    }
                }
//                rw.startElement("view-state", this);
//                faces.getApplication().getViewHandler().writeState(faces);
//                rw.endElement("view-state");
                rw.endElement("response");
            } catch (FacesException e) {
                if (e.getCause() instanceof IOException) {
                    throw (IOException) e.getCause();
                } else {
                    throw e;
                }
            }
        } else {
            super.encodeAll(faces);
        }
    }

    public void decode(FacesContext faces) {
        ExternalContext ctx = faces.getExternalContext();

        // get these parameters
        Map<String, String[]> params = ctx.getRequestParameterValuesMap();
        String[] update = params.get(PARAM_UPDATE);
        String[] encode = params.get(PARAM_ENCODE);

        if (update != null && update.length > 0) {
            for (String id : update) {
                this.update.add(id);
            }
        }

        if (encode != null && encode.length > 0) {
            for (String id : encode) {
                this.encode.add(id);
            }
            this.getAttributes().put("contentType", "text/xml");
        }

        super.decode(faces);
    }

    public Set<String> getEncodeIdSet() {
        return this.encode;
    }

    public Set<String> getUpdateIdSet() {
        return this.update;
    }

}
