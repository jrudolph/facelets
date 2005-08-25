package com.sun.facelets.oracle.adf;

import java.io.IOException;

import javax.el.ELException;
import javax.el.ValueExpression;
import javax.faces.component.ActionSource;
import javax.faces.component.UIComponent;

import com.sun.facelets.FaceletContext;
import com.sun.facelets.FaceletException;
import com.sun.facelets.tag.TagAttribute;
import com.sun.facelets.tag.TagConfig;
import com.sun.facelets.tag.TagHandler;
import com.sun.facelets.tag.jsf.ComponentSupport;

/**
 * @author Emmanuel Pirsch
 */
public class SetActionListenerTag extends TagHandler
{
	
  public SetActionListenerTag(TagConfig tagConfig)
  {
    super(tagConfig);
    _from = getRequiredAttribute("from");
    _to   = getRequiredAttribute("to");
  }
  
  public void apply(FaceletContext faceletContext,
                    UIComponent parent) throws FaceletException, ELException
  {
    if (ComponentSupport.isNew(parent))
    {
      ValueExpression fromExpression = _from.getValueExpression(faceletContext,
                                                                Object.class);
      ValueExpression toExpression=  _to.getValueExpression(faceletContext,
                                                            Object.class);
      ActionSource actionSource= (ActionSource) parent;
      actionSource.addActionListener(
        new SetActionListener(fromExpression, toExpression));
    }
  }

  private final TagAttribute _from;
  private final TagAttribute _to;
}

