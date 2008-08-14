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

package com.sun.facelets.tag.jstl.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

import com.sun.facelets.Facelet;
import com.sun.facelets.FaceletFactory;
import com.sun.facelets.FaceletTestCase;
import com.sun.facelets.bean.Employee;
import com.sun.facelets.mock.MockResponseWriter;
import com.sun.facelets.util.FastWriter;

public final class JstlCoreTestCase extends FaceletTestCase {

    public void testIf() throws Exception {
        FacesContext faces = FacesContext.getCurrentInstance();
        Map session = faces.getExternalContext().getSessionMap();
        Employee e = new Employee();
        session.put("employee", e);

        FaceletFactory f = FaceletFactory.getInstance();
        Facelet at = f.getFacelet("if.xml");
        
        UIViewRoot root = faces.getViewRoot();

        // make sure the form is there
        e.setManagement(true);
        at.apply(faces, root);
        UIComponent c = root.findComponent("form");
        assertNotNull("form is null", c);
        
        // now make sure it isn't
        e.setManagement(false);
        at.apply(faces, root);
        c = root.findComponent("form");
        assertNull("form is not null", c);
    }
    
    public void testForEach() throws Exception {
        FacesContext faces = FacesContext.getCurrentInstance();
        Map session = faces.getExternalContext().getSessionMap();
        Collection c = new ArrayList();
        for (int i = 0; i < 10; i++) {
            c.add(new Character((char)('A' + i)));
        }
        session.put("list", c);
        Map m = new HashMap();
        for (int i = 0; i < 10; i++) {
            m.put("" + i, "" + i);
        }
        
        FaceletFactory f = FaceletFactory.getInstance();
        Facelet at = f.getFacelet("forEach.xml");
        
        UIViewRoot root = faces.getViewRoot();
        at.apply(faces, root);
        
        FastWriter fw = new FastWriter();
        MockResponseWriter mrw = new MockResponseWriter(fw);
        faces.setResponseWriter(mrw);
        root.encodeAll(faces);
        System.out.println(fw);
        
        //System.out.println(root);
    }

}
