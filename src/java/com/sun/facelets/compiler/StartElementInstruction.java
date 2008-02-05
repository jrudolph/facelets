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

package com.sun.facelets.compiler;


import java.io.IOException;

import javax.el.ELContext;
import javax.el.ExpressionFactory;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;


final class StartElementInstruction implements Instruction {
    private final String element;

    public StartElementInstruction(String element) {
        this.element = element;
    }

    public void write(FacesContext context, UIComponent component) throws IOException {
        context.getResponseWriter().startElement(this.element, null);
    }

    public Instruction apply(ExpressionFactory factory, ELContext ctx) {
        return this;
    }

    public boolean isLiteral() {
        return true;
    }
}
