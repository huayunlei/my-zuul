package com.ihomefnt.sky.proxy;

import com.ihomefnt.sky.common.constant.HttpResponseConstants;
import com.ihomefnt.sky.common.constant.O2oApiConstants;
import com.ihomefnt.sky.common.exception.BusinessException;
import com.ihomefnt.sky.common.http.HttpBaseResponse;
import com.ihomefnt.sky.domain.dto.OrderAuthResultDto;
import com.ihomefnt.zeus.finder.ServiceCaller;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Service
public class O2oApiProxy {
    private static final Logger LOG = LoggerFactory.getLogger(O2oApiProxy.class);

    @Resource
    private ServiceCaller serviceCaller;

    public OrderAuthResultDto orderAuth(String orderId, Integer userId, String requestURI) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("orderId", orderId);
        params.put("userId", userId);
        params.put("requestURI", requestURI);

        HttpBaseResponse<OrderAuthResultDto> response = null;
        try {
            response = serviceCaller.post(O2oApiConstants.ORDER_AUTH, params, new TypeReference<HttpBaseResponse<OrderAuthResultDto>>() {});
        } catch (Exception e) {
            LOG.error(O2oApiConstants.ORDER_AUTH + " serviceCaller ERROR:{}", e);
            throw new BusinessException(HttpResponseConstants.SERVER_ROUTE_UNAVAILABLE_USER.getCode(), HttpResponseConstants.SERVER_ROUTE_UNAVAILABLE_USER.getMsg());
        }
        if (null == response) {
            throw new BusinessException(HttpResponseConstants.SERVER_ROUTE_UNAVAILABLE_USER.getCode(), HttpResponseConstants.SERVER_ROUTE_UNAVAILABLE_USER.getMsg());
        }
        return response.getObj();
    }

}
