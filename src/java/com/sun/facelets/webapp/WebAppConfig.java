package com.sun.facelets.webapp;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

import com.sun.facelets.FaceletException;
import com.sun.facelets.FaceletResolver;
import com.sun.facelets.config.FaceletConfig;
import com.sun.facelets.config.TagLibraryConfig;
import com.sun.facelets.tag.TagDecorator;
import com.sun.facelets.tag.TagLibrary;

public class WebAppConfig extends FaceletConfig {
    
    public static void setInstance(ServletContext ctx, WebAppConfig cfg) {
        ctx.setAttribute(FaceletConfig.CONFIG_KEY, cfg);
    }

    public final static String PARAM_SKIP_COMMENTS = "facelets.SKIP_COMMENTS";

    public final static String PARAM_LIBRARIES = "facelets.LIBRARIES";

    public final static String PARAM_DECORATORS = "facelets.DECORATORS";

    public final static String PARAM_FACELET_RESOLVER = "facelets.FACELET_RESOLVER";

    public final static String PARAM_DEVELOPMENT = "facelets.DEVELOPMENT";

    public WebAppConfig(ServletContext ctx) {
        this.initializeFaceletResolver(ctx);
        this.initializeTagLibraries(ctx);
        this.intitializeTagDecorators(ctx);
        this.intitializeCompilerFlags(ctx);
        this.initializeMode(ctx);
    }

    protected void initializeFaceletResolver(ServletContext ctx) {

        String typeParam = ctx.getInitParameter(PARAM_FACELET_RESOLVER);
        if (typeParam != null && !"".equals(typeParam)) {
            try {
                Class type = Thread.currentThread().getContextClassLoader()
                        .loadClass(typeParam);
                this.setResolver((FaceletResolver) type.newInstance());
            } catch (InstantiationException e) {
                throw new FaceletException(
                        "Couldn't Initialize FaceletResolver: " + typeParam, e
                                .getCause());
            } catch (Exception e) {
                throw new FaceletException(
                        "Couldn't Initialize FaceletResolver: " + typeParam, e);
            }
        }
    }

    protected void intitializeCompilerFlags(ServletContext ctx) {

        // skip params?
        String skipParam = ctx.getInitParameter(PARAM_SKIP_COMMENTS);
        if (skipParam != null && "true".equals(skipParam)) {
            this.setTrimmingComments(true);
        }
    }
    
    protected void initializeMode(ServletContext ctx) {
        String param = ctx.getInitParameter(PARAM_DEVELOPMENT);
        this.setDevelopmentMode("true".equals(param));
    }

    protected void intitializeTagDecorators(ServletContext ctx) {

        // load decorators
        String decParam = ctx.getInitParameter(PARAM_DECORATORS);
        if (decParam != null) {
            decParam = decParam.trim();
            String[] decs = decParam.split(";");
            TagDecorator decObj;
            for (int i = 0; i < decs.length; i++) {
                try {
                    decObj = (TagDecorator) Class.forName(decs[i])
                            .newInstance();
                    this.addTagDecorator(decObj);
                    log.fine("Successfully Loaded Decorator: " + decs[i]);
                } catch (Exception e) {
                    log.log(Level.SEVERE,
                            "Error Loading Decorator: " + decs[i], e);
                }
            }
        }
    }

    protected void initializeTagLibraries(ServletContext ctx) {
        try {
            new TagLibraryConfig().loadImplicit(this);
        } catch (IOException e) {
            throw new FaceletException(e);
        }

        // load libraries
        String libParam = ctx.getInitParameter(PARAM_LIBRARIES);
        if (libParam != null) {
            libParam = libParam.trim();
            String[] libs = libParam.split(";");
            URL src;
            TagLibrary libObj;
            for (int i = 0; i < libs.length; i++) {
                try {
                    src = ctx.getResource(libs[i].trim());
                    if (src == null) {
                        throw new FileNotFoundException(libs[i]);
                    }
                    libObj = TagLibraryConfig.create(src);
                    this.addTagLibrary(libObj);
                    log.fine("Successfully Loaded Library: " + libs[i]);
                } catch (IOException e) {
                    log.log(Level.SEVERE, "Error Loading Library: " + libs[i],
                            e);
                }
            }
        }
    }
}
