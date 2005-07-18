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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletInputStream;

/**
 * 
 * @author Jacob Hookom
 * @version $Id: MockServletInputStream.java,v 1.1 2005/07/18 08:25:42 jhook Exp $
 */
public class MockServletInputStream extends ServletInputStream {

    private final InputStream source;
    
    public MockServletInputStream() {
        this.source = new ByteArrayInputStream(new byte[0]);
    }
    
    public MockServletInputStream(InputStream source) {
        this.source = source;
    }

    public int read() throws IOException {
        return this.source.read();
    }

}
