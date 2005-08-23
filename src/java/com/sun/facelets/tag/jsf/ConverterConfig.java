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

package com.sun.facelets.tag.jsf;

import com.sun.facelets.tag.TagConfig;

/**
 * Used in creating ConvertHandler's and all implementations.
 * 
 * @see com.sun.facelets.tag.jsf.ConvertHandler
 * @author Jacob Hookom
 * @version $Id: ConverterConfig.java,v 1.1 2005/08/15 03:56:51 jhook Exp $
 */
public interface ConverterConfig extends TagConfig {

    /**
     * The converter id to be used in instantiating this converter
     * @return the converter id that can be passed to Application.createConverter
     */
    public String getConverterId();
    
}