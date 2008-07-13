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

import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import com.sun.facelets.Facelet;
import com.sun.facelets.FaceletFactory;
import com.sun.facelets.FaceletTestCase;
import com.sun.facelets.FaceletViewHandler;
import com.sun.facelets.tag.jsf.ComponentSupport;
import com.sun.facelets.util.FastWriter;

public class IncludeParamTestCase extends FaceletTestCase {

    public void testCaching() throws Exception {
        Facelet f = FaceletFactory.getInstance().getFacelet("test1.xml");
        
        
        FacesContext faces = FacesContext.getCurrentInstance();
        
        UIViewRoot root = faces.getViewRoot();
        
        this.servletRequest.setAttribute("test", "test2.xml");
        f.apply(faces, root);
        
        FastWriter fw = new FastWriter();
        ResponseWriter rw = faces.getResponseWriter();
        rw = rw.cloneWithWriter(fw);
        faces.setResponseWriter(rw);
        root.encodeAll(faces);
        System.out.println(fw);
        
        ComponentSupport.removeTransient(root);
        
        this.servletRequest.setAttribute("test", "test3.xml");
        f.apply(faces, root);
        
        fw = new FastWriter();
        rw = faces.getResponseWriter();
        rw = rw.cloneWithWriter(fw);
        faces.setResponseWriter(rw);
        root.encodeAll(faces);
        System.out.println(fw);
    }

}
