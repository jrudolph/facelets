package com.sun.facelets.tag;

import com.sun.facelets.FaceletContext;

/**
 * External information on how to wire dynamic or literal state to the
 * passed Object instance.
 * 
 * @author Jacob Hookom
 * @version $Id: Metadata.java,v 1.1 2005/07/27 04:32:52 jhook Exp $
 */
public abstract class Metadata {

    /**
     * @param ctx
     * @param instance
     */
    public abstract void applyMetadata(FaceletContext ctx, Object instance);

}
