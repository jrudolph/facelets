package com.sun.facelets.compiler;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.el.ValueBinding;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.FacesEvent;
import javax.faces.event.FacesListener;
import javax.faces.render.Renderer;

import com.sun.facelets.el.ELText;

final class UILiteralText extends UILeaf {

    private final static Map attributes = new HashMap(){
    
        public void putAll(Map map) {
            // do nothing
        }
    
        public Object put(Object name, Object value) {
            return null;
        }
    };
    
    private final static Map facets = attributes;
    
    private final String text;
    
    public UILiteralText(String text) {
        this.text = text;
    }

    public void encodeBegin(FacesContext faces) throws IOException {
        ResponseWriter writer = faces.getResponseWriter();
        writer.write(this.text);
    }

}
