/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sun.facelets.tag.composite;

import com.sun.facelets.tag.TagConfig;

/**
 *
 * @author edburns
 */
public class ActionSource2AttachedObjectTargetHandler extends AttachedObjectTargetHandler {
    
    public ActionSource2AttachedObjectTargetHandler(TagConfig config) {
        super(config);
    }
    

    @Override
    AttachedObjectTargetImpl newAttachedObjectTargetImpl() {
        return new ActionSource2AttachedObjectTargetImpl();
    }

}
