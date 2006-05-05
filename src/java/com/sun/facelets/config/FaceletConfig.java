package com.sun.facelets.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.el.ExpressionFactory;
import javax.faces.context.FacesContext;

import com.sun.facelets.FaceletException;
import com.sun.facelets.FaceletResolver;
import com.sun.facelets.compiler.Compiler;
import com.sun.facelets.compiler.SAXCompiler;
import com.sun.facelets.impl.DefaultFaceletResolver;
import com.sun.facelets.tag.CompositeTagDecorator;
import com.sun.facelets.tag.CompositeTagLibrary;
import com.sun.facelets.tag.TagDecorator;
import com.sun.facelets.tag.TagLibrary;
import com.sun.facelets.util.FacesAPI;
import com.sun.facelets.util.ParameterCheck;

public class FaceletConfig {
    
    public final static String CONFIG_KEY = FaceletConfig.class.getName();

    protected final static Logger log = Logger.getLogger("facelets.config");

    private static final TagLibrary EMPTY_LIBRARY = new CompositeTagLibrary(
            new TagLibrary[0]);

    private static final TagDecorator EMPTY_DECORATOR = new CompositeTagDecorator(
            new TagDecorator[0]);

    public final static String EXPRESSION_FACTORY = "compiler.ExpressionFactory";

    private FaceletResolver resolver = new DefaultFaceletResolver();

    private boolean validating = false;

    private boolean trimmingWhitespace = false;

    private boolean trimmingComments = false;
    
    private boolean developmentMode = false;

    private final List libraries = new ArrayList();

    private final List decorators = new ArrayList();

    private TagLibrary createdLibrary;

    private TagDecorator createdDecorator;
    
    private String resourcePath = "/FACES-INF";

    private final Map features = new HashMap();

    private boolean initialized = false;

    public FaceletConfig() {
        super();
    }

    public final TagDecorator createTagDecorator() {
        if (this.createdDecorator != null)
            return this.createdDecorator;
        if (this.decorators.size() > 0) {
            this.createdDecorator = new CompositeTagDecorator(
                    (TagDecorator[]) this.decorators
                            .toArray(new TagDecorator[this.decorators.size()]));
            return this.createdDecorator;
        }
        return EMPTY_DECORATOR;
    }

    public final void addTagDecorator(TagDecorator decorator) {
        ParameterCheck.notNull("decorator", decorator);
        if (!this.decorators.contains(decorator)) {
            this.createdDecorator = null;
            this.decorators.add(decorator);
        }
    }

    public final ExpressionFactory createExpressionFactory() {
        ExpressionFactory el = null;
        el = (ExpressionFactory) this.featureInstance(EXPRESSION_FACTORY);
        if (el == null && FacesAPI.getVersion() >= 12) {
            try {
                el = FacesContext.getCurrentInstance().getApplication()
                        .getExpressionFactory();
                if (el == null) {
                    log
                            .warning("No default ExpressionFactory from Faces Implementation, attempting to load from Feature["
                                    + EXPRESSION_FACTORY + "]");
                }
            } catch (Exception e) {
                // do nothing
            }
        }
        if (el == null) {
            this.features.put(EXPRESSION_FACTORY,
                    "com.sun.el.ExpressionFactoryImpl");
            el = (ExpressionFactory) this.featureInstance(EXPRESSION_FACTORY);
        }
        return el;
    }

    private final Object featureInstance(String name) {
        String type = (String) this.features.get(name);
        if (type != null) {
            try {
                return Class.forName(type, true,
                        Thread.currentThread().getContextClassLoader())
                        .newInstance();
            } catch (Throwable t) {
                throw new FaceletException("Could not instantiate feature["
                        + name + "]: " + type);
            }
        }
        return null;
    }

    public final TagLibrary createTagLibrary() {
        if (this.createdLibrary != null)
            return this.createdLibrary;

        if (this.libraries.size() > 0) {
            this.createdLibrary = new CompositeTagLibrary(
                    (TagLibrary[]) this.libraries
                            .toArray(new TagLibrary[this.libraries.size()]));
            return this.createdLibrary;
        }
        return EMPTY_LIBRARY;
    }

    public final void addTagLibrary(TagLibrary library) {
        ParameterCheck.notNull("library", library);
        if (!this.libraries.contains(library)) {
            this.createdLibrary = null;
            this.libraries.add(library);
        }
    }

    public final void setFeature(String name, String value) {
        this.features.put(name, value);
    }

    public final String getFeature(String name) {
        return (String) this.features.get(name);
    }

    public final boolean isTrimmingComments() {
        return this.trimmingComments;
    }

    public final void setTrimmingComments(boolean trimmingComments) {
        this.trimmingComments = trimmingComments;
    }

    public final boolean isTrimmingWhitespace() {
        return this.trimmingWhitespace;
    }

    public final void setTrimmingWhitespace(boolean trimmingWhitespace) {
        this.trimmingWhitespace = trimmingWhitespace;
    }

    public final boolean isValidating() {
        return this.validating;
    }

    public final void setValidating(boolean validating) {
        this.validating = validating;
    }

    public FaceletResolver getResolver() {
        return resolver;
    }

    public void setResolver(FaceletResolver resolver) {
        this.resolver = resolver;
    }

    public Compiler createCompiler() {
        return new SAXCompiler(this);
    }
    
    public static FaceletConfig getInstance(FacesContext faces) {
        return (FaceletConfig) faces.getExternalContext().getApplicationMap().get(CONFIG_KEY);
    }

    public String getResourcePath() {
        return resourcePath;
    }

    public boolean isDevelopmentMode() {
        return developmentMode;
    }

    public void setDevelopmentMode(boolean developmentMode) {
        this.developmentMode = developmentMode;
    }
}
