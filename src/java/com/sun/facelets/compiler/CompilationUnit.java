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
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.FunctionMapper;

import com.sun.facelets.FaceletException;
import com.sun.facelets.FaceletHandler;
import com.sun.facelets.el.ELText;
import com.sun.facelets.tag.Tag;
import com.sun.facelets.tag.TagAttribute;
import com.sun.facelets.tag.TagAttributeException;
import com.sun.facelets.tag.TagAttributes;
import com.sun.facelets.tag.TagDecorator;
import com.sun.facelets.tag.TagException;
import com.sun.facelets.tag.TagLibrary;
import com.sun.facelets.tag.ui.CompositionHandler;
import com.sun.facelets.tag.ui.UILibrary;
import com.sun.facelets.tag.ui.UITextHandler;

/**
 * Compilation unit for managing the creation of a single FaceletHandler
 * based on events from an XML parser.
 * 
 * @see com.sun.facelets.compiler.Compiler
 * 
 * @author Jacob Hookom
 * @version $Id: CompilationUnit.java,v 1.1 2005/05/21 17:54:49 jhook Exp $
 */
final class CompilationUnit {

    protected final static Logger log = Logger.getLogger("facelets.compiler");

    private static class State {

        public Tag tag = null;

        public boolean handled = false;

        public boolean startElementWritten = false;

        public boolean endElementWritten = false;

        public boolean fnMapper = false;

        public void reset() {
            this.tag = null;
            this.handled = false;
            this.startElementWritten = false;
            this.endElementWritten = false;
            this.fnMapper = false;
        }
    }

    private final Compiler compiler;

    private final TagLibrary library;

    private final TagDecorator decorator;

    private final NamespaceManager namespaces;

    private final ExpressionFactory elFactory;

    private final Stack elStack;

    protected final List states;

    protected int stateIdx = 0;

    private int handlerId;

    private final StringBuffer buffer;

    private final Stack configs;

    private final Configuration rootConfig;

    public CompilationUnit(Compiler compiler) {
        super();
        this.compiler = compiler;
        this.library = compiler.createTagLibrary();
        this.decorator = compiler.createTagDecorator();
        this.namespaces = new NamespaceManager();
        this.elFactory = compiler.createExpressionFactory();
        this.buffer = new StringBuffer(1024);
        this.states = new ArrayList(10);
        this.elStack = new Stack();
        this.elStack
                .push(new CompilationContext(this.namespaces, this.library));
        this.handlerId = 0;
        this.rootConfig = new Configuration(null, null);
        this.configs = new Stack();
        this.configs.push(this.rootConfig);
        // push root state on
        State s = this.pushState();
        s.handled = true;
        s.endElementWritten = true;
        s.startElementWritten = true;
    }

    private String nextTagHandlerId() {
        return "_tagId" + (this.handlerId++);
    }

    protected State pushState() {
        State s = null;
        if (stateIdx < this.states.size()) {
            s = (State) this.states.get(this.stateIdx);
        } else {
            s = new State();
            this.states.add(s);
        }
        s.reset();
        this.stateIdx++;
        return s;
    }

    protected State currentState() {
        return (State) this.states.get(this.stateIdx - 1);
    }

    protected State popState() {
        this.stateIdx--;
        return (State) this.states.get(this.stateIdx);
    }

    public FaceletHandler createFaceletHandler() {
        this.flushBufferToConfig();
        return this.rootConfig.getNextHandler();
    }

    private void flushStartElement() {
        // if in unprocessed element, finish it
        State s = this.currentState();
        if (!s.handled && !s.startElementWritten) {
            s.startElementWritten = true;
            this.buffer.append('>');
        }
    }

    public void writeComment(String text) {
        if (!this.compiler.isTrimmingComments()) {
            this.writeText("<!-- " + text + " -->");
        }
    }

    public void writeWhitespace(String text) {
        if (!this.compiler.isTrimmingWhitespace()) {
            this.writeText(text);
        }
    }

    public void writeText(String text) {
        this.flushStartElement();
        this.buffer.append(text);
    }

