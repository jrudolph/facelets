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

package com.sun.facelets.tag;

import com.sun.facelets.FaceletContext;
import com.sun.facelets.util.ParameterCheck;

/**
 * A base tag for wiring state to an object instance based on rules populated at
 * the time of creating a MetaRuleset.
 * 
 * @author Jacob Hookom
 * @version $Id: MetaTagHandler.java,v 1.1 2005/07/27 04:32:54 jhook Exp $
 */
public abstract class MetaTagHandler extends TagHandler {

    private Class lastType = Object.class;

    private Metadata mapper;

    public MetaTagHandler(TagConfig config) {
        super(config);
    }

    /**
     * Extend this method in order to add your own rules.
     * 
     * @param type
     * @return
     */
    protected MetaRuleset createMetaRuleset(Class type) {
        ParameterCheck.notNull("type", type);
        return new MetaRulesetImpl(this.tag, type);
    }

    /**
     * Invoking/extending this method will cause the results of the created
     * MetaRuleset to auto-wire state to the passed instance.
     * 
     * @param ctx
     * @param instance
     */
    protected void setAttributes(FaceletContext ctx, Object instance) {
        if (instance != null) {
            Class type = instance.getClass();
            if (mapper == null || !this.lastType.equals(type)) {
                this.lastType = type;
                this.mapper = this.createMetaRuleset(type).finish();
            }
            this.mapper.applyMetadata(ctx, instance);
        }
    }
}
