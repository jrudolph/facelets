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

package com.sun.facelets.tag.core;

import javax.faces.validator.DoubleRangeValidator;
import javax.faces.validator.Validator;

import com.sun.facelets.FaceletContext;
import com.sun.facelets.tag.TagConfig;
import com.sun.facelets.tag.ValidateHandler;

/**
 * Register a DoubleRangeValidator instance on the UIComponent associated with
 * the closest parent UIComponent custom action. <p/> See <a target="_new"
 * href="http://java.sun.com/j2ee/javaserverfaces/1.1_01/docs/tlddocs/f/validateDoubleRange.html">tag
 * documentation</a>.
 * 
 * @author Jacob Hookom
 * @version $Id: ValidateDoubleRangeHandler.java,v 1.2 2005/07/20 19:31:47 jhook Exp $
 */
public final class ValidateDoubleRangeHandler extends ValidateHandler {

    public ValidateDoubleRangeHandler(TagConfig config) {
        super(config);
    }

    /**
     * Returns a new DoubleRangeValidator
     * 
     * @see DoubleRangeValidator
     * @see com.sun.facelets.tag.ValidateHandler#createValidator(com.sun.facelets.FaceletContext)
     */
    protected Validator createValidator(FaceletContext ctx) {
        return ctx.getFacesContext().getApplication().createValidator(DoubleRangeValidator.VALIDATOR_ID);
    }
}
