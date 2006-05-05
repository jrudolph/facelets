package com.sun.facelets.impl;

import java.io.IOException;
import java.net.URL;

import javax.faces.FacesException;
import javax.faces.context.FacesContext;

import com.sun.facelets.FaceletResolver;
import com.sun.facelets.util.PathTools;

public class DefaultFaceletResolver implements FaceletResolver {

    public DefaultFaceletResolver() {
        super();
    }

    public URL resolvePath(String path) {
        try {
            return PathTools.getResourceUrl(FacesContext.getCurrentInstance(),
                    path);
        } catch (IOException e) {
            throw new FacesException(e);
        }
    }

    public String toString() {
        return this.getClass().getName().toString();
    }

}
