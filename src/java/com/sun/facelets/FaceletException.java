/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package com.sun.facelets;

import javax.faces.FacesException;

/**
 * An Exception from Facelet implementation
 * 
 * @author Jacob Hookom
 * @version $Id: FaceletException.java,v 1.2.28.1 2008/08/14 21:12:07 rlubke Exp $
 */
public class FaceletException extends FacesException {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * 
     */
    public FaceletException() {
        super();
    }

    /**
     * @param message
     */
    public FaceletException(String message) {
        super(message);
    }

    /**
     * @param cause
     */
    public FaceletException(Throwable cause) {
        super(cause);
    }

    /**
     * @param message
     * @param cause
     */
    public FaceletException(String message, Throwable cause) {
        super(message, cause);
    }

}
