package com.ihomefnt.sky.filter.pre;

import com.ihomefnt.sky.common.constant.HttpResponseConstants;
import com.ihomefnt.sky.common.constant.SecretKeyConstants;
import com.ihomefnt.sky.common.util.*;
import com.ihomefnt.sky.config.ApplicationParamsConfig;
import com.ihomefnt.sky.filter.BodyReaderHttpServletRequestWrapper;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.Map;

/**
 * @author huayunlei
 * @created 2018年11月5日 上午11:48:22
 * @desc 请求参数安全认证filter
 */
@Component
public class RequestParamAuthFilter extends ZuulFilter {
	
    private static Logger LOGGER = LoggerFactory.getLogger(RequestParamAuthFilter.class);
    
    @Autowired
    private ApplicationParamsConfig config;
    
	@Override
	public boolean shouldFilter() {
		RequestContext ctx = RequestContext.getCurrentContext();
		Map<String, String> headerMap = ctx.getZuulRequestHeaders();
		String isencrypt = headerMap.get("isencrypt");
        return !ctx.containsKey("error.status_code") && "true".equals(isencrypt);
	}
	

	@Override
	public String filterType() {
		return "pre";
	}

	@Override
	public int filterOrder() {
		return 2;
	}
	
	@Override
	public Object run() {
		RequestContext ctx = RequestContext.getCurrentContext();
		try {
	        HttpServletRequest request = ctx.getRequest();
	        
	        // 校验密钥对是否过期，预留，本期暂为不失效
	        String sign = request.getHeader("sign");
	        LOGGER.info("RequestParamAuthFilter request sign {} ", sign);
	        if (StringUtils.isEmpty(sign)) {
				LOGGER.error("RequestParamAuthFilter request sign is null ");
            	ctx.set("error.status_code", HttpServletResponse.SC_UNAUTHORIZED);
    			ctx.setResponseBody(JsonUtils.obj2json(ResponseCommonUtil.returnFail(HttpResponseConstants.PARAMS_NOT_EXISTS)));
				return null;
	        }
	        // 解密前的参数
	        String body = StreamUtils.copyToString(request.getInputStream(), Charset.forName("UTF-8"));
	        LOGGER.info("RequestParamAuthFilter decode before...body {} ", body);

	        body = body.replace("%2F", "/").replace("%3D", "=").replace("%2B", "+");

	        LOGGER.info("RequestParamAuthFilter decode after...body {} ", body);
	        if (StringUtils.isNotEmpty(body)) {
    			// 对请求的密文param使用MD5算法进行加密（字母大写）
    			String signNew = MD5Helper.encode(body);
    			// 使用RSA算法（私钥privateKey）对请求头中的sign进行解密
    			String signDecrypt = new String(RSAUtils.decryptByPrivateKey(Base64Utils.decode(sign), SecretKeyConstants.APP_PRIVATE_KEY));
    			// 比较sign是否相等
    			if (!signNew.equals(signDecrypt)) {
    				LOGGER.error("RequestParamAuthFilter sign vefiy is not pass , {} ", body);
    				ctx.set("error.status_code", HttpServletResponse.SC_UNAUTHORIZED);
        			ctx.setResponseBody(JsonUtils.obj2json(ResponseCommonUtil.returnFail(HttpResponseConstants.SIGN_INVALID)));
    				return null;
    			}
    			
    			// 使用RSA算法（私钥privateKey）对密文param进行解密
    			String decryptValue = new String(RSAUtils.decryptByPrivateKey(Base64Utils.decode(body), SecretKeyConstants.APP_PRIVATE_KEY));
    			LOGGER.info("RequestParamAuthFilter 解密后： {} ", decryptValue);
    			
    			Map<String, Object> decryptParams = JsonUtils.json2map(decryptValue);
	        	if (decryptParams.containsKey("req")) {
	        		String req = (String) decryptParams.get("req");
	    			LOGGER.info("RequestParamAuthFilter 解密后的req： {} ", req);
	        		decryptParams = JsonUtils.json2map(req);
	        		
	        		decryptValue = JsonUtils.obj2json(decryptParams);
	    			LOGGER.info("RequestParamAuthFilter decryptValue： {} ", decryptValue);

	        	}
    			
    			// check timestamp
    			Object timestampObj = decryptParams.get("timestamp");
    			if (null == timestampObj) {
    				LOGGER.error("RequestParamAuthFilter timestamp is null , {} ", decryptParams);
    				ctx.set("error.status_code", HttpServletResponse.SC_UNAUTHORIZED);
        			ctx.setResponseBody(JsonUtils.obj2json(ResponseCommonUtil.returnFail(HttpResponseConstants.PARAMS_NOT_EXISTS)));
    				return null;
    			}
    			Long timestamp = new BigInteger(timestampObj.toString()).longValue();
    			Long now = System.currentTimeMillis();
    			if (now > Long.sum(timestamp, config.getServerApiTimeout())) {
    				LOGGER.error("RequestParamAuthFilter request is over time , {} ", decryptParams);
    				ctx.set("error.status_code", HttpServletResponse.SC_UNAUTHORIZED);
    				ctx.setResponseBody(JsonUtils.obj2json(ResponseCommonUtil.returnFail(HttpResponseConstants.REQUEST_OVERTIME)));
    				return null;
    			}
    			
    			// 将明文参数组装为下游接口需要的格式，并请求下游
    			ctx.setRequest(new BodyReaderHttpServletRequestWrapper(request, decryptValue.getBytes("UTF-8")));
    			
	        }
		} catch (Exception e) {
			LOGGER.error("RequestParamAuthFilter Exception , {} ", e);
			ctx.set("error.status_code", HttpResponseConstants.SYSTEM_FAILED.getCode());
			ctx.setResponseBody(JsonUtils.obj2json(ResponseCommonUtil.returnSystemFail()));
		}
		return null;
	}

}
