package com.sun.facelets.tag.ui;

import com.sun.facelets.tag.MetaRuleset;
import com.sun.facelets.tag.jsf.ComponentConfig;
import com.sun.facelets.tag.jsf.ComponentHandler;

public final class ComponentRefHandler extends ComponentHandler {

    public final static String Name = "component";
    
    public ComponentRefHandler(ComponentConfig config) {
        super(config);
    }

    protected MetaRuleset createMetaRuleset(Class type) {
        return super.createMetaRuleset(type).ignoreAll();
    }

}
