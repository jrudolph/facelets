/**
 * 
 */
package com.sun.facelets;

import javax.el.ELContext;
import javax.faces.component.UIViewRoot;

import com.sun.facelets.Facelet;
import com.sun.facelets.FaceletFactory;

/**
 * @author Jacob Hookom
 *
 */
public class TemplateTestCase extends FaceletTestCase {

    /**
     * 
     */
    public TemplateTestCase() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @param arg0
     */
    public TemplateTestCase(String arg0) {
        super(arg0);
        // TODO Auto-generated constructor stub
    }
    
    public void testDefaultTemplate() throws Exception {
        Facelet f = FaceletFactory.getInstance().getFacelet("template.xhtml");
        UIViewRoot root = this.facesContext.getViewRoot();
        ELContext ctx = this.facesContext.getELContext();
        ctx.getELResolver().setValue(ctx, null, "name", "Jacob");
        f.apply(this.facesContext, root);
        this.write(root);
        String content = this.writer.toString();
        assertTrue("Default Title", content.indexOf("Default Title") != -1);
        assertTrue("Default Body", content.indexOf("Default Body") != -1);
    }
    
    public void testClientTemplate() throws Exception {
        Facelet f = FaceletFactory.getInstance().getFacelet("template-client.xhtml");
        UIViewRoot root = this.facesContext.getViewRoot();
        ELContext ctx = this.facesContext.getELContext();
        ctx.getELResolver().setValue(ctx, null, "name", "Jacob");
        f.apply(this.facesContext, root);
        this.write(root);
        String content = this.writer.toString();
        assertTrue("My Title", content.indexOf("My Title") != -1);
        assertTrue("My Body", content.indexOf("My Body") != -1);
    }

}
