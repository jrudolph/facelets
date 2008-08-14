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


package com.sun.facelets.tag.composite;

import java.util.ArrayList;
import java.util.List;
import javax.faces.component.UIComponent;
import javax.faces.webapp.pdl.AttachedObjectTarget;

/**
 *
 * @author edburns
 */
public class AttachedObjectTargetImpl implements AttachedObjectTarget {

    private String name = null;
    public String getName() {
        return name;
    }
    
    void setName(String name) {
        this.name = name;
    }

    UIComponent component = null;

    public void setComponent(UIComponent component) {
        this.component = component;
    }

    

    private List<UIComponent> targets;
    public List<UIComponent> getTargets() {
        
        if (null == targets) {
            targets = new ArrayList<UIComponent>();
            populateTargets();
        }
        
        return targets;
    }
    
    private String targetsList;
    
    void setTargetsList(String targetsList) {
        this.targetsList = targetsList;
    }
    
    private void populateTargets() {
        assert(null != name);
        assert(null != targets);
        assert (null != component);
        UIComponent comp = null;
        if (null != targetsList) {
            String [] targetArray = targetsList.split(",");
            for (int i = 0; i < targetArray.length; i++) {
                comp = component.findComponent(targetArray[i]);
                if (null != comp) {
                    targets.add(comp);
                }
            }
        }
        else {
            comp = component.findComponent(name);
            if (null != comp) {
                targets.add(comp);
            }
        }
    }
    

}
