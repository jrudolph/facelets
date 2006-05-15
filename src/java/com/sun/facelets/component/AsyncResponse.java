package com.sun.facelets.component;

import java.util.HashMap;
import java.util.Map;

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
}
