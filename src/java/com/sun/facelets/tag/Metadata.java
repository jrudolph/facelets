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
 * External information on how to wire dynamic or literal state to the
 * passed Object instance.
 * 
 * @author Jacob Hookom
 * @version $Id: Metadata.java,v 1.2 2005/08/24 04:38:47 jhook Exp $
 */
public abstract class Metadata {

    /**
     * @param ctx
     * @param instance
     */
    public abstract void applyMetadata(FaceletContext ctx, Object instance);

}
