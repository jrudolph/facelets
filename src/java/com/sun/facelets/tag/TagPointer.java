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
package com.sun.facelets.tag;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;


/**
 * @author Jacob Hookom
 *
 */
public final class TagPointer implements Externalizable {
    
    public final static String ATTR_NAME = TagPointer.class.getName();
    
    private long createTime;
    
    private int faceletId;
    
    private int tagId;
    
    public TagPointer() {}
    
    public TagPointer(String faceletId, String tagId, long createTime) {
        
    }
    
    public TagPointer(int faceletId, int tagId, long createTime) {
        this.faceletId = faceletId;
        this.tagId = tagId;
        this.createTime = createTime;
    }

    /* (non-Javadoc)
     * @see java.io.Externalizable#writeExternal(java.io.ObjectOutput)
     */
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeLong(this.createTime);
        out.writeInt(this.faceletId);
        out.writeInt(this.tagId);
    }

    /* (non-Javadoc)
     * @see java.io.Externalizable#readExternal(java.io.ObjectInput)
     */
    public void readExternal(ObjectInput in) throws IOException,
            ClassNotFoundException {
        this.createTime = in.readLong();
        this.faceletId = in.readInt();
        this.tagId = in.readInt();
    }

    public long getCreateTime() {
        return createTime;
    }

    public int getFaceletId() {
        return faceletId;
    }

    public int getTagId() {
        return tagId;
    }

}
