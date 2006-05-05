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

package com.sun.facelets.compiler;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.el.ELException;
import javax.el.ExpressionFactory;
import javax.faces.FacesException;
import javax.faces.context.FacesContext;

import com.sun.facelets.FaceletException;
import com.sun.facelets.FaceletHandler;
import com.sun.facelets.config.FaceletConfig;
import com.sun.facelets.config.TagLibraryConfig;
import com.sun.facelets.tag.CompositeTagDecorator;
import com.sun.facelets.tag.CompositeTagLibrary;
import com.sun.facelets.tag.TagDecorator;
import com.sun.facelets.tag.TagLibrary;
import com.sun.facelets.tag.ui.UILibrary;
import com.sun.facelets.util.ParameterCheck;
import com.sun.facelets.util.FacesAPI;

/**
 * A Compiler instance may handle compiling multiple sources
 * 
 * @author Jacob Hookom
 * @version $Id: Compiler.java,v 1.14.2.1 2006/05/05 06:49:48 jhook Exp $
 */
public abstract class Compiler {
    
    private final FaceletConfig config;

    /**
     * 
     */
    public Compiler(FaceletConfig config) {
        this.config = config;
    }
    
    protected FaceletConfig getFaceletConfig() {
        return this.config;
    }

    public abstract FaceletHandler compile(URL src, String alias)
            throws IOException, FaceletException, ELException, FacesException;

}
