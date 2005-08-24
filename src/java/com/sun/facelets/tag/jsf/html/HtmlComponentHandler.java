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

package com.sun.facelets.tag.jsf.html;

import com.sun.facelets.tag.MetaRuleset;
import com.sun.facelets.tag.jsf.ComponentConfig;
import com.sun.facelets.tag.jsf.ComponentHandler;

/**
 * @author Jacob Hookom
 * @version $Id: HtmlComponentHandler.java,v 1.2 2005/08/24 04:38:45 jhook Exp $
 */
public class HtmlComponentHandler extends ComponentHandler {

    /**
     * @param config
     */
    public HtmlComponentHandler(ComponentConfig config) {
        super(config);
    }

    protected MetaRuleset createMetaRuleset(Class type) {
        return super.createMetaRuleset(type).alias("class", "styleClass");
    }

}
