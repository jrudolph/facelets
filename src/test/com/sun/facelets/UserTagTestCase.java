/**
 * 
 */
package com.sun.facelets;

import javax.faces.component.UIViewRoot;

import com.sun.facelets.Facelet;
import com.sun.facelets.FaceletFactory;

/**
 * @author Jacob Hookom
 *
 */
public class UserTagTestCase extends FaceletTestCase {

    public void testEcho() throws Exception {
        Facelet f = FaceletFactory.getInstance().getFacelet("userTag.xhtml");
        UIViewRoot root = this.facesContext.getViewRoot();
        this.facesContext.getExternalContext().getRequestMap().put("name", "Jacob");
        f.apply(this.facesContext, root);
        this.write(root);
        System.out.println(this.writer.toString());
    }

}
