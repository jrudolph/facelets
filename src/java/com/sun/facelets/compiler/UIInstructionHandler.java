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
import java.io.Writer;

import java.util.ArrayList;
import java.util.List;

import javax.el.ELException;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;

import com.sun.facelets.FaceletContext;
import com.sun.facelets.FaceletException;
import com.sun.facelets.FaceletHandler;
import com.sun.facelets.el.ELText;
import com.sun.facelets.tag.TextHandler;
import com.sun.facelets.tag.jsf.ComponentSupport;
import com.sun.facelets.util.FastWriter;

/**
 * @author Adam Winer
 * @version $Id: UIInstructionHandler.java,v 1.2 2006/03/29 04:10:04 jhook Exp $
 */
final class UIInstructionHandler implements FaceletHandler, TextHandler {
    private final String alias;

    private final ELText txt;
    
    private final Instruction[] instructions;

    private final int length;
  
    private final boolean literal;

    public UIInstructionHandler(String alias, Instruction[] instructions, ELText txt) {
        this.alias = alias;
        this.instructions = instructions;
        this.txt = txt;
        this.length = txt.toString().length();

        boolean literal = true;
        int size = instructions.length;

        for (int i = 0; i < size; i++) {
            Instruction ins = (Instruction) this.instructions[i];
            if (!ins.isLiteral()) {
                literal = false;
                break;
            }
        }

        this.literal = literal;
    }

    public void apply(FaceletContext ctx, UIComponent parent)
            throws IOException, FacesException, FaceletException, ELException {
        if (parent != null) {
            Instruction[] applied;
            if (this.literal) {
                applied = this.instructions;
            } else {
                int size = this.instructions.length;
                applied = new Instruction[size];
                // Create a new list with all of the necessary applied
                // instructions
                Instruction ins;
                for (int i = 0; i < size; i++) {
                    ins = this.instructions[i];
                    applied[i] = ins.apply(ctx.getExpressionFactory(), ctx);
                }
            }

            UIComponent c = new UIInstructions(txt, applied);
            c.setId(ComponentSupport.getViewRoot(ctx, parent).createUniqueId());
            parent.getChildren().add(c);
        }
    }

    public String toString() {
        return this.txt.toString();
    }

    public String getText() {
        return this.txt.toString();
    }

    public String getText(FaceletContext ctx) {
        Writer writer = new FastWriter(this.length);
        try {
            this.txt.apply(ctx.getExpressionFactory(), ctx).write(writer, ctx);
        } catch (IOException e) {
            throw new ELException(this.alias + ": "+ e.getMessage(), e.getCause());
        }
        return writer.toString();
    }
}
