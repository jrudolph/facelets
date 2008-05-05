/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sun.facelets.tag.composite;

import com.sun.facelets.FaceletContext;
import com.sun.facelets.FaceletException;
import com.sun.facelets.tag.TagConfig;
import com.sun.facelets.tag.TagHandler;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.el.ELException;
import javax.faces.FacesException;
import javax.faces.component.CompositeComponent;
import javax.faces.component.UIComponent;

/**
 *
 * @author edburns
 */
public class AttachedObjectTargetHandler extends TagHandler {

    public AttachedObjectTargetHandler(TagConfig config) {
        super(config);
    }
    
    public void apply(FaceletContext ctx, UIComponent target) throws IOException, FacesException, FaceletException, ELException {
        UIComponent cur = CompositeComponentTagHandler.
                getCurrentCompositeComponent(ctx.getFacesContext().getExternalContext());
        if (null != cur && cur instanceof CompositeComponent) {
            // Allow the composite component to know about the target
            // component.
            AttachedObjectTargetHandler.getAttachedObjectTargets(cur).add(target);
        }
    }

    public static List<UIComponent> getAttachedObjectTargets(UIComponent component) {
        Map<String, Object> attrs = component.getAttributes();
        List<UIComponent> result = (List<UIComponent>)
                attrs.get("javax.faces.AttachedObjectTargets");
        
        if (null == result) {
            result = new ArrayList<UIComponent>();
            attrs.put("javax.faces.AttachedObjectTargets", result);
        }
        
        return result;
    }
    
    public static List<RetargetableAttachedObjectHandler> getRetargetableHandlers(UIComponent component) {
        Map<String, Object> attrs = component.getAttributes();
        List<RetargetableAttachedObjectHandler> result = (List<RetargetableAttachedObjectHandler>)
                attrs.get("javax.faces.RetargetableHandlers");
        
        if (null == result) {
            result = new ArrayList<RetargetableAttachedObjectHandler>();
            attrs.put("javax.faces.RetargetableHandlers", result);
        }
        
        return result;
    }

    
}
