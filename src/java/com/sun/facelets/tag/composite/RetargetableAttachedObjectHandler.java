/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sun.facelets.tag.composite;

import com.sun.facelets.FaceletContext;
import javax.faces.component.UIComponent;

/**
 *
 * @author edburns
 */
public interface RetargetableAttachedObjectHandler {
    
     public void applyAttachedObjectToComponent(FaceletContext ctx, UIComponent parent);
     
     public String getFor();

}
