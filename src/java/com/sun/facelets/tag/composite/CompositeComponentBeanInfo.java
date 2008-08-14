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

import java.beans.BeanDescriptor;
import java.beans.BeanInfo;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author edburns
 */
public class CompositeComponentBeanInfo extends SimpleBeanInfo implements BeanInfo {

    private BeanDescriptor descriptor = null;
    
    @Override
    public BeanDescriptor getBeanDescriptor() {
        return descriptor;
    }
    
    public void setBeanDescriptor(BeanDescriptor newDescriptor) {
        descriptor = newDescriptor;
    }

    @Override
    public PropertyDescriptor[] getPropertyDescriptors() {
        List<PropertyDescriptor> list = getPropertyDescriptorsList();
        PropertyDescriptor [] result = new PropertyDescriptor[list.size()];
        list.toArray(result);
        return result;
    }

    private List<PropertyDescriptor> propertyDescriptors;
    public List<PropertyDescriptor> getPropertyDescriptorsList() {

        if (null == propertyDescriptors) {
            propertyDescriptors = new ArrayList<PropertyDescriptor>();
        }
        return propertyDescriptors;
    }
    
    
}
