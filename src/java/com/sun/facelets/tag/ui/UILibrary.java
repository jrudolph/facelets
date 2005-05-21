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

package com.sun.facelets.tag.ui;

import com.sun.facelets.tag.AbstractTagLibrary;

/**
 * @author Jacob Hookom
 * @version $Id: UILibrary.java,v 1.1 2005/05/21 17:54:57 jhook Exp $
 */
public final class UILibrary extends AbstractTagLibrary {

    public final static String Namespace = "http://java.sun.com/jsf/facelet";

    public final static UILibrary Instance = new UILibrary();

    public UILibrary() {
        super(Namespace);

        this.addTagHandler("include", IncludeHandler.class);

        this.addTagHandler("composition", CompositionHandler.class);

        this.addTagHandler("define", DefineHandler.class);

        this.addTagHandler("insert", InsertHandler.class);

        this.addTagHandler("param", ParamHandler.class);

        this.addTagHandler("decorate", DecorateHandler.class);
    }
}
