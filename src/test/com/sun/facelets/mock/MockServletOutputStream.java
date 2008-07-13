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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletOutputStream;

/**
 * 
 * @author Jacob Hookom
 * @version $Id: MockServletOutputStream.java,v 1.2 2008/07/13 19:01:48 rlubke Exp $
 */
public class MockServletOutputStream extends ServletOutputStream {

    private final OutputStream dest;
    
    public MockServletOutputStream() {
        this.dest = new ByteArrayOutputStream();
    }
    
    public MockServletOutputStream(OutputStream dest) {
        this.dest = dest;
    }

    public void write(int b) throws IOException {
        this.dest.write(b);
    }
}
