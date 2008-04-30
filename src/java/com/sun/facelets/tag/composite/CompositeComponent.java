/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sun.facelets.tag.composite;

import javax.faces.component.UIComponentBase;

/**
 *
 * @author edburns
 */
public class CompositeComponent extends UIComponentBase {
    
    public static final String TYPE = "javax.faces.Composite";

    @Override
    public String getFamily() {
        return TYPE;
    }

}
