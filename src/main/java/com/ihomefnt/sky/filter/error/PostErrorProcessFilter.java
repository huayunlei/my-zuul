package com.ihomefnt.sky.filter.error;

import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import com.ihomefnt.sky.common.constant.SecretKeyConstants;
import com.ihomefnt.sky.common.util.Base64Utils;
import com.ihomefnt.sky.common.util.JsonUtils;
import com.ihomefnt.sky.common.util.RSAUtils;
import com.ihomefnt.sky.common.util.ResponseCommonUtil;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;

/**
 * @author huayunlei
 * @created 2018年11月9日 上午11:40:29
 * @desc 错误处理filter--只处理post filter
 */
@Component
public class PostErrorProcessFilter extends ZuulFilter {
	private static Logger LOGGER = LoggerFactory.getLogger(PostErrorProcessFilter.class);

	@Override
	public boolean shouldFilter() {
		// post过滤器需要对response result 特殊处理：加密
		RequestContext ctx = RequestContext.getCurrentContext();
		
		boolean isPost = false;
		ZuulFilter failedFilter =(ZuulFilter)ctx.get("failed.filter");
        if(failedFilter != null && failedFilter.filterType().equals("post")){
        	isPost = true;
			LOGGER.info(" ErrorProcessFilter filterType .... {}", failedFilter.filterType());
        }
        Map<String, String> headerMap = ctx.getZuulRequestHeaders();
		String isencrypt = headerMap.get("isencrypt");
		return "true".equals(isencrypt) && isPost;
	}

	@Override
	public String filterType() {
		return "error";
	}

	@Override
	public int filterOrder() {
		return 0;
	}

	@Override
	public Object run() {
		try {
			RequestContext ctx = RequestContext.getCurrentContext();
			Throwable throwable = ctx.getThrowable();
			if (null != throwable) {
				LOGGER.error(" before ErrorProcessFilter Exception ....{}", throwable.getCause());
			}
			
            String result = JsonUtils.obj2json(ResponseCommonUtil.returnSystemFail());
			LOGGER.info(" ErrorProcessFilter result .... {}", result);
			// 使用RSA算法对下游返回的response进行加密（私钥privateKey）
			String responseResult = Base64Utils.encode(
					RSAUtils.encryptByPrivateKey(result.getBytes(), SecretKeyConstants.APP_PRIVATE_KEY));

			HttpServletResponse response = ctx.getResponse();
            PrintWriter writer = response.getWriter();
    		writer.print(responseResult);
    		writer.close();
            response.flushBuffer();
		} catch (Exception ex) {
			LOGGER.error(" ErrorProcessFilter Exception .... {}", ex);
			ReflectionUtils.rethrowRuntimeException(ex);
		}

		return null;
	}
	
}
