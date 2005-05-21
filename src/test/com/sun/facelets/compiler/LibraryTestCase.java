package com.sun.facelets.compiler;


import com.sun.facelets.compiler.Compiler;
import com.sun.facelets.compiler.SAXCompiler;
import com.sun.facelets.compiler.TagLibraryConfig;
import com.sun.facelets.tag.TagLibrary;

import junit.framework.TestCase;

public class LibraryTestCase extends TestCase {

    public LibraryTestCase() {
        super();
    }

    public LibraryTestCase(String arg0) {
        super(arg0);
    }

    public void testFind() throws Exception {
    	Compiler c = new SAXCompiler();
        TagLibraryConfig cfg = new TagLibraryConfig();
        cfg.loadImplicit(c);
        TagLibrary tc = c.createTagLibrary();
        System.out.println(tc);
    }

}
