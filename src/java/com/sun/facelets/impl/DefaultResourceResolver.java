package com.sun.facelets.impl;

import java.io.IOException;
import java.net.URL;

import javax.faces.FacesException;
import javax.faces.context.FacesContext;

import com.sun.facelets.util.Resource;

public class DefaultResourceResolver implements ResourceResolver {

    public DefaultResourceResolver() {
        super();
    }

    public URL resolveUrl(String path) {
        try {
            return Resource.getResourceUrl(FacesContext.getCurrentInstance(),
                    path);
        } catch (IOException e) {
            throw new FacesException(e);
        }
    }

    public String toString() {
        return "DefaultResourceResolver";
    }

}
