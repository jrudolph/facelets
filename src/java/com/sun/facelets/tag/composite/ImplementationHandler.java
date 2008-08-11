/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sun.facelets.tag.composite;

import com.sun.facelets.FaceletContext;
import com.sun.facelets.FaceletException;
import com.sun.facelets.tag.TagConfig;
import com.sun.facelets.tag.TagHandler;
import java.io.IOException;
import java.util.logging.Logger;
import javax.el.ELException;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;

/**
 *
 * @author edburns
 */
public class ImplementationHandler extends TagHandler {

    private static final Logger log = Logger.getLogger("facelets.tag.composite");

    public final static String Name = "implementation";

    
    public ImplementationHandler(TagConfig config) {
        super(config);
    }
    
    public void apply(FaceletContext ctx, UIComponent parent) throws IOException, FacesException, FaceletException, ELException {
        this.nextHandler.apply(ctx, parent);
    }

}
