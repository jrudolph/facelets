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

package com.sun.facelets.tag.jsf.html;

import javax.el.MethodExpression;
import javax.faces.component.ActionSource2;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

import com.sun.facelets.Facelet;
import com.sun.facelets.FaceletFactory;
import com.sun.facelets.FaceletTestCase;

public class HtmlTestCase extends FaceletTestCase {
    
    public void testCommandComponent() throws Exception {
        FacesContext faces = FacesContext.getCurrentInstance();
        this.servletRequest.getSession().setAttribute("test", new TestBean());

        FaceletFactory f = FaceletFactory.getInstance();
        Facelet at = f.getFacelet("componentOwner.xml");
        
        UIViewRoot root = faces.getViewRoot();
        at.apply(faces, root);
        
        UIComponent c = root.findComponent("cmd");
        assertNotNull("cmd", c);
        
        Object v = c.getAttributes().get("id");
        assertEquals("id", "cmd", v);
        
        ActionSource2 as2 = (ActionSource2) c;
        MethodExpression me = as2.getActionExpression();
        assertNotNull("method", me);
        
        String result = (String) me.invoke(faces.getELContext(), null);
        System.out.println(result);
    }
    
    public void testCommandButton() throws Exception {
        FacesContext faces = FacesContext.getCurrentInstance();

        FaceletFactory f = FaceletFactory.getInstance();
        Facelet at = f.getFacelet("commandButton.xml");
        
        UIViewRoot root = faces.getViewRoot();
        at.apply(faces, root);
        
        UIComponent c = root.findComponent("form:button");
        assertNotNull("button", c);
        
        Object v = c.getAttributes().get("id");
        assertEquals("id", "button", v);
    }

    public void testPanelGrid() throws Exception {
        FacesContext faces = FacesContext.getCurrentInstance();

        FaceletFactory f = FaceletFactory.getInstance();
        Facelet at = f.getFacelet("panelGrid.xml");

        UIViewRoot root = faces.getViewRoot();
        at.apply(faces, root);
    }

}
