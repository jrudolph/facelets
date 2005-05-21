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

package com.sun.facelets.compiler;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jacob Hookom
 * @version $Id: NamespaceManager.java,v 1.1 2005/05/21 17:54:50 jhook Exp $
 */
final class NamespaceManager {

    private final static class NS {
        public final String prefix;

        public final String namespace;

        public NS(String prefix, String ns) {
            this.prefix = prefix;
            this.namespace = ns;
        }
    }

    protected final List namespaces;

    /**
     * 
     */
    public NamespaceManager() {
        this.namespaces = new ArrayList();
    }

    public void reset() {
        this.namespaces.clear();
    }

    public void pushNamespace(String prefix, String namespace) {
        NS ns = new NS(prefix, namespace);
        this.namespaces.add(0, ns);
    }

    public String getNamespace(String prefix) {
        NS ns = null;
        for (int i = 0; i < this.namespaces.size(); i++) {
            ns = (NS) this.namespaces.get(i);
            if (ns.prefix.equals(prefix)) {
                return ns.namespace;
            }
        }
        return null;
    }

    public void popNamespace(String prefix) {
        NS ns = null;
        for (int i = 0; i < this.namespaces.size(); i++) {
            ns = (NS) this.namespaces.get(i);
            if (ns.prefix.equals(prefix)) {
                this.namespaces.remove(i);
                return;
            }
        }
    }

}
