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

package com.sun.facelets;

import java.io.IOException;
import java.net.URL;

import javax.el.ELException;
import javax.faces.FacesException;

/**
 * FaceletFactory for producing Facelets relative to the context of the
 * underlying implementation.
 * 
 * @author Jacob Hookom
 * @version $Id: FaceletFactory.java,v 1.3.8.1 2006/05/05 06:49:40 jhook Exp $
 */
public abstract class FaceletFactory {

    private static ThreadLocal Instance = new ThreadLocal();

    /**
     * Return a Facelet instance as specified by the file at the passed URI.
     * 
     * @param uri
     * @return
     * @throws IOException
     * @throws FaceletException
     * @throws FacesException
     * @throws ELException
     */
    public abstract Facelet getFacelet(String uri) throws IOException,
            FaceletException, FacesException, ELException;
    
    /**
     * Return a Facelet instance as specified by the file at the passed URL.
     * 
     * @param uri
     * @return
     * @throws IOException
     * @throws FaceletException
     * @throws FacesException
     * @throws ELException
     */
    public abstract Facelet getFacelet(URL url) throws IOException,
            FaceletException, FacesException, ELException;

    /**
     * Set the static instance
     * 
     * @param factory
     */
    public static final void setInstance(FaceletFactory factory) {
        Instance.set(factory);
    }

    /**
     * Get the static instance
     * 
     * @return
     */
    public static final FaceletFactory getInstance() {
        return (FaceletFactory) Instance.get();
    }
}
