package com.ihomefnt.sky.common.exception;

import com.netflix.zuul.exception.ZuulException;

public class ZuulExceptionUtil {

	public static ZuulException findZuulException(Throwable throwable) {
        if (BusinessException.class.isInstance(throwable.getCause())) {
            return (ZuulException)throwable.getCause();
        } else if (ZuulException.class.isInstance(throwable.getCause())) {
            return (ZuulException)throwable.getCause();
        } else {
            return ZuulException.class.isInstance(throwable) ? (ZuulException)throwable : new ZuulException(throwable, 500, (String)null);
        }
    }
}
