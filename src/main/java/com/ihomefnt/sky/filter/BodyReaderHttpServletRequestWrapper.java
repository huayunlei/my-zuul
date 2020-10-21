package com.ihomefnt.sky.filter;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import com.netflix.zuul.http.ServletInputStreamWrapper;

import java.io.IOException;

/**
 * 防止 body参数 被 filter 捕捉后 controller里就不捉不到
 */
public class BodyReaderHttpServletRequestWrapper extends HttpServletRequestWrapper {

    private final byte[] body;

    public BodyReaderHttpServletRequestWrapper(HttpServletRequest request, byte[] body) throws IOException {
        super(request);
        this.body = body;
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
    	return new ServletInputStreamWrapper(body);
    }

    @Override
    public int getContentLength() {
      return body.length;
    }
    @Override
    public long getContentLengthLong() {
      return body.length;
    }

}
