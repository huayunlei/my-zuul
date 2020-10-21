package com.ihomefnt.sky.common.cache;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

@Component
public class HttpUriCacheManage implements InitializingBean {
	
	private static Map<String, String> httpUriCache = new HashMap<String, String>();

	@Override
	public void afterPropertiesSet() throws Exception {
		// 修改密码
		httpUriCache.put("/o2o-api/account/setsmspass", "/account/setsmspass");
//		
//		// 银行卡
//		httpUriCache.put("/o2o-api/bankCard/checkCard", "/bankCard/checkCard");
//		httpUriCache.put("/o2o-api/bankCard/checkPhoneCode", "/bankCard/checkPhoneCode");
//		httpUriCache.put("/o2o-api/bankCard/checkUserSendSmsCode", "/bankCard/checkUserSendSmsCode");
//		httpUriCache.put("/o2o-api/bankCard/getBankCardDetail", "/bankCard/getBankCardDetail");
		
		
	}
	
	public static boolean isContains(String httpUri) {
		return httpUriCache.containsKey(httpUri);
	}
	

}
