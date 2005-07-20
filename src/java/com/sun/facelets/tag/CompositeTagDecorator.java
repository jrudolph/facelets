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

import com.sun.facelets.util.Assert;

/**
 * A TagDecorator that is composed of 1 or more TagDecorator instances. It uses
 * the chain of responsibility pattern to stop processing if any of the
 * TagDecorators return a value other than null.
 * 
 * @author Jacob Hookom
 * @version $Id: CompositeTagDecorator.java,v 1.2 2005/07/20 06:37:06 jhook Exp $
 */
public final class CompositeTagDecorator implements TagDecorator {

    private final TagDecorator[] decorators;

    public CompositeTagDecorator(TagDecorator[] decorators) {
        Assert.param("decorators", decorators);
        this.decorators = decorators;
    }

    /**
     * Uses the chain of responsibility pattern to stop processing if any of
     * the TagDecorators return a value other than null.
     * 
     * @see com.sun.facelets.tag.TagDecorator#decorate(com.sun.facelets.tag.Tag)
     */
    public Tag decorate(Tag tag) {
        Tag t = null;
        for (int i = 0; i < this.decorators.length; i++) {
            t = this.decorators[i].decorate(tag);
            if (t != null) {
                return t;
            }
        }
        return tag;
    }

}
