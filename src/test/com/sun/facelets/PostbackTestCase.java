/**
 * 
 */
package com.sun.facelets;

import java.util.Iterator;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;

import com.sun.facelets.Facelet;
import com.sun.facelets.FaceletFactory;

/**
 * @author Jacob Hookom
 *
 */
public class PostbackTestCase extends FaceletTestCase {

    /**
     * 
     */
    public PostbackTestCase() {
        super();
    }

    /**
     * @param arg0
     */
    public PostbackTestCase(String arg0) {
        super(arg0);
    }
    
    public void testCommandLink() throws Exception {
        FaceletFactory ff = FaceletFactory.getInstance();
        Facelet f = ff.getFacelet("home.xhtml");
        UIViewRoot root = this.facesContext.getViewRoot();
        f.apply(this.facesContext, root);
        this.write(root);
        System.out.println(this.writer.toString());
    }
    
    public void testPostBackChanges() throws Exception {
        Facelet f = FaceletFactory.getInstance().getFacelet("postback.xhtml");
        UIViewRoot root = this.facesContext.getViewRoot();
        Map request = this.facesContext.getExternalContext().getRequestMap();
        request.put("show", Boolean.TRUE);
        
        f.apply(this.facesContext, root);
        this.write(root);
        String s0 = this.writer.toString();
        System.out.println(s0);
        
        removeTransient(root);
        
        assertNotNull("submit", root.findComponent("myForm:submit"));
        assertNotNull("submit2", root.findComponent("myForm2:submit2"));
        assertNotNull("submit3", root.findComponent("myForm3:submit3"));
        
        request.remove("show");
        f.apply(this.facesContext, root);
        this.write(root);
        String s1 = this.writer.toString();
        System.out.println(s1);
        
        assertNull("submit", root.findComponent("myForm:submit"));
    }
    
    protected static void removeTransient(UIComponent c) {
        UIComponent d;
        for (Iterator itr = c.getChildren().iterator(); itr.hasNext(); ) {
            d = (UIComponent) itr.next();
            if (d.isTransient()) {
                itr.remove();
            } else {
                removeTransient(d);
            }
        }
    }
    
    public void testPostBack() throws Exception {
        Facelet f = FaceletFactory.getInstance().getFacelet("postback.xhtml");
        UIViewRoot root = this.facesContext.getViewRoot();
        
        f.apply(this.facesContext, root);
        this.write(root);
        String s0 = this.writer.toString();
        //System.out.println(s0);
        
        removeTransient(root);
        //System.out.println("\n\n==========\n\n");
        
        f.apply(this.facesContext, root);
        this.write(root);
        String s1 = this.writer.toString();
        //System.out.println(s1);
        
        assertEquals(s0, s1);
    }

}
