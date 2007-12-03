/**
 * Licensed under the Common Development and Distribution License,
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.sun.com/cddl/
 *   
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or 
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package com.sun.facelets.tag.ui;

import javax.faces.component.UIInput;

public final class Component2Ref extends UIInput {

    public final static String COMPONENT_TYPE = "facelets.ui.Component2Ref";
    public final static String COMPONENT_FAMILY = "facelets";
    public final static String RENDERER_TYPE = "javax.faces.Text";
    
    public Component2Ref() {
        super();
    }

    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    public String getRendererType() {
        return RENDERER_TYPE;
    }
    
    

}
