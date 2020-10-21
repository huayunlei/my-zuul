package com.ihomefnt.sky.config;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import org.springframework.stereotype.Component;

@Component
public class ApplicationParamsConfig {
	
	/**
	 * 网关接口请求的有效时间（单位：秒）
	 */
	@NacosValue(value = "${server.api.timeout}", autoRefreshed = true)
	private long serverApiTimeout;

	/**
	 * 是否订单鉴权
	 */
	@NacosValue(value = "${is.order.auth}", autoRefreshed = true)
	private boolean isOrderAuth;

	/**
	 * ## sky订单鉴权监控配置
	 */
	@NacosValue(value = "${sky.monitor.order.auth.conf}", autoRefreshed = true)
	private String monitorOrderAuthConf;

	public String getMonitorOrderAuthConf() {
		return monitorOrderAuthConf;
	}

	public void setMonitorOrderAuthConf(String monitorOrderAuthConf) {
		this.monitorOrderAuthConf = monitorOrderAuthConf;
	}

	public long getServerApiTimeout() {
		return serverApiTimeout*1000;
	}

	public void setServerApiTimeout(long serverApiTimeout) {
		this.serverApiTimeout = serverApiTimeout;
	}

	public boolean isOrderAuth() {
		return isOrderAuth;
	}

	public void setOrderAuth(boolean orderAuth) {
		isOrderAuth = orderAuth;
	}
}
