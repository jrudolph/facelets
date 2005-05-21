/**
 * Copyright 2005 Sun Microsystems, Inc. All rights reserved.
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

package com.sun.facelets.util;

/**
 * @author Jacob Hookom
 * @version $Id: Assert.java,v 1.1 2005/05/21 17:54:45 jhook Exp $
 */
public final class Assert {

    public final static void param(String name, Object value)
            throws NullPointerException {
        if (value == null) {
            throw new NullPointerException("Parameter '" + name
                    + "' cannot be null");
        }
    }

}
