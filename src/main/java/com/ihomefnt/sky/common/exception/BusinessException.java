package com.ihomefnt.sky.common.exception;

import com.ihomefnt.sky.common.constant.HttpResponseConstants;

public class BusinessException extends RuntimeException{

	private long code;

	public BusinessException(long code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }
	
    public BusinessException(long code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(String message) {
        super(message);
        this.code = HttpResponseConstants.SYSTEM_FAILED.getCode();
    }

    public long getCode() {
        return code;
    }

    public void setCode(long code) {
        this.code = code;
    }

}
