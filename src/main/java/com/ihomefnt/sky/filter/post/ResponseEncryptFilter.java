package com.ihomefnt.sky.filter.post;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import com.ihomefnt.sky.common.constant.HttpResponseConstants;
import com.ihomefnt.sky.common.constant.SecretKeyConstants;
import com.ihomefnt.sky.common.exception.BusinessException;
import com.ihomefnt.sky.common.util.Base64Utils;
import com.ihomefnt.sky.common.util.JsonUtils;
import com.ihomefnt.sky.common.util.RSAUtils;
import com.ihomefnt.sky.common.util.ResponseCommonUtil;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;

/**
 * @author huayunlei
 * @created 2018年11月8日 下午3:28:17
 * @desc 返回参数统一加密处理
 */
@Component
public class ResponseEncryptFilter extends ZuulFilter {
	
    private static Logger LOGGER = LoggerFactory.getLogger(ResponseEncryptFilter.class);
    
	@Override
	public boolean shouldFilter() {
		RequestContext ctx = RequestContext.getCurrentContext();
		Map<String, String> headerMap = ctx.getZuulRequestHeaders();
		String isencrypt = headerMap.get("isencrypt");
        return "true".equals(isencrypt);
	}
	
	@Override
	public String filterType() {
		return "post";
	}

	@Override
	public int filterOrder() {
		return 10;
	}
	
	@Override
	public Object run() {
		try {
			// 对返回的response统一加密处理
			RequestContext ctx = RequestContext.getCurrentContext();
			String body = getResponseBody(ctx);
			LOGGER.info(" ResponseEncryptFilter Encrypt before response， {} ", body);
			// 使用RSA算法对下游返回的response进行加密（私钥privateKey）
			String responseResult = Base64Utils.encode(
					RSAUtils.encryptByPrivateKey(body.getBytes(), SecretKeyConstants.APP_PRIVATE_KEY));
			LOGGER.info(" ResponseEncryptFilter Encrypt after response， {} ", responseResult);
			// 重新写入密文
			ctx.setResponseBody(responseResult);
			
		} catch (Exception e) {
			LOGGER.error(" ResponseEncryptFilter Encrypt response error ", e);
			throw new BusinessException(HttpResponseConstants.SYSTEM_FAILED.getCode(), "加解密异常", e);
		}
		
		return null;
	}

	private String getResponseBody(RequestContext ctx) throws IOException {
		String body = null;
		if (StringUtils.isNotEmpty(ctx.getResponseBody())) {
			body=ctx.getResponseBody();
		} else if (null != ctx.getResponseDataStream()) {
			body = StreamUtils.copyToString(ctx.getResponseDataStream(), Charset.forName("UTF-8"));
			
//			body = HttpHelper.getBodyStringByStream(ctx.getResponseDataStream());
		} else {
			body=JsonUtils.obj2json(ResponseCommonUtil.returnSystemFail());
		}
		
		if (StringUtils.isEmpty(body)) {
			body=JsonUtils.obj2json(ResponseCommonUtil.returnSystemFail());
		}
		return body;
	}

}
