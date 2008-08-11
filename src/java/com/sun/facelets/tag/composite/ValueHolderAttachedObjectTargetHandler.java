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
public class ValueHolderAttachedObjectTargetHandler extends AttachedObjectTargetHandler {
    
    public ValueHolderAttachedObjectTargetHandler(TagConfig config) {
        super(config);
    }
    

    @Override
    AttachedObjectTargetImpl newAttachedObjectTargetImpl() {
        return new ValueHolderAttachedObjectTargetImpl();
    }

}
