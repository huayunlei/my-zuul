package com.ihomefnt.sky.common.constant;

public enum HttpResponseConstants {
	
	
	SUCCESS(103001L, "成功"),
	SYSTEM_FAILED(103002L, "系统繁忙，请稍后再试"),//系统异常
	PARAMS_NOT_EXISTS(103003L, "传入参数有空值,或传入参数有误"),// 缺少必填参数
	SIGN_INVALID(103004L, "签名错误"),
	REQUEST_OVERTIME(103005L, "请求过期"),
	SERVER_ROUTE_UNAVAILABLE(103006L, "服务路由不可用"),
	ORDER_AUTH_FAIL(103007L, "订单鉴权失败"),
	SERVER_ROUTE_UNAVAILABLE_ORDER(103008L, "系统繁忙，请稍后再试"),
	SERVER_ROUTE_UNAVAILABLE_USER(103009L, "系统繁忙，请稍后再试"),

	USER_NOT_LOGIN(103010L,"你的账号已在其他地方登录，请重新登录"),

	ERROR_REQUESR_METHOD(103011L,"错误的请求类型"),

	;
	
	private Long code;
	private String msg;
	
	private HttpResponseConstants(Long code, String msg) {
		this.code = code;
		this.msg = msg;
	}
	public Long getCode() {
		return code;
	}
	public void setCode(Long code) {
		this.code = code;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	

}
