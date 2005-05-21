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

package com.sun.facelets.spi;

import java.net.URL;

import com.sun.facelets.FaceletException;
import com.sun.facelets.compiler.Compiler;

/**
 * Using the passed period (in seconds), determine if a Facelet instance
 * needs to be re-created based on the last time the source URL was modified.
 *
 * @see java.net.URLConnection#getLastModified()
 * 
 * @author Jacob Hookom
 * @version $Id: RefreshableFaceletFactory.java,v 1.1 2005/05/21 17:54:59 jhook Exp $
 */
public class RefreshableFaceletFactory extends DefaultFaceletFactory {

    protected final long refreshPeriod;

    /**
     * @param compiler
     */
    public RefreshableFaceletFactory(Compiler compiler, URL root,
            long refreshPeriod) {
        super(compiler, root);
        this.refreshPeriod = (refreshPeriod > 0) ? refreshPeriod * 1000 : -1;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sun.facelets.defaults.DefaultFaceletFactory#needsToBeRefreshed(com.sun.facelets.defaults.DefaultFacelet,
     *      java.lang.String)
     */
    protected boolean needsToBeRefreshed(DefaultFacelet facelet) {
        if (this.refreshPeriod != -1) {
            long ttl = facelet.getCreateTime() + this.refreshPeriod;
            if (System.currentTimeMillis() > ttl) {
                try {
                    long atl = facelet.getSource().openConnection()
                            .getLastModified();
                    return atl > ttl;
                } catch (Exception e) {
                    throw new FaceletException(
                            "Error Checking Last Modified for "
                                    + facelet.getAlias(), e);
                }
            }
        }
        return false;
    }
}
