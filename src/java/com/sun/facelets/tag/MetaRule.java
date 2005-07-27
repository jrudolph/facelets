package com.sun.facelets.tag;

/**
 * A potential rule for Metadata on the passed MetadataTarget
 * 
 * @see com.sun.facelets.tag.Metadata
 * @see com.sun.facelets.tag.MetadataTarget
 * @author Jacob Hookom
 * @version $Id: MetaRule.java,v 1.1 2005/07/27 04:32:53 jhook Exp $
 */
public abstract class MetaRule {

    /**
     * @param name
     * @param attribute
     * @param meta
     * @return
     */
    public abstract Metadata applyRule(String name, TagAttribute attribute,
            MetadataTarget meta);

}
