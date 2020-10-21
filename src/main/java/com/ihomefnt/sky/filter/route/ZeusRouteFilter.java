package com.ihomefnt.sky.filter.route;

import com.alibaba.ttl.threadpool.TtlExecutors;
import com.ihomefnt.redis.GracefulRedisTemplate;
import com.ihomefnt.sky.common.constant.HttpResponseConstants;
import com.ihomefnt.sky.common.exception.BusinessException;
import com.ihomefnt.sky.common.util.JsonUtils;
import com.ihomefnt.sky.common.util.ResponseCommonUtil;
import com.ihomefnt.sky.common.util.URLEncodeAndDecoder;
import com.ihomefnt.sky.config.ApplicationParamsConfig;
import com.ihomefnt.sky.domain.dto.AiMonitorDto;
import com.ihomefnt.sky.domain.dto.OrderAuthResultDto;
import com.ihomefnt.sky.domain.dto.UserDto;
import com.ihomefnt.sky.proxy.O2oApiProxy;
import com.ihomefnt.sky.proxy.OrderProxy;
import com.ihomefnt.sky.proxy.UserProxy;
import com.ihomefnt.sky.service.AiDingTalkService;
import com.ihomefnt.zeus.finder.ServiceCaller;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * @Description: zeus路由转发
 * @author: huayunlei
 * @date: 2018年11月14日 下午3:33:15
 */

@Component
public class ZeusRouteFilter extends ZuulFilter {

    private static Logger LOGGER = LoggerFactory.getLogger(ZeusRouteFilter.class);

    @Resource
    private GracefulRedisTemplate gracefulRedisTemplate;
    @Resource
    private ServiceCaller serviceCaller;
    @Autowired
    private UserProxy userProxy;
    @Autowired
    private OrderProxy orderProxy;
    @Autowired
    private ApplicationParamsConfig config;
    @Autowired
    private AiDingTalkService aiDingTalkService;
    @Autowired
    private O2oApiProxy o2oApiProxy;

    private static final ExecutorService executorService = TtlExecutors.getTtlExecutorService(Executors.newCachedThreadPool());

    @Override
    public boolean shouldFilter() {
        RequestContext ctx = RequestContext.getCurrentContext();
        return !ctx.containsKey("error.status_code")
                && ctx.getRouteHost() == null
                && ctx.get("serviceId") != null
                && ctx.sendZuulResponse();
    }

