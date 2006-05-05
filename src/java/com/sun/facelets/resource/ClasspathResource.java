package com.sun.facelets.resource;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import javax.faces.FacesException;

import com.sun.facelets.FaceletException;

public final class ClasspathResource implements Resource {

    private final URL url;

    private final String mimetype;

    public ClasspathResource(URL url, String mimetype) {
        this.url = url;
        this.mimetype = mimetype;
    }

    public long getLastModified() {
        URLConnection conn = null;
        long when = -1;
        try {
            conn = this.url.openConnection();
            when = conn.getLastModified();
        } catch (IOException e) {
            throw new FaceletException(e);
        } finally {
            if (conn != null) {
                try {
                    conn.getInputStream().close();
                } catch (IOException e) {
                }
            }
        }
        return when;
    }

    public String getMimeType() {
        return this.mimetype;
    }

    public void write(OutputStream out) throws IOException, FacesException {
        InputStream in = url.openStream();
        try {
            byte[] buffer = new byte[256];
            while (true) {
                int bytesRead = in.read(buffer);
                if (bytesRead == -1)
                    break;
                out.write(buffer, 0, bytesRead);
            }
        } finally {
            in.close();
        }
    }
}
