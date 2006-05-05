package com.sun.facelets.resource;

import java.io.IOException;
import java.io.OutputStream;

import javax.faces.FacesException;

public interface Resource {
    
    public String getMimeType();
    
    public long getLastModified();
    
    public void write(OutputStream os) throws IOException, FacesException;
    
}