    protected void flushBufferToConfig() {
        if (this.buffer.length() > 0) {
            String s = this.buffer.toString();
            if (!this.compiler.isTrimmingWhitespace() || s.trim().length() > 0) {
                if (this.currentState().handled) {
                    s = trimRight(s);
                }
                if (s.length() > 0) {
                    ELText txt = ELText.parse(this.elFactory, this
                            .getELContext(), s);
                    if (txt != null) {
                        this.currentConfig().addHandler(new UITextHandler(txt));
                    }
                }
            }
            this.buffer.setLength(0);
        }
    }

    protected Configuration currentConfig() {
        return (Configuration) this.configs.peek();
    }

    protected final static String trimRight(String s) {
        int i = s.length() - 1;
        while (i >= 0 && Character.isWhitespace(s.charAt(i))) {
            i--;
        }
        if (i == s.length() - 1) {
            return s;
        } else if (i + 1 < s.length() && Character.isSpaceChar(s.charAt(i + 1))) {
            return s.substring(0, i + 2);
        } else {
            return s.substring(0, i + 1);
        }
    }

    public void pushTag(Tag orig) {
        // first, finish previous start elements
        this.flushStartElement();

        // process this element
        Tag t = this.decorator.decorate(orig);
        t = this.processAttributes(t);

        // validate element
        this.validateAttributes(t);

        // set the current state
        State s = this.pushState();
        s.tag = t;

        // if thise element is handled
        boolean handled = false;

        // check for root element
        if (isComposition(t)) {
            this.buffer.setLength(0);
            this.rootConfig.reset();
            this.configs.clear();
            this.configs.push(this.rootConfig);
            handled = true;
        } else if (isRemove(t)) {
            handled = true;
        } else {
            // check library
            handled = this.library.containsTagHandler(t.getNamespace(), t
                    .getLocalName());
        }

        // if this element is unhandled, then
        // write it out to the buffer for flushing
        if (handled) {
            // set the state
            s.handled = true;
            s.startElementWritten = true;
            s.endElementWritten = true;

            // finish writing unhandled start elements
            this.flushStartElement();

            // flush the buffer to the the tree
            // because we are going to add a new handler
            this.flushBufferToConfig();

            // now make us the current composition
            Configuration cfg = new Configuration(t, this.nextTagHandlerId());
            if (isRemove(t)) {
                cfg.close();
            }
            this.configs.push(cfg);
        } else {
            // set the state
            s.handled = false;
            s.startElementWritten = false;
            s.endElementWritten = false;

            // write the element to the buffer
            this.buffer.append('<');
            this.buffer.append(t.getQName());
            TagAttribute[] attrs = t.getAttributes().getAll();
            if (attrs.length > 0) {
                for (int i = 0; i < attrs.length; i++) {
                    this.buffer.append(' ').append(attrs[i].getQName()).append(
                            "=\"").append(attrs[i].getValue()).append("\"");
                }
            }
        }
    }

    public void popTag() {
        State s = this.currentState();
        Tag t = s.tag;
        if (t == null) {
            throw new IllegalStateException("No Element to Pop");
        }
        try {
            // process namespaces first
            if (s.fnMapper) {
                this.currentConfig().setFunctionMapper(this.popELContext());
            }

            if (s.handled) {
                // handle element

                // flush the buffer to current
                // element in the composition (e)
                this.flushBufferToConfig();

                // create the UI handler
                Configuration cfg = (Configuration) this.configs.pop();
                FaceletHandler h = null;
                if (isComposition(t)) {
                    h = new CompositionHandler(cfg);
                    // h = cfg.getNextHandler();
                } else if (!isRemove(t)) {
                    String ns = t.getNamespace();
                    String nm = t.getLocalName();
                    h = this.library.createTagHandler(ns, nm, cfg);
                    if (h == null) {
                        throw new FaceletException(
                                "Libraries are inconsistent, unable to create UIHandler for "
                                        + t);
                    }
                }
                if (h != null) {
                    this.currentConfig().addHandler(h);
                }
                if (isComposition(t)) {
                    this.currentConfig().close();
                }
            } else {
                if (!s.endElementWritten) {
                    if (!s.startElementWritten) {
                        this.buffer.append("/>");
                    } else {
                        this.buffer.append("</").append(t.getQName()).append(
                                ">");
                    }
                }
            }

            // finally pop our state
            this.popState();
        } catch (Exception e) {
            throw new TagException(t, e.getMessage(), e.getCause());
        }
    }

