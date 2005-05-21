package com.sun.facelets;

import java.net.URL;


import com.sun.facelets.compiler.Compiler;
import com.sun.facelets.spi.DefaultFaceletFactory;

public class TestFaceletFactory extends DefaultFaceletFactory {

    protected final Class root;
    
    public TestFaceletFactory(Class root, Compiler compiler) {
        super(compiler, getLocation(root));
        this.root = root;
    }
    
    protected static final URL getLocation(Class root) {
        String directory = getDirectory(root);
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        URL url = cl.getResource(directory + "/");
        return url;
    }
    
    protected static String getDirectory(Class root) {
        return root.getName().substring(0,
                root.getName().lastIndexOf('.')).replace('.', '/');
    }

}
