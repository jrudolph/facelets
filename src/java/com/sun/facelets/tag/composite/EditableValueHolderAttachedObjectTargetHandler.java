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
public class EditableValueHolderAttachedObjectTargetHandler extends AttachedObjectTargetHandler {
    
    public EditableValueHolderAttachedObjectTargetHandler(TagConfig config) {
        super(config);
    }
    

    @Override
    AttachedObjectTargetImpl newAttachedObjectTargetImpl() {
        return new EditableValueHolderAttachedObjectTargetImpl();
    }

}