    @Override
    public String filterType() {
        return "route";
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    private static String goProductListRrl = "goProductList";

    @Override
    public Object run() {
        Map<String, Object> body = null;
        String requestURI = null;
        RequestContext ctx = RequestContext.getCurrentContext();
        try {
            HttpServletRequest request = ctx.getRequest();
            String requestUri = request.getRequestURI()
                    // 去除context path
                    .replaceFirst("/sky","").replaceFirst("sky","");
            if (requestUri.endsWith("o2o-api") || requestUri.endsWith("o2o-api/")) {
                ctx.setResponseBody("welcome to sky!");
                return null;
            }
            String result = null;
            if (requestUri.contains(goProductListRrl)) {
                result = zeusCallerForoProductList(request);
            } else {
                // get requestURI
                requestURI = getRequestURIFor(request);

                // header
                Map<String, String> extraHeaders = getRequestHeaders(request);

                // body
                String bodyStr = getRequestBody(request);
                LOGGER.info("request url：{}，method:{}, params：{}.", requestURI, request.getMethod(), bodyStr);
                if (RequestMethod.OPTIONS.name().equals(request.getMethod())) {
                    ctx.setResponseBody(JsonUtils.obj2json(ResponseCommonUtil.returnFail(HttpResponseConstants.ERROR_REQUESR_METHOD)));
                    return null;
                }

                body = bodyStr != null ? JsonUtils.json2map(bodyStr) : new HashMap<>();
                String accessToken = null != body.get("accessToken") ? (String) body.get("accessToken") : null;
                try {
					UserDto userDto = null;
					if (StringUtils.isNotBlank(accessToken)) {
						userDto = userProxy.findUserByToken(accessToken);
					}

                    if (config.isOrderAuth()) {// 鉴权总开关
                        String orderId = body.get("orderId") != null ? body.get("orderId").toString() : null;
                        if (StringUtils.isEmpty(orderId)) {
                            orderId = body.get("orderNum") != null ? body.get("orderNum").toString() : null;
                        }
                        if (StringUtils.isNotEmpty(orderId)) {
                            OrderAuthResultDto orderAuthResultDto = o2oApiProxy.orderAuth(orderId, userDto != null ? userDto.getId() : null, requestURI);
                            if(null != orderAuthResultDto && !orderAuthResultDto.isResult()) {
                                // 订单鉴权不通过，直接返回失败，并钉钉告警
                                asyncSendDingTalkWarn(requestURI, JsonUtils.obj2json(body));
                                ctx.setResponseBody(JsonUtils.obj2json(ResponseCommonUtil.returnFail(HttpResponseConstants.ORDER_AUTH_FAIL)));
                                return null;
                            }
                        }
                    }

                    body.put("userInfo", userDto);
				} catch (BusinessException e) {
					ctx.setResponseBody(JsonUtils.obj2json(ResponseCommonUtil.returnFail(e.getCode(), e.getMessage())));
					return null;
                }

                if (RequestMethod.GET.name().equals(request.getMethod())) {// GET请求
                    result = zeusServiceCaller(requestURI, extraHeaders, body, RequestMethod.GET.name());
                } else {// post 请求
                    result = zeusServiceCaller(requestURI, extraHeaders, body, RequestMethod.POST.name());
                }
            }

            ctx.setResponseBody(result);
            ctx.setSendZuulResponse(false);
            ctx.setResponseStatusCode(HttpStatus.OK.value());
        } catch (Exception e) {
        	LOGGER.error("remote has error,the serviceName is {} , the param is {}.", requestURI, JsonUtils.obj2json(body), e);
            ctx.set("error.status_code", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            ctx.setResponseBody(JsonUtils.obj2json(ResponseCommonUtil.returnSystemFail()));
        }
        return null;
    }

    private void asyncSendDingTalkWarn(String requestURI, String body) {
        try {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    String monitorOrderAuthConf = config.getMonitorOrderAuthConf();
                    if (StringUtils.isBlank(monitorOrderAuthConf)) {
						LOGGER.error("ZeusRouteFilter sendDingTalkWarn monitorOrderAuthConf is null ,the serviceName is {} , the param is {}.", requestURI, JsonUtils.obj2json(body));
                        return;
                    }

                    AiMonitorDto aiMonitorDto = JsonUtils.json2obj(monitorOrderAuthConf, AiMonitorDto.class);
                    aiDingTalkService.sendDingTalkWarn(aiMonitorDto, requestURI, body
                            , HttpResponseConstants.ORDER_AUTH_FAIL.getCode(), HttpResponseConstants.ORDER_AUTH_FAIL.getMsg());
                }
            });
        } catch (Exception e) {
			LOGGER.error("ZeusRouteFilter sendDingTalkWarn Exception ,the serviceName is {} , the param is {}.", requestURI, JsonUtils.obj2json(body), e);
        }
    }

    private String zeusCallerForoProductList(HttpServletRequest request) throws IOException {
        String requestURI = request.getRequestURI();
        requestURI = StringUtils.trim(URLEncodeAndDecoder.encodeAndDecoder(requestURI));
        int endIndex = requestURI.indexOf(goProductListRrl) + goProductListRrl.length();
        String url = requestURI.substring(0, endIndex).replaceFirst("/sky","").replaceFirst("sky","");
        url = url.substring(1).replace("/", ".");

        // header
        Map<String, String> extraHeaders = getRequestHeaders(request);

        Map<String, Object> body = new HashMap<>();
        if (RequestMethod.GET.name().equals(request.getMethod())) {
            Enumeration<String> parameterNames = request.getParameterNames();
            if (null != parameterNames) {
                while (parameterNames.hasMoreElements()) {
                    String name = parameterNames.nextElement();
                    body.put(name, request.getParameter(name));
                }
            }
        } else {
            String bodyStr = StreamUtils.copyToString(request.getInputStream(), Charset.forName("UTF-8"));
            if (null == bodyStr) {
                return null;
            } else {
                bodyStr = URLEncodeAndDecoder.encodeAndDecoder(bodyStr);
                if (bodyStr.endsWith("=")) {
                    bodyStr = bodyStr.substring(0, bodyStr.length() - 1);
                }
                if (bodyStr.startsWith("req=")) {
                    bodyStr = bodyStr.substring(4);
                }
            }
            body = JsonUtils.json2map(bodyStr);
        }

        if (requestURI.length() > endIndex + 1) {
            String pathVariableParams = requestURI.substring(endIndex + 1, requestURI.length());
            String[] pathVariableParamsArray = pathVariableParams.split("/");
            for (int i = 0; i < pathVariableParamsArray.length; i++) {
                if (i == 0) {
                    body.put("nodeId", Long.parseLong(pathVariableParamsArray[i]));
                }
                if (i == 1) {
                    body.put("pageNo", Long.parseLong(pathVariableParamsArray[i]));
                }
            }
            if (pathVariableParamsArray.length == 2) {
                url = url + "PageNew";
            } else {
                url = url + "New";
            }
        }

        return zeusServiceCaller(url, extraHeaders, body, RequestMethod.POST.name());
    }

    private String zeusServiceCaller(String requestURI, Map<String, String> extraHeaders, Object body, String requestMethod) {
        String result = null;
		LOGGER.info("ZeusRouteFilter serviceCaller begin serviceName is {} , the param is {}", requestURI, JsonUtils.obj2json(body));
        if (RequestMethod.GET.name().equals(requestMethod)) {// GET请求
            result = serviceCaller.get(requestURI, extraHeaders, body, String.class);
        } else {
            result = serviceCaller.post(requestURI, extraHeaders, body, String.class);

        }
        String logResult = result;
        if (null != logResult && logResult.length() > 500) {
            logResult = logResult.substring(0, 500);
        }
		LOGGER.info("ZeusRouteFilter serviceCaller end serviceName is {} , the response is {} "
				, requestURI, null != logResult ? logResult : " response is null ");
        return result;
    }


    private String getRequestURIFor(HttpServletRequest request) throws IOException {
        String requestURI = request.getRequestURI();
        requestURI = StringUtils.trim(URLEncodeAndDecoder.encodeAndDecoder(requestURI));
        if (requestURI.endsWith("%20")) {
            requestURI = requestURI.substring(0, requestURI.indexOf("%20"))
                    // 去除context path
                    .replace("/sky","").replace("sky","");
        }
        requestURI = requestURI
                // 去除context path
                .replaceFirst("/sky","").replaceFirst("sky","")
                .substring(1).replace("/", ".");
        return requestURI;
    }

    private String getRequestBody(HttpServletRequest request) throws IOException {
        if (RequestMethod.GET.name().equals(request.getMethod())) {
            Enumeration<String> parameterNames = request.getParameterNames();
            if (null != parameterNames) {
                Map<String, Object> body = new HashMap<>();
                while (parameterNames.hasMoreElements()) {
                    String name = parameterNames.nextElement();
                    body.put(name, request.getParameter(name));
                }
                return JsonUtils.obj2json(body);
            }
        } else {
            String body = StreamUtils.copyToString(request.getInputStream(), Charset.forName("UTF-8"));
            if (null == body) {
                return null;
            } else {
//				body = URLEncodeAndDecoder.encodeAndDecoder(body);
                if (body.endsWith("=")) {
                    body = body.substring(0, body.length() - 1);
                }
                if (body.startsWith("req=")) {
                    body = body.substring(4);
                }

                if (body.startsWith("%")) {
                    body = URLEncodeAndDecoder.decoder(body);
                }
            }
            return body;
        }
        return null;

    }

    private Map<String, String> getRequestHeaders(HttpServletRequest request) {
        Map<String, String> extraHeaders = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = (String) headerNames.nextElement();
            if(!"host".equals(headerName)){
                extraHeaders.put(headerName, request.getHeader(headerName));
            }
        }
        return extraHeaders;
    }


}
