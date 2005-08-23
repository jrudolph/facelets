package com.sun.facelets.oracle.adf;

import com.sun.facelets.tag.jsf.ComponentHandler;
import com.sun.facelets.tag.jsf.ComponentConfig;

import com.sun.facelets.tag.MetaRuleset;

/**
 * TagLibrary implementation for Oracle ADF core library.
 * @author Adam Winer
 * @version
 */
public class AdfComponentHandler extends ComponentHandler
{
  public AdfComponentHandler(ComponentConfig config) 
  {
    super(config);
  }

  protected MetaRuleset createMetaRuleset(Class type)
  {
    MetaRuleset m = super.createMetaRuleset(type);
    
    m.addRule(StringArrayPropertyTagRule.Instance);
    m.addRule(AdfListenersTagRule.Instance);
           
    return m;
  }
}