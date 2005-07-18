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

package com.sun.facelets.mock;

import java.io.IOException;
import java.net.URL;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * 
 * @author Jacob Hookom
 * @version $Id: MockRequestDispatcher.java,v 1.1 2005/07/18 08:25:42 jhook Exp $
 */
public class MockRequestDispatcher implements RequestDispatcher {

    protected final URL url;
    
    public MockRequestDispatcher(URL url) {
        this.url = url;
    }

    public void forward(ServletRequest request, ServletResponse response)
            throws ServletException, IOException {
        // TODO Auto-generated method stub

    }

    public void include(ServletRequest request, ServletResponse response)
            throws ServletException, IOException {
        // TODO Auto-generated method stub

    }

}
