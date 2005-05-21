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

/**
 * Representation of a Tag in the Facelet definition
 * 
 * @author Jacob Hookom
 * @version $Id: Tag.java,v 1.1 2005/05/21 17:54:38 jhook Exp $
 */
public final class Tag {
    protected final TagAttributes attributes;

    protected final Location location;

    protected final String namespace;

    protected final String localName;

    protected final String qName;

    public Tag(Location location, String namespace, String localName,
            String qName, TagAttributes attributes) {
        this.location = location;
        this.namespace = namespace;
        this.localName = localName;
        this.qName = qName;
        this.attributes = attributes;
    }

    /**
     * All TagAttributes specified
     * 
     * @return all TagAttributes specified
     */
    public TagAttributes getAttributes() {
        return attributes;
    }

    /**
     * Local name of the tag &lt;my:tag /> would be "tag"
     * 
     * @return local name of the tag
     */
    public String getLocalName() {
        return localName;
    }

    /**
     * Location of the Tag in the Facelet file
     * 
     * @return location of the Tag in the Facelet file
     */
    public Location getLocation() {
        return location;
    }

    /**
     * The resolved Namespace for this tag
     * 
     * @return the resolved namespace for this tag
     */
    public String getNamespace() {
        return namespace;
    }

    /**
     * Get the qualified name for this tag &lt;my:tag /> would be "my:tag"
     * 
     * @return qualified name of the tag
     */
    public String getQName() {
        return qName;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return this.location + " <" + this.qName + ">";
    }
}
