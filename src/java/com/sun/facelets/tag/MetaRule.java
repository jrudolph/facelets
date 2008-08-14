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


package com.sun.facelets.tag;

/**
 * A potential rule for Metadata on the passed MetadataTarget
 * 
 * @see com.sun.facelets.tag.Metadata
 * @see com.sun.facelets.tag.MetadataTarget
 * @author Jacob Hookom
 * @version $Id: MetaRule.java,v 1.2.28.1 2008/08/14 21:11:36 rlubke Exp $
 */
public abstract class MetaRule {

    /**
     * @param name
     * @param attribute
     * @param meta
     * @return
     */
    public abstract Metadata applyRule(String name, TagAttribute attribute,
            MetadataTarget meta);

}
