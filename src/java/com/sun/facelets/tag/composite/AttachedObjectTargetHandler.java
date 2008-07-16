/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sun.facelets.tag.composite;

import com.sun.facelets.FaceletContext;
import com.sun.facelets.FaceletException;
import com.sun.facelets.tag.TagAttribute;
import com.sun.facelets.tag.TagConfig;
import com.sun.facelets.tag.TagException;
import com.sun.facelets.tag.TagHandler;
import java.beans.BeanDescriptor;
import java.beans.BeanInfo;
import java.io.IOException;
import java.util.List;
import javax.el.ELException;
import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.webapp.pdl.AttachedObjectTarget;

/**
 *
 * @author edburns
 */
public abstract class AttachedObjectTargetHandler extends TagHandler {
    
    private TagAttribute name = null;
    private TagAttribute targets = null;

    public AttachedObjectTargetHandler(TagConfig config) {
        super(config);
        this.name = this.getRequiredAttribute("name");
        this.targets = this.getAttribute("targets");
        
    }
    
    abstract AttachedObjectTargetImpl newAttachedObjectTargetImpl();
    
    public void apply(FaceletContext ctx, UIComponent parent) throws IOException, FacesException, FaceletException, ELException {

        BeanInfo componentBeanInfo = (BeanInfo)
                parent.getAttributes().get(UIComponent.BEANINFO_KEY);
        if (null == componentBeanInfo) {
            throw new TagException(this.tag, "Error: I have an EditableValueHolder tag, but no enclosing composite component");
        }
        BeanDescriptor componentDescriptor = componentBeanInfo.getBeanDescriptor();
        if (null == componentDescriptor) {
            throw new TagException(this.tag, "Error: I have an EditableValueHolder tag, but no enclosing composite component");
        }

        FacesContext context = ctx.getFacesContext();
        List<AttachedObjectTarget> targetList = (List<AttachedObjectTarget>)
                componentDescriptor.getValue(AttachedObjectTarget.ATTACHED_OBJECT_TARGETS_KEY);
        AttachedObjectTargetImpl target = newAttachedObjectTargetImpl();
        targetList.add(target);
        target.setComponent(parent);
        
        ValueExpression ve = null;
        String strValue = null;

        ve = name.getValueExpression(ctx, String.class);
        strValue = (String) ve.getValue(ctx);
        if (null != strValue) {
            target.setName(strValue);
        }

        if (null != targets) {
            ve = targets.getValueExpression(ctx, String.class);
            strValue = (String) ve.getValue(ctx);
            if (null != strValue) {
                target.setTargetsList(strValue);
            }
        }
        
    }

}
