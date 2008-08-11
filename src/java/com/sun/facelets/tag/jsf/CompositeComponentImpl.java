/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sun.facelets.tag.jsf;

import javax.faces.application.Resource;
import javax.faces.component.UINamingContainer;
import javax.faces.context.FacesContext;

/**
 *
 * @author edburns
 */
public class CompositeComponentImpl extends UINamingContainer {
    
    public static final String TYPE = "javax.faces.NamingContainer";

    @Override
    public String getFamily() {
        return TYPE;
    }
    

}
