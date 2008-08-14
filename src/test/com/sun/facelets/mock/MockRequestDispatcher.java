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
 * @version $Id: MockRequestDispatcher.java,v 1.1.28.1 2008/08/14 21:12:12 rlubke Exp $
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
