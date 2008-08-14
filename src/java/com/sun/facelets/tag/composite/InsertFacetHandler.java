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

import com.sun.facelets.FaceletContext;
import com.sun.facelets.FaceletException;
import com.sun.facelets.tag.TagAttribute;
import com.sun.facelets.tag.jsf.ComponentConfig;
import com.sun.facelets.tag.jsf.ComponentHandler;
import com.sun.facelets.tag.jsf.ComponentSupport;
import java.io.IOException;
import javax.el.ELException;
import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.component.UIOutput;
import javax.faces.context.FacesContext;

/**
 *
 * @author edburns
 */
public class InsertFacetHandler extends ComponentHandler {

    private TagAttribute name = null;

    public InsertFacetHandler(ComponentConfig config) {
        super(config);
        this.name = this.getRequiredAttribute("name");
    }

    @Override
    protected void onComponentCreated(FaceletContext ctx, UIComponent c, UIComponent parent) {
        ValueExpression ve = null;
        String strValue = null;
        // Get the value of required the name propertyDescriptor
        ve = name.getValueExpression(ctx, String.class);
        strValue = (String) ve.getValue(ctx);
        
        c.getAttributes().put(UIComponent.FACETS_KEY, strValue);
        
    }

}
