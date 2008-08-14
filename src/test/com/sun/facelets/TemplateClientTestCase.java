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

package com.sun.facelets;

import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

import com.sun.facelets.mock.MockResponseWriter;
import com.sun.facelets.util.FastWriter;

public class TemplateClientTestCase extends FaceletTestCase {

    public void testClientClient() throws Exception {
        FaceletFactory ff = FaceletFactory.getInstance();
        FacesContext faces = FacesContext.getCurrentInstance();
        
        Facelet f = ff.getFacelet("template-client-client.xhtml");
        
        this.servletRequest.setAttribute("name", "Mr. Hookom");
        
        UIViewRoot root = faces.getViewRoot();
        f.apply(faces, root);
        
        FastWriter fw = new FastWriter();
        MockResponseWriter mrw = new MockResponseWriter(fw);
        faces.setResponseWriter(mrw);
        root.encodeAll(faces);
        System.out.println(fw);
    }
    
    /*
    public void testLayoutClient() throws Exception {
        FaceletFactory ff = FaceletFactory.getInstance();
        FacesContext faces = FacesContext.getCurrentInstance();
        
        Facelet f = ff.getFacelet("layout-client.xhtml");
        
        this.servletRequest.setAttribute("name", "Mr. Hookom");
        
        UIViewRoot root = faces.getViewRoot();
        f.apply(faces, root);
        
        FastWriter fw = new FastWriter();
        MockResponseWriter mrw = new MockResponseWriter(fw);
        faces.setResponseWriter(mrw);
        root.encodeAll(faces);
        System.out.println(fw);
    }
    */
   

}
