package com.sun.facelets.tag;

import com.sun.facelets.FaceletContext;

/**
 * 
 * @author Jacob Hookom
 * @version $Id: MetadataImpl.java,v 1.1 2005/07/27 04:32:52 jhook Exp $
 */
final class MetadataImpl extends Metadata {

    private final Metadata[] mappers;
    private final int size;
    
    public MetadataImpl(Metadata[] mappers) {
        this.mappers = mappers;
        this.size = mappers.length;
    }

    public void applyMetadata(FaceletContext ctx, Object instance) {
        for (int i = 0; i < size; i++) {
            this.mappers[i].applyMetadata(ctx, instance);
        }
    }

}
