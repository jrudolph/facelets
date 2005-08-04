package com.sun.facelets.util;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.el.Expression;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.el.MethodBinding;
import javax.faces.el.ValueBinding;

public final class DevTools {
    
    private final static String TS = "&lt;";
    
    private static final String TEMPLATE = "META-INF/rsc/facelet-dev-error.xml";
    
    private static String[] parts;

    public DevTools() {
        super();
    }
    
    public static void main(String[] argv) throws Exception {
        DevTools.init();
        System.out.println(parts.length);
    }
    
    private static void init() throws IOException {
        if (parts == null) {
            InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(TEMPLATE);
            if (is == null) {
                throw new FileNotFoundException(TEMPLATE);
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buff = new byte[512];
            int read;
            while ((read = is.read(buff)) != -1) {
                baos.write(buff, 0, read);
            }
            String str = baos.toString();
            parts = str.split("@@");
        }
    }
    
    public static void debugHtml(PrintWriter writer, FacesContext faces, Exception e) throws IOException {
        init();
        Date now = new Date();
        for (int i = 0; i < parts.length; i++) {
            if ("message".equals(parts[i])) {
                String msg = e.getMessage();
                if (msg != null) {
                    writer.write(msg.replaceAll("<", TS));
                } else {
                    writer.write(e.getClass().getName());
                }
            } else if ("trace".equals(parts[i])) {
                StringWriter str = new StringWriter(256);
                PrintWriter pstr = new PrintWriter(str);
                e.printStackTrace(pstr);
                pstr.close();
                writer.write(str.toString().replaceAll("<", TS));
            } else if ("now".equals(parts[i])) {
                writer.write(DateFormat.getDateTimeInstance().format(now));
            } else if ("tree".equals(parts[i])) {
                writeComponent(writer, faces.getViewRoot());
            } else if ("vars".equals(parts[i])) {
                writeVariables(writer, faces);
            } else {
                writer.write(parts[i]);
            }
        }
    }
    
    private static void writeVariables(PrintWriter writer, FacesContext faces) throws IOException {
        ExternalContext ctx = faces.getExternalContext();
        writeVariables(writer, ctx.getRequestParameterMap(), "Request Parameters");
        writeVariables(writer, ctx.getRequestMap(), "Request Attributes");
        if (ctx.getSession(false) != null) {
            writeVariables(writer, ctx.getSessionMap(), "Session Attributes");
        }
        writeVariables(writer, ctx.getApplicationMap(), "Application Attributes");
    }
    
    private static void writeVariables(PrintWriter writer, Map vars, String caption) throws IOException {
        writer.write("<table><caption>");
        writer.write(caption);
        writer.write("</caption><thead><tr><th style=\"width: 10%; \">Name</th><th style=\"width: 90%; \">Value</th></tr></thead><tbody>");
        boolean written = false;
        if (!vars.isEmpty()) {
            SortedMap map = new TreeMap(vars);
            Map.Entry entry = null;
            String key = null;
            for (Iterator itr = map.entrySet().iterator(); itr.hasNext(); ) {
                entry = (Map.Entry) itr.next();
                key = entry.getKey().toString();
                if (key.indexOf('.') == -1) {
                    writer.write("<tr><td>");
                    writer.write(key.replaceAll("<", TS));
                    writer.write("</td><td>");
                    writer.write(entry.getValue().toString().replaceAll("<", TS));
                    writer.write("</td></tr>");
                    written = true;
                }
            }
        }
        if (!written) {
            writer.write("<tr><td colspan=\"2\"><em>None</em></td></tr>");
        }
        writer.write("</tbody></table>");
    }
    
    private static void writeComponent(PrintWriter writer, UIComponent c) throws IOException {
        writer.write("<dl><dt");
        if (isText(c)) {
            writer.write(" class=\"uicText\"");
        }
        writer.write(">");
        
        boolean hasChildren = c.getChildCount() > 0 || c.getFacets().size() > 0;
        
        writeStart(writer, c, hasChildren);
        writer.write("</dt>");
        if (hasChildren) {
            if (c.getFacets().size() > 0) {
                Map.Entry entry;
                for (Iterator itr = c.getFacets().entrySet().iterator(); itr.hasNext(); ) {
                    entry = (Map.Entry) itr.next();
                    writer.write("<dd class=\"uicFacet\">");
                    writer.write("<span>");
                    writer.write((String) entry.getKey());
                    writer.write("</span>");
                    writeComponent(writer, (UIComponent) entry.getValue());
                    writer.write("</dd>");
                }
            }
            if (c.getChildCount() > 0) {
                for (Iterator itr = c.getChildren().iterator(); itr.hasNext(); ) {
                    writer.write("<dd>");
                    writeComponent(writer, (UIComponent) itr.next());
                    writer.write("</dd>");
                }
            }
            writer.write("<dt>");
            writeEnd(writer, c);
            writer.write("</dt>");
        }
        writer.write("</dl>");
    }
    
    private static void writeEnd(PrintWriter writer, UIComponent c) {
        if (!isText(c)) {
            writer.write(TS);
            writer.write('/');
            writer.write(getName(c));
            writer.write('>');
        }
    }
    
    private final static String[] IGNORE = new String[] { "parent", "rendererType" };
    
    private static void writeAttributes(PrintWriter writer, UIComponent c) {
        try {
            BeanInfo info = Introspector.getBeanInfo(c.getClass());
            PropertyDescriptor[] pd = info.getPropertyDescriptors();
            Method m = null;
            Object v = null;
            String str = null;
            for (int i = 0; i < pd.length; i++) {
                if (pd[i].getWriteMethod() != null && Arrays.binarySearch(IGNORE, pd[i].getName()) < 0) {
                    m = pd[i].getReadMethod();
                    try {
                        v = m.invoke(c, null);
                        if (v != null) {
                            if (v instanceof Collection || v instanceof Map || v instanceof Iterator) {
                                continue;
                            }
                            writer.write(" ");
                            writer.write(pd[i].getName());
                            writer.write("=\"");
                            if (v instanceof Expression) {
                                str = ((Expression) v).getExpressionString();
                            } else if (v instanceof ValueBinding) {
                                str = ((ValueBinding) v).getExpressionString();
                            } else if (v instanceof MethodBinding) {
                                str = ((MethodBinding) v).getExpressionString();
                            } else {
                                str = v.toString();
                            }
                            writer.write(str.replaceAll("<", TS));
                            writer.write("\"");
                        }
                    } catch (Exception e) {
                        // do nothing
                    }
                }
            }
        } catch (Exception e) {
            // do nothing
        }
    }
    
    private static void writeStart(PrintWriter writer, UIComponent c, boolean children) {
        if (isText(c)) {
            String str = c.toString().trim();
            writer.write(str.replaceAll("<", TS));
        } else {
            writer.write(TS);
            writer.write(getName(c));
            writeAttributes(writer, c);
            if (children) {
                writer.write('>');
            } else {
                writer.write("/>");
            }
        }
    }
    
    private static String getName(UIComponent c) {
        String nm = c.getClass().getName();
        return nm.substring(nm.lastIndexOf('.') + 1);
    }
    
    private static boolean isText(UIComponent c) {
        return (c.getClass().getName().startsWith("com.sun.facelets.compiler"));
    }

}