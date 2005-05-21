package com.sun.facelets;

import javax.el.ELContext;
import javax.faces.component.UIViewRoot;
import javax.faces.render.RenderKitFactory;

import com.sun.facelets.Facelet;

public class DefaultFaceletTestCase extends FaceletTestCase {

    public DefaultFaceletTestCase() {
        super();
    }

    public DefaultFaceletTestCase(String arg0) {
        super(arg0);
    }
    
    public static void main(String[] argv) throws Exception {
    	DefaultFaceletTestCase tst = new DefaultFaceletTestCase();
    	tst.setUp();
    	tst.testDefaults();
    	tst.tearDown();
    }
    
    public void testDefaults() throws Exception {
        TestFaceletFactory factory = new TestFaceletFactory(this.getClass(), this.compiler);
        Facelet f = factory.getFacelet("userTag.xhtml");
        ELContext ctx = this.facesContext.getELContext();
        ctx.getELResolver().setValue(ctx, null, "name", "Jacob");
        this.facesContext.getExternalContext().getRequestMap().put("show", Boolean.TRUE);
        UIViewRoot root = new UIViewRoot();
        root.setRenderKitId(RenderKitFactory.HTML_BASIC_RENDER_KIT);
        root.setViewId("/postback.xhtml");
        this.facesContext.setViewRoot(root);
        f.apply(this.facesContext, root);
        //long t;
        int r = 100000;
        long tt = System.currentTimeMillis();
        for (int i = 0; i < r; i++) {
            root = new UIViewRoot();
            root.setRenderKitId(RenderKitFactory.HTML_BASIC_RENDER_KIT);
            root.setViewId("/postback.xhtml");
            this.facesContext.setViewRoot(root);
            //t = System.nanoTime();
            f.apply(this.facesContext, root);
            //this.write(root);
            //t = System.nanoTime() - t;
            //System.out.println(t + " ns");
        }
        tt = System.currentTimeMillis() - tt;
        System.out.println(((double)tt / r) + " ms");
        this.write(root);
        System.out.println(this.writer.toString());
        
    }
    
   

}
