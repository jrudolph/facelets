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

import com.sun.facelets.tag.Tag;
import com.sun.facelets.tag.TagLibrary;

class ImplementationUnit extends TrimmedTagUnit {

    public ImplementationUnit(TagLibrary library, String namespace, String name, Tag tag, String id) {
        super(library, namespace, name, tag, id);
    }

}