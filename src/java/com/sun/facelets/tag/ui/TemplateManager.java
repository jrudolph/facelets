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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.sun.facelets.FaceletContext;
import com.sun.facelets.FaceletHandler;

/**
 * @author Jacob Hookom
 * @version $Id: TemplateManager.java,v 1.2 2005/07/20 06:37:11 jhook Exp $
 */
final class TemplateManager implements TemplateClient {

    private final static String KEY = TemplateManager.class.getName();

    private final List clients;

    private TemplateClient currentClient;

    /**
     * 
     */
    private TemplateManager() {
        this.clients = new ArrayList(5);
    }

    public void pushClient(TemplateClient client) {
        if (this.currentClient != null) {
            this.clients.add(this.currentClient);
        }
        this.currentClient = client;
    }

    public void popClient() {
        if (!this.clients.isEmpty()) {
            this.currentClient = (TemplateClient) this.clients
                    .remove(this.clients.size() - 1);
        } else {
            this.currentClient = null;
        }
    }

    public static TemplateManager getInstance(FaceletContext ctx) {
        Map req = ctx.getFacesContext().getExternalContext().getRequestMap();
        TemplateManager mngr = (TemplateManager) req.get(KEY);
        if (mngr == null) {
            mngr = new TemplateManager();
            req.put(KEY, mngr);
        }
        return mngr;
    }

    public FaceletHandler getHandler(FaceletContext ctx, String name) {
        if (this.currentClient != null) {
            return this.currentClient.getHandler(ctx, name);
        }
        return null;
    }

}
