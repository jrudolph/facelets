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

import com.sun.facelets.FaceletHandler;

/**
 * 
 * @author Jacob Hookom
 * @version $Id: RemoveUnit.java,v 1.3 2005/08/24 04:38:54 jhook Exp $
 */
final class RemoveUnit extends CompilationUnit {

    public RemoveUnit() {
        super();
    }

    public void addChild(CompilationUnit unit) {
        // do nothing
    }

    public FaceletHandler createFaceletHandler() {
        return LEAF;
    }
}