    protected static boolean isRemove(Tag t) {
        return UILibrary.Namespace.equals(t.getNamespace())
                && "remove".equals(t.getLocalName());
    }

    protected static boolean isComposition(Tag t) {
        return UILibrary.Namespace.equals(t.getNamespace())
                && CompositionHandler.Name.equals(t.getLocalName());
    }

    public void pushNamespace(String prefix, String namespace) {
        this.namespaces.pushNamespace(prefix, namespace);
    }

    public void popNamespace(String prefix) {
        this.namespaces.popNamespace(prefix);
    }

    private void pushELContext() {
        this.elStack
                .push(new CompilationContext(this.namespaces, this.library));
    }

    private FunctionMapper popELContext() {
        CompilationContext ctx = (CompilationContext) this.elStack.pop();
        if (ctx != null) {
            return ctx.createFunctionMapper();
        }
        return null;
    }

    private ELContext getELContext() {
        return (CompilationContext) this.elStack.peek();
    }

    private void validateAttributes(Tag tag) {
        TagAttribute[] attr = tag.getAttributes().getAll();
        ELContext ctx = this.getELContext();
        for (int i = 0; i < attr.length; i++) {
            try {
                this.elFactory.createValueExpression(ctx, attr[i].getValue(),
                        Object.class);
            } catch (Exception e) {
                throw new TagAttributeException(tag, attr[i], e);
            }
        }
    }

    private Tag processAttributes(Tag tag) {
        Tag t = this.processJSFC(tag);
        t = this.processAttributesNS(t);
        return t;
    }

    private Tag processJSFC(Tag tag) {
        TagAttribute attr = tag.getAttributes().get("jsfc");
        if (attr != null) {
            if (log.isLoggable(Level.FINE)) {
                log.fine(attr + " JSF Facelet Compile Directive Found");
            }
            String pre = attr.getValue();
            int c = pre.indexOf(':');
            if (c == -1) {
                throw new TagAttributeException(tag, attr,
                        "Must be in the form prefix:localName");
            } else {
                pre = pre.substring(0, c);
                String ns = this.namespaces.getNamespace(pre);
                if (ns == null) {
                    throw new TagAttributeException(tag, attr,
                            "No Namespace matched for: " + pre);
                }
                TagAttribute[] oa = tag.getAttributes().getAll();
                TagAttribute[] na = new TagAttribute[oa.length - 1];
                int p = 0;
                for (int i = 0; i < oa.length; i++) {
                    if (!"jsfc".equals(oa[i].getLocalName())) {
                        na[p++] = oa[i];
                    }
                }
                return new Tag(tag.getLocation(), ns, attr.getValue()
                        .substring(c + 1), tag.getQName(),
                        new TagAttributes(na));
            }
        }
        return tag;
    }

    private Tag processAttributesNS(Tag tag) {
        TagAttribute[] attr = tag.getAttributes().getAll();
        int remove = 0;
        for (int i = 0; i < attr.length; i++) {
            if (attr[i].getQName().startsWith("xmlns")
                    && this.library.containsNamespace(attr[i].getValue())) {
                remove |= 1 << i;
                if (log.isLoggable(Level.FINE)) {
                    log.fine(attr[i] + " Namespace Bound to TagLibrary");
                }
            }
        }
        if (remove == 0) {
            return tag;
        } else {
            this.currentState().fnMapper = true;
            this.pushELContext();
            List attrList = new ArrayList(attr.length);
            int p = 0;
            for (int i = 0; i < attr.length; i++) {
                p = 1 << i;
                if ((p & remove) == p) {
                    continue;
                }
                attrList.add(attr[i]);
            }
            attr = (TagAttribute[]) attrList.toArray(new TagAttribute[attrList
                    .size()]);
            return new Tag(tag.getLocation(), tag.getNamespace(), tag
                    .getLocalName(), tag.getQName(), new TagAttributes(attr));
        }
    }
}
