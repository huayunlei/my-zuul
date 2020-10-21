package com.ihomefnt.sky.filter.error;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import com.ihomefnt.sky.common.util.JsonUtils;
import com.ihomefnt.sky.common.util.ResponseCommonUtil;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;

/**
 * @author huayunlei
 * @created 2018年11月9日 上午11:40:29
 * @desc 错误处理filter--只处理pre和route的filter，不处理post filter 
 */
@Component
public class ErrorProcessFilter extends ZuulFilter {
	private static Logger LOGGER = LoggerFactory.getLogger(ErrorProcessFilter.class);

	@Override
	public boolean shouldFilter() {
		RequestContext ctx = RequestContext.getCurrentContext();
		boolean isPost = false;
		ZuulFilter failedFilter =(ZuulFilter)ctx.get("failed.filter");
        if(failedFilter != null && failedFilter.filterType().equals("post")){
        	isPost = true;
			LOGGER.info(" ErrorProcessFilter filterType .... {}", failedFilter.filterType());
        }
        //只处理pre和route的filter，不处理post filter 
		return !isPost;
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
			
            HttpServletResponse response = ctx.getResponse();
            response.setContentType("application/json;charset=utf-8");
            response.setCharacterEncoding("UTF-8");
            
            String result = JsonUtils.obj2json(ResponseCommonUtil.returnSystemFail());
			LOGGER.info(" ErrorProcessFilter result .... {}", result);
			ctx.setResponseBody(result);
			
		} catch (Exception ex) {
			LOGGER.error(" ErrorProcessFilter Exception .... {}", ex);
			ReflectionUtils.rethrowRuntimeException(ex);
		}

		return null;
	}
	
}
