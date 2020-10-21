package com.ihomefnt.sky.proxy;

import com.ihomefnt.common.api.ResponseVo;
import com.ihomefnt.sky.common.constant.HttpResponseConstants;
import com.ihomefnt.sky.common.constant.OrderConstants;
import com.ihomefnt.sky.common.exception.BusinessException;
import com.ihomefnt.sky.domain.dto.OrderDto;
import com.ihomefnt.zeus.finder.ServiceCaller;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class OrderProxy {
    private static final Logger LOG = LoggerFactory.getLogger(OrderProxy.class);

    @Resource
    private ServiceCaller serviceCaller;

    public List<OrderDto> queryOrderListByUserId(Integer userId) {
        ResponseVo<List<OrderDto>> response = null;
        try {
            response = serviceCaller.post(OrderConstants.QUERY_ORDER_LIST_BY_USER_ID, userId,
                    new TypeReference<ResponseVo<List<OrderDto>>>() {
                    });
        } catch (Exception e) {
            LOG.error(OrderConstants.QUERY_ORDER_LIST_BY_USER_ID + " serviceCaller ERROR:{}", e);
            throw new BusinessException(HttpResponseConstants.SERVER_ROUTE_UNAVAILABLE_ORDER.getCode(), HttpResponseConstants.SERVER_ROUTE_UNAVAILABLE_ORDER.getMsg());
        }
        if (null == response || !response.isSuccess()) {
            throw new BusinessException(HttpResponseConstants.SERVER_ROUTE_UNAVAILABLE_ORDER.getCode(), HttpResponseConstants.SERVER_ROUTE_UNAVAILABLE_ORDER.getMsg());
        }
        return response.getData();
    }

}
