package com.sun.facelets.webapp;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

public class AsyncResponseWrapper extends HttpServletResponseWrapper {

    protected HttpServletResponse origResponse = null;

    protected ServletOutputStream stream = null;

    protected PrintWriter writer = null;

    protected OutputStream cache = null;

    public AsyncResponseWrapper(HttpServletResponse resp, OutputStream cache) {
        super(resp);
        this.cache = cache;
    }

    private ServletOutputStream createOutputStream() throws IOException {
        return (new AsyncResponseStream(this.cache));
    }

    public void flushBuffer() throws IOException {
        if (this.writer != null) {
            this.writer.flush();
        }
        if (this.stream != null) {
            this.stream.flush();
        }
    }
    
    public void closeBuffer() throws IOException {
        if (this.writer != null) {
            this.writer.close();
        }
        if (this.stream != null) {
            this.stream.close();
        }
    }

    public ServletOutputStream getOutputStream() throws IOException {
        if (this.writer != null) {
            throw new IllegalStateException(
                    "getWriter() has already been called!");
        }
        if (this.stream == null)
            this.stream = createOutputStream();
        return this.stream;
    }

    public PrintWriter getWriter() throws IOException {
        if (this.writer != null) {
            return (this.writer);
        }

        this.stream = createOutputStream();
        this.writer = new PrintWriter(new OutputStreamWriter(stream));
        return (this.writer);
    }

}
