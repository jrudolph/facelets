package com.sun.facelets.compiler;

import java.net.URL;


import com.sun.facelets.FaceletHandler;
import com.sun.facelets.FaceletTestCase;

public class CompilerTestCase extends FaceletTestCase {

    public CompilerTestCase() {
        super();
    }

    public CompilerTestCase(String arg0) {
        super(arg0);
    }
    
    public void testCompile() throws Exception {
        String s = "simple.xhtml";
        URL url = this.getLocalFile(s);
        FaceletHandler handler = this.compiler.compile(url, s);
        long time = 0;
        for (int i = 0; i < 50; i++) {
            time = System.currentTimeMillis();
            handler = this.compiler.compile(url, s);
            time = System.currentTimeMillis() - time;
            System.out.println(time + " ms");
        }
        System.out.println(handler.toString());
    }

}
