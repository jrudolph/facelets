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
import java.util.List;

import javax.faces.context.FacesContext;

final class UIInstructions extends UILeaf {
    
    private final List instructions;
    
    public UIInstructions(List instructions) {
        this.instructions = instructions;
    }

    public void encodeBegin(FacesContext context) throws IOException {
        int size = this.instructions.size();
        for (int i = 0; i < size; i++) {
            Instruction instruction = (Instruction) this.instructions.get(i);
            instruction.write(context);
        }
    }

    public String toString() {
        // TODO - good toString() impl.
        return "UIInstructions[" + instructions + "]";
    }

}
