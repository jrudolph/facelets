package com.sun.facelets.el;

import java.io.Serializable;

import javax.el.ELException;
import javax.el.MethodExpression;
import javax.faces.component.ActionSource;
import javax.faces.context.FacesContext;
import javax.faces.el.EvaluationException;
import javax.faces.el.MethodBinding;
import javax.faces.el.MethodNotFoundException;

/**
 * For legacy ActionSources
 * 
 * @see ActionSource
 * @author Jacob Hookom
 * @deprecated
 */
public class LegacyMethodBinding extends
        MethodBinding implements Serializable {

    private static final long serialVersionUID = 1L;

    protected final MethodExpression m;

    public LegacyMethodBinding(MethodExpression m) {
        this.m = m;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.faces.el.MethodBinding#getType(javax.faces.context.FacesContext)
     */
    public Class getType(FacesContext context)
            throws MethodNotFoundException {
        try {
            return m.getMethodInfo(ELAdaptor.getELContext(context)).getReturnType();
        } catch (javax.el.MethodNotFoundException e) {
            throw new MethodNotFoundException(e.getMessage(), e.getCause());
        } catch (ELException e) {
            throw new EvaluationException(e.getMessage(), e.getCause());
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.faces.el.MethodBinding#invoke(javax.faces.context.FacesContext,
     *      java.lang.Object[])
     */
    public Object invoke(FacesContext context, Object[] params)
            throws EvaluationException, MethodNotFoundException {
        try {
            return m.invoke(ELAdaptor.getELContext(context), params);
        } catch (javax.el.MethodNotFoundException e) {
            throw new MethodNotFoundException(e.getMessage(), e.getCause());
        } catch (ELException e) {
            throw new EvaluationException(e.getMessage(), e.getCause());
        }
    }
}