package com.ihomefnt.sky.common.util;

import com.ihomefnt.sky.common.constant.HttpResponseConstants;
import com.ihomefnt.sky.common.http.HttpBaseResponse;
import com.ihomefnt.sky.common.http.HttpMessage;

public class ResponseCommonUtil {

	/**
	 * 返回成功
	 * @param obj
	 * @return
	 */
	public static HttpBaseResponse returnSuccess(Object obj){
		HttpBaseResponse baseResponse = new HttpBaseResponse();
		baseResponse.setCode(HttpResponseConstants.SUCCESS.getCode());
		baseResponse.setObj(obj);
		baseResponse.setExt(HttpResponseConstants.SUCCESS.getMsg());
		return baseResponse;
	}
	
	/**
	 * 返回失败
	 * @return
	 */
	public static HttpBaseResponse returnFail(String errorMsg) {
		HttpBaseResponse baseResponse = new HttpBaseResponse();
		baseResponse.setCode(HttpResponseConstants.SYSTEM_FAILED.getCode());
		HttpMessage message = new HttpMessage();
		message.setMsg(errorMsg);
		baseResponse.setExt(message);
		return baseResponse;
	}

	/**
	 * 返回失败
	 * @return
	 */
	public static HttpBaseResponse returnFail(Long code, String errorMsg) {
		HttpBaseResponse baseResponse = new HttpBaseResponse();
		baseResponse.setCode(code);
		HttpMessage message = new HttpMessage();
		message.setMsg(errorMsg);
		baseResponse.setExt(message);
		return baseResponse;
	}
	
	/**
	 * 返回失败
	 * @return
	 */
	public static HttpBaseResponse returnFail(HttpResponseConstants httpResponseConstant) {
		HttpBaseResponse baseResponse = new HttpBaseResponse();
		baseResponse.setCode(httpResponseConstant.getCode());
		HttpMessage message = new HttpMessage();
		message.setMsg(httpResponseConstant.getMsg());
		baseResponse.setExt(message);
		return baseResponse;
	}
	
	
	/**
	 * 返回系统繁忙
	 * @return
	 */
	public static HttpBaseResponse returnSystemFail() {
		HttpBaseResponse baseResponse = new HttpBaseResponse();
		baseResponse.setCode(HttpResponseConstants.SYSTEM_FAILED.getCode());
		baseResponse.setObj(HttpResponseConstants.SYSTEM_FAILED.getMsg());
		HttpMessage message = new HttpMessage();
		message.setMsg(HttpResponseConstants.SYSTEM_FAILED.getMsg());
		baseResponse.setExt(message);
		return baseResponse;
	}
	
}
