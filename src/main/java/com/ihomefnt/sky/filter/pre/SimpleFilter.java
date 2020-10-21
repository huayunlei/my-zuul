package com.ihomefnt.sky.filter.pre;

import com.ihomefnt.common.http.HttpBaseRequest;
import com.ihomefnt.common.util.StringUtil;
import com.ihomefnt.sky.common.constant.HttpResponseConstants;
import com.ihomefnt.sky.common.constant.SecretKeyConstants;
import com.ihomefnt.sky.common.util.*;
import com.ihomefnt.sky.config.EncryptInterfaceConfig;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author jerfan cang
 * @date 2018/8/13 11:39
 */
@Component
public class SimpleFilter extends ZuulFilter{
    private static Logger LOGGER = LoggerFactory.getLogger(SimpleFilter.class);

    @Autowired
    private EncryptInterfaceConfig encryptInterfaceConfig;
    
    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
    	RequestContext ctx = RequestContext.getCurrentContext();
		
		try {
			HttpServletRequest request = ctx.getRequest();
			HttpServletResponse response = ctx.getResponse();
			ZuulResponseHeaderCommonUtil.setCommonResponseHeaders(response, request);
			
			String requestURI = request.getRequestURI()
					// 去除context path
					.replaceFirst("/sky","").replaceFirst("sky","");
			LOGGER.info("SimpleFilter requestURI is {} ", requestURI);
			String headerParams = request.getHeader("headerParams");
			if (StringUtil.isNotBlank(headerParams)) {
				LOGGER.info("SimpleFilter headerParams is {} ", headerParams);
				// 使用RSA算法（私钥privateKey）对请求头中的headerParams进行解密
				String headerParamsDecrypt = new String(RSAUtils.decryptByPrivateKey(Base64Utils.decode(headerParams), SecretKeyConstants.APP_PRIVATE_KEY));
				LOGGER.info("SimpleFilter headerParams after decrypt is {} ", headerParamsDecrypt);
				HttpBaseRequest requestHeader = JsonUtils.json2obj(headerParamsDecrypt, HttpBaseRequest.class);
				if (StringUtil.isNotBlank(requestHeader.getAppVersion())) {
					// 是否加密set header中
					boolean isencrypt = encryptInterfaceConfig.isShouldFilter(requestURI, requestHeader.getAppVersion());
					ctx.addZuulRequestHeader("isencrypt", String.valueOf(isencrypt));
					return null;
				}
			}
		} catch (Exception e) {
			LOGGER.error("SimpleFilter Exception , {} ", e);
			ctx.set("error.status_code", HttpResponseConstants.SYSTEM_FAILED.getCode());
			ctx.setResponseBody(JsonUtils.obj2json(ResponseCommonUtil.returnSystemFail()));
		}
		
        return null;
    }
    
    public static void main(String[] args) throws Exception {
    	String body = "{\"deviceToken\":\"BD46ADFE-92AB-49DA-A936-BAC0587E3889\",\"osType\":1,\"appVersion\":\"5.2.6\",\"parterValue\":\"100\",\"width\":1242,\"cityCode\":null,\"accessToken\":\"D2C087A130107CF049529F8146E85470\",\"location\":\"商丘\",\"mobileNum\":\"18903702085\",\"deviceType\":\"iPhone 6 Plus\",\"systemVersion\":\"12.1.4\",\"bundleVersion\":\"5.2.6.7.4.5\",\"sessionId\":null}";
    	String responseResult = Base64Utils.encode(
				RSAUtils.encryptByPublicKey(body.getBytes(), SecretKeyConstants.APP_PUBLIC_KEY));
    	System.out.println(responseResult);
	}
}
