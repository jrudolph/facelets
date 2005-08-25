package com.sun.facelets.oracle.adf;

import java.io.Serializable;

import javax.el.ELContext;
import javax.el.ValueExpression;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;

import com.sun.facelets.el.LegacyELContext;

/**
 * @author Emmanuel Pirsch
 */
public class SetActionListener implements ActionListener, Serializable
{
  public SetActionListener()
  {
  }

  public SetActionListener(
   ValueExpression fromExpression,
   ValueExpression toExpression)
  {
    _toExpression = toExpression;
    _fromExpression = fromExpression;
  }

  public void processAction(ActionEvent actionEvent)
  {
    ELContext elContext= new LegacyELContext(FacesContext.getCurrentInstance());
    Object fromValue = _fromExpression.getValue(elContext);
    _toExpression.setValue(elContext, fromValue);
  }
  
  private ValueExpression _toExpression;
  private ValueExpression _fromExpression;

  private static final long serialVersionUID = -7884615973628756807L;
}