package com.charon.encryption.request;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Http请求包装器
 *
 * @author charon
 * @date 2025/3/31 19:17
 */
public class HttpRequestWrapper extends HttpServletRequestWrapper {

    private byte[] body;

    public HttpRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);

        if (request.getContentLength() > 0) {
            this.body = readBytes(request.getReader());
        } else {
            this.body = new byte[0];
        }
    }

    public HttpRequestWrapper(HttpServletRequest request, String body) {
        super(request);
        this.body = body.getBytes();
    }

    public String getBody() {
        return new String(body);
    }

    public void setBody(String body) {
        this.body = body.getBytes();
    }

    @Override
    public ServletInputStream getInputStream() {
        return new ByteArrayServletInputStream(body);
    }

    @Override
    public BufferedReader getReader() {
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }

    private byte[] readBytes(BufferedReader reader) throws IOException {
        StringBuilder sb = new StringBuilder();
        char[] buffer = new char[1024];
        int bytesRead;
        while ((bytesRead = reader.read(buffer)) != -1) {
            sb.append(buffer, 0, bytesRead);
        }
        return sb.toString().getBytes();
    }

    private static class ByteArrayServletInputStream extends ServletInputStream {
        private final ByteArrayInputStream buffer;

        public ByteArrayServletInputStream(byte[] body) {
            this.buffer = new ByteArrayInputStream(body);
        }

        @Override
        public int read() {
            return buffer.read();
        }

        @Override
        public boolean isFinished() {
            return buffer.available() == 0;
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setReadListener(ReadListener readListener) {

        }
    }
}
