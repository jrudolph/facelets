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

import javax.faces.context.FacesContext;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.faces.component.UIComponent;

final class LiteralTextInstruction implements Instruction {
    private final String text;

    public LiteralTextInstruction(String text) {
        this.text = text;
    }

    public void write(FacesContext context, UIComponent component) throws IOException {
        context.getResponseWriter().writeText(this.text, null);
    }

    public Instruction apply(ExpressionFactory factory, ELContext ctx) {
        return this;
    }

    public boolean isLiteral() {
        return true;
    }
}
