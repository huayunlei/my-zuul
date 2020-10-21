package com.ihomefnt.sky.common.fallback;

import com.ihomefnt.sky.common.constant.HttpResponseConstants;
import com.ihomefnt.sky.common.util.JsonUtils;
import com.ihomefnt.sky.common.util.ResponseCommonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.zuul.filters.route.FallbackProvider;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

//import org.springframework.cloud.netflix.zuul.filters.route.ZuulFallbackProvider;

/**
 * @author huayunlei
 * @created 2018年11月7日 上午11:07:37
 * @desc 当Zuul中给定路由的电路跳闸时，您可以通过创建ZuulFallbackProvider类型的bean来提供回退响应。
 *  在这个bean中，您需要指定回退所对应的路由ID，并提供一个ClientHttpResponse作为后备返回。 这是一个非常简单的ZuulFallbackProvider实现。
 */
@Component
public class O2oApiFallbackProvider implements FallbackProvider {
	
	private static Logger LOGGER = LoggerFactory.getLogger(O2oApiFallbackProvider.class);

	//getRoute返回的必须要和zuul.routes.***一致，才能针对某个服务降级
	@Override
	public String getRoute() {
		return "o2o-api";
	}

	@Override
	public ClientHttpResponse fallbackResponse(String route, Throwable cause) {
		LOGGER.info(" O2oApiFallbackProvider fallbackResponse route:{}, exception:{}", route, cause);

		return new ClientHttpResponse() {

			@Override
			public HttpHeaders getHeaders() {
				HttpHeaders headers = new HttpHeaders();
				headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
				headers.add("Access-Control-Allow-Origin", "*");
				headers.add("Access-Control-Allow-Credentials", "true");
				headers.add("Access-Control-Allow-Methods", "GET, POST, HEAD, PUT, DELETE");
				headers.add("Access-Control-Allow-Headers",
						"Accept, Origin, X-Requested-With, Content-Type, Last-Modified");

				return headers;
			}

			@Override
			public InputStream getBody() throws IOException {
				String result = JsonUtils.obj2json(ResponseCommonUtil.returnFail(HttpResponseConstants.SERVER_ROUTE_UNAVAILABLE));
				LOGGER.info(" O2oApiFallbackProvider fallbackResponse ... response is {}", result);
				return new ByteArrayInputStream(result.getBytes("UTF-8"));
			}

			@Override
			public String getStatusText() throws IOException {
				return HttpStatus.OK.getReasonPhrase();
			}

			/**
			 * 网关向api服务请求是失败了，但是消费者客户端向网关发起的请求是OK的，
			 * 不应该把api的404,500等问题抛给客户端
			 * 网关和api服务集群对于客户端来说是黑盒子
			 */
			@Override
			public HttpStatus getStatusCode() throws IOException {
				return HttpStatus.OK;
			}

			@Override
			public int getRawStatusCode() throws IOException {
				return HttpStatus.OK.value();
			}

			@Override
			public void close() {

			}
		};
	}



}
