package com.sun.facelets.tag;

/**
 * A mutable set of rules to be used in auto-wiring state to a particular object
 * instance. Rules assigned to this object will be composed into a single
 * Metadata instance.
 * 
 * @author Jacob Hookom
 * @version $Id: MetaRuleset.java,v 1.1 2005/07/27 04:32:53 jhook Exp $
 */
public abstract class MetaRuleset {
    /**
     * @param attribute
     * @return
     */
    public abstract MetaRuleset ignore(String attribute);

    /**
     * @return
     */
    public abstract MetaRuleset ignoreAll();

    /**
     * @param attribute
     * @param property
     * @return
     */
    public abstract MetaRuleset alias(String attribute, String property);

    /**
     * @param mapper
     * @return
     */
    public abstract MetaRuleset add(Metadata mapper);

    /**
     * @param rule
     * @return
     */
    public abstract MetaRuleset addRule(MetaRule rule);

    /**
     * @return
     */
    public abstract Metadata finish();
}
