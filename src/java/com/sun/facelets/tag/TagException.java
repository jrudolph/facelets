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

import com.sun.facelets.FaceletException;

/**
 * An Exception caused by a Tag
 * 
 * @author Jacob Hookom
 * @version $Id: TagException.java,v 1.3 2005/08/24 04:38:48 jhook Exp $
 */
public final class TagException extends FaceletException {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * 
     */
    public TagException(Tag tag) {
        super(tag.toString());
    }

    /**
     * @param message
     */
    public TagException(Tag tag, String message) {
        super(tag + " " + message);
    }

    /**
     * @param cause
     */
    public TagException(Tag tag, Throwable cause) {
        super(tag.toString(), cause);
    }

    /**
     * @param message
     * @param cause
     */
    public TagException(Tag tag, String message, Throwable cause) {
        super(tag + " " + message, cause);
    }

}
