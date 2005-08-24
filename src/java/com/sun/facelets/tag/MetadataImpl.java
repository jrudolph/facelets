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

package com.sun.facelets.tag;

import com.sun.facelets.FaceletContext;

/**
 * 
 * @author Jacob Hookom
 * @version $Id: MetadataImpl.java,v 1.2 2005/08/24 04:38:47 jhook Exp $
 */
final class MetadataImpl extends Metadata {

    private final Metadata[] mappers;
    private final int size;
    
    public MetadataImpl(Metadata[] mappers) {
        this.mappers = mappers;
        this.size = mappers.length;
    }

    public void applyMetadata(FaceletContext ctx, Object instance) {
        for (int i = 0; i < size; i++) {
            this.mappers[i].applyMetadata(ctx, instance);
        }
    }

}
