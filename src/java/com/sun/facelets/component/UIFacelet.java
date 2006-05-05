package com.sun.facelets.component;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.FacesException;
import javax.faces.component.ContextCallback;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.servlet.http.HttpServletResponse;

import com.sun.facelets.client.ClientUtils;
import com.sun.facelets.event.EventCallback;
import com.sun.facelets.event.Events;
import com.sun.facelets.util.FastWriter;

public class UIFacelet extends UIViewRoot {

    private static final Logger log = Logger.getLogger("facelets.UIFacelet");

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
    
    public final static String PARAM_ASYNC = "javax.faces.Async";

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
        EventCallback c = Events.getEventCallback(faces);
        if (c != null && !c.isImmediate()) {
            if (log.isLoggable(Level.FINE)) {
                log.fine(c.toString());
            }
            c.invoke(faces);
            this.encodeQueued(faces);
        } else if (!this.encodeQueued(faces)) {
            if (log.isLoggable(Level.FINE)) {
                log.fine("Encoding whole View");
            }
            super.encodeAll(faces);
        } else {
            if (log.isLoggable(Level.FINE)) {
                log.fine("Skipped EncodeAll since Updates or Encodes were queued");
            }
        }
    }

    private boolean encodeQueued(FacesContext faces) throws IOException {
        if (!this.encode.isEmpty()) {
            ExternalContext ctx = faces.getExternalContext();
            Object resp = ctx.getResponse();
            if (resp instanceof HttpServletResponse) {
                ((HttpServletResponse) resp).addHeader("Cache-Control",
                        "no-cache");
            }

            UIViewRoot root = faces.getViewRoot();
            boolean success = false;
            ResponseWriter rw = faces.getResponseWriter();
            FastWriter fw = new FastWriter(256);
            ResponseWriter crw;
            StringBuffer headerId = new StringBuffer(16);
            try {
                if (resp instanceof HttpServletResponse) {
                    ((HttpServletResponse) resp).setHeader("javax.faces.Encode",
                            ClientUtils.toArray(this.encode));
                }
                for (String id : this.encode) {
                    fw.reset();
                    crw = rw.cloneWithWriter(fw);
                    faces.setResponseWriter(crw);
                    success = root.invokeOnComponent(faces, id, Encode);
                    if (!success) {
                        log.warning(id + " not found");
                    }
                    headerId.setLength(0);
                    headerId.append("javax.faces.Encode_").append(id);
                    if (resp instanceof HttpServletResponse) {
                        ((HttpServletResponse) resp).setHeader(headerId
                                .toString(), fw.toString());
                    }
                }
            } catch (FacesException e) {
                if (e.getCause() instanceof IOException) {
                    throw (IOException) e.getCause();
                } else {
                    throw e;
                }
            } finally {
                faces.setResponseWriter(rw);
            }
            return true;
        }
        return !this.update.isEmpty();
    }

    public void decode(FacesContext faces) {
        ExternalContext ctx = faces.getExternalContext();

        // get these parameters
        Map<String, String> params = ctx.getRequestHeaderMap();
        String param = params.get(PARAM_UPDATE);
        String[] update = (param != null) ? param.split(",") : null;
        param = params.get(PARAM_ENCODE);
        String[] encode = (param != null) ? param.split(",") : null;

        if (update != null && update.length > 0) {
            for (String id : update) {
                this.update.add(id);
            }
            if (log.isLoggable(Level.FINE)) {
                log.fine("Updating Only: "+this.update);
            }
        }

        if (encode != null && encode.length > 0) {
            for (String id : encode) {
                this.encode.add(id);
            }
            if (log.isLoggable(Level.FINE)) {
                log.fine("Encoding Only: "+this.encode);
            }
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
