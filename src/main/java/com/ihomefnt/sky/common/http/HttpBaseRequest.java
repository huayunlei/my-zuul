package com.ihomefnt.sky.common.http;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Created by shirely_geng on 15-1-10.
 */
@ApiModel("公共请求参数")
public class HttpBaseRequest {

	@ApiModelProperty("设备token")
	private String deviceToken;

	@ApiModelProperty("设备类型")
	private Integer osType; // 订单来源,1:iPhone客户端，2:Android客户端，3:H5网站，4:PC网站，5:客服电话下单，6:客服现场下单 8:小程序订单

	@ApiModelProperty("app版本号")
	private String appVersion;

	@ApiModelProperty("iOS 自然默认 100 ,每个应用都不一样")
	private String parterValue;// iOS 自然默认 100
								// ,每个应用都不一样。具体请参看http://wiki.ihomefnt.com:8002/pages/viewpage.action?pageId=3834260

	@ApiModelProperty("设备宽度")
	private Integer width;

	@ApiModelProperty("定位城市id")
	private String cityCode;

	@ApiModelProperty("登录标识")
	private String accessToken;

	@ApiModelProperty("定位地区")
	private String location;

	@ApiModelProperty("手机号码")
	private String mobileNum; // 手机号码

	@ApiModelProperty("设备类型")
	private String deviceType;

	@ApiModelProperty("系统版本")
	private String systemVersion;

	@ApiModelProperty("bundle版本号")
	private String bundleVersion;

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	private String sessionId;

	public String getDeviceToken() {
		return deviceToken;
	}

	public void setDeviceToken(String deviceToken) {
		this.deviceToken = deviceToken;
	}

	public Integer getOsType() {
		return osType;
	}

	public void setOsType(Integer osType) {
		this.osType = osType;
	}

	public String getAppVersion() {
		return appVersion;
	}

	public void setAppVersion(String appVersion) {
		this.appVersion = appVersion;
	}

	public String getParterValue() {
		return parterValue;
	}

	public void setParterValue(String parterValue) {
		this.parterValue = parterValue;
	}

	public Integer getWidth() {
		return width;
	}

	public void setWidth(Integer width) {
		this.width = width;
	}

	public String getCityCode() {
		return cityCode;
	}

	public void setCityCode(String cityCode) {
		this.cityCode = cityCode;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getMobileNum() {
		return mobileNum;
	}

	public void setMobileNum(String mobileNum) {
		this.mobileNum = mobileNum;
	}

	public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}

	public String getSystemVersion() {
		return systemVersion;
	}

	public void setSystemVersion(String systemVersion) {
		this.systemVersion = systemVersion;
	}

	public String getBundleVersion() {
		return bundleVersion;
	}

	public void setBundleVersion(String bundleVersion) {
		this.bundleVersion = bundleVersion;
	}
}
