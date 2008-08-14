/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sun.facelets.tag.ui;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

import com.sun.facelets.Facelet;
import com.sun.facelets.FaceletFactory;
import com.sun.facelets.FaceletTestCase;
import com.sun.facelets.mock.MockResponseWriter;
import com.sun.faces.util.DebugUtil;

public class UITestCase extends FaceletTestCase {

    public void testRelativePaths() throws Exception {
        FacesContext faces = FacesContext.getCurrentInstance();

        FaceletFactory f = FaceletFactory.getInstance();
        Facelet at = f.getFacelet("parent.xml");

        UIViewRoot root = faces.getViewRoot();
        at.apply(faces, root);
        
        StringWriter sw = new StringWriter();
        MockResponseWriter mrw = new MockResponseWriter(sw);
        faces.setResponseWriter(mrw);
        root.encodeAll(faces);
        sw.flush();
        System.out.println("************************");
        System.out.println(sw.toString());
        System.out.println("************************");
    }
    
    public void testCompositionTemplate() throws Exception {
        FacesContext faces = FacesContext.getCurrentInstance();

        FaceletFactory f = FaceletFactory.getInstance();
        Facelet at = f.getFacelet("composition-template.xml");

        UIViewRoot root = faces.getViewRoot();
        at.apply(faces, root);
    }
    
    public void testCompositionTemplateSimple() throws Exception {
        FacesContext faces = FacesContext.getCurrentInstance();

        FaceletFactory f = FaceletFactory.getInstance();
        Facelet at = f.getFacelet("composition-template-simple.xml");

        UIViewRoot root = faces.getViewRoot();
        at.apply(faces, root);
    }
    
    public void testComponent() throws Exception {
        FacesContext faces = FacesContext.getCurrentInstance();
        Map map = new HashMap();
        faces.getExternalContext().getRequestMap().put("map", map);

        FaceletFactory f = FaceletFactory.getInstance();
        Facelet at = f.getFacelet("component.xml");

        UIViewRoot root = faces.getViewRoot();
        at.apply(faces, root);
        
        assertEquals("only one child, the component", 1, root.getChildCount());
        assertNotNull("bound to map", map.get("c"));
    }
    
    public void testComponentClient() throws Exception {
        FacesContext faces = FacesContext.getCurrentInstance();
        Map map = new HashMap();
        faces.getExternalContext().getRequestMap().put("map", map);

        FaceletFactory f = FaceletFactory.getInstance();
        Facelet at = f.getFacelet("component-client.xml");

        UIViewRoot root = faces.getViewRoot();
        at.apply(faces, root);
        
        assertEquals("4 children, the component", 4, root.getChildCount());
        assertNotNull("bound to map", map.get("c"));
    }

}
