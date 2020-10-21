package com.ihomefnt.sky.common.util;

import com.netflix.zuul.context.RequestContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author huayunlei
 * @created 2018年11月26日 下午4:34:35
 * @desc zuul response header 公共处理类
 */
public class ZuulResponseHeaderCommonUtil {

	public static void setCommonHeaders(RequestContext ctx) {
		ctx.addZuulResponseHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE);
        ctx.addZuulResponseHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
        ctx.addZuulResponseHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
        ctx.addZuulResponseHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, "GET, POST, HEAD, PUT, DELETE");
        ctx.addZuulResponseHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, "Accept, Origin, X-Requested-With, Content-Type, Last-Modified");
	}
	
	public static void setCommonResponseHeaders(HttpServletResponse response, HttpServletRequest request) {
		response.setCharacterEncoding("UTF-8");
		response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
		response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, request.getHeader("Origin"));
        response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
        response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, "GET, POST, HEAD, PUT, DELETE");
        response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, "Accept, Origin, X-Requested-With, Content-Type, Last-Modified");
	}

}
