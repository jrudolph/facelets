package com.sun.facelets.component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.faces.FacesException;
import javax.faces.application.ViewHandler;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import com.sun.facelets.FaceletViewHandler;
import com.sun.facelets.client.ClientWriter;

public class AsyncResponse {
    
    private final static ThreadLocal<AsyncResponse> Instance = new ThreadLocal<AsyncResponse>();
    
    private final Map<String,String> encoded = new HashMap<String, String>();
    private String viewState;

    public AsyncResponse() {
        super();
    }
    
    public static boolean exists() {
        return Instance.get() != null;
    }
    
    public static AsyncResponse getInstance(boolean create) {
        AsyncResponse ar = Instance.get();
        if (ar == null && create) {
            ar = new AsyncResponse();
            Instance.set(ar);
        }
        return ar;
    }
    
    public static AsyncResponse getInstance() {
        return getInstance(true);
    }
    
    public static void clearInstance() {
        Instance.remove();
    }
    
    public Map<String,String> getEncoded() {
        return this.encoded;
    }
    
    public void setViewState(String output) {
        this.viewState = output;
    }
    
    public String getViewState() {
        return this.viewState;
    }
    
    public static void encode(UIComponent component) {
        if (component != null) {
            encode(component.getClientId(FacesContext.getCurrentInstance()));
        }
    }
    
    public static void encode(String clientId) {
        if (clientId == null) return;
        
        FacesContext faces = FacesContext.getCurrentInstance();
        UIViewRoot root = faces.getViewRoot();
        if (!(root instanceof UIFacelet)) {
            throw new FacesException("UIViewRoot not instanceof UIFacelet");
        }
        
        ((UIFacelet) root).getEncodeIdSet().add(clientId);
    }
    
    public static ResponseWriter getResponseWriter() throws IOException {
        FacesContext faces = FacesContext.getCurrentInstance();
        ResponseWriter rw = faces.getResponseWriter();
        if (rw == null) {
            ViewHandler vh = faces.getApplication().getViewHandler();
            FaceletViewHandler fvh = null;
            if (vh instanceof FaceletViewHandler) {
                fvh = (FaceletViewHandler) vh;
            } else {
                fvh = new FaceletViewHandler(vh);
            }
            rw = fvh.createResponseWriter(faces);
            faces.setResponseWriter(rw);
        }
        return rw;
    }
    
    public static ClientWriter getClientWriter() throws IOException {
        return new ClientWriter(getResponseWriter());
    }
}
