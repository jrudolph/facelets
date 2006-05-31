package com.sun.facelets;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

import com.sun.facelets.mock.MockResponseWriter;
import com.sun.facelets.util.FastWriter;

public class EncodingTestCase extends FaceletTestCase {
    
    public void testPattern() throws Exception {
        Pattern p = Pattern.compile("^<\\?xml.+?version=['\"](.+?)['\"](.+?encoding=['\"]((.+?))['\"])?.*?\\?>");
        String[] d = new String[] { "<?xml version=\"1.0\" ?>", "<?xml version='1.0' ?>", "<?xml version='1.0' encoding='iso-8859-1'?>" };
        for (int i = 0; i < d.length; i++) {
            Matcher m = p.matcher(d[i]);
            System.out.println(d[i] + " " + m.matches());
            if (m.matches()) {
                for (int j = 0; j < m.groupCount(); j++) {
                    System.out.println('\t' + m.group(j));
                }
            }
        }
    }

    public void testEncoding() throws Exception {
        FaceletFactory ff = FaceletFactory.getInstance();
        FacesContext faces = FacesContext.getCurrentInstance();
        
        Facelet f = ff.getFacelet("encoding.xml");
        
        this.servletRequest.setAttribute("name", "Mr. Hookom");
        
        UIViewRoot root = faces.getViewRoot();
        f.apply(faces, root);
        
        FastWriter fw = new FastWriter();
        MockResponseWriter mrw = new MockResponseWriter(fw);
        faces.setResponseWriter(mrw);
        root.encodeAll(faces);
        System.out.println(fw);
    }

}
