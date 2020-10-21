package com.ihomefnt.sky.proxy;

import com.ihomefnt.common.api.ResponseVo;
import com.ihomefnt.sky.common.constant.HttpResponseConstants;
import com.ihomefnt.sky.common.constant.UserConstants;
import com.ihomefnt.sky.common.exception.BusinessException;
import com.ihomefnt.sky.domain.dto.UserDto;
import com.ihomefnt.zeus.finder.ServiceCaller;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserProxy {
    private static final Logger LOG = LoggerFactory.getLogger(UserProxy.class);

    @Resource
    private ServiceCaller serviceCaller;

    public UserDto findUserByToken(String token) {
        if (token == null) {
            return null;
        }
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("token", token);

        ResponseVo<UserDto> response = null;
        try {
            response = serviceCaller.post(UserConstants.FIND_USER_BY_TOKEN, params,
                    new TypeReference<ResponseVo<UserDto>>() {
                    });
        } catch (Exception e) {
            LOG.error(UserConstants.FIND_USER_BY_TOKEN + " serviceCaller ERROR:{}", e);
            throw new BusinessException(HttpResponseConstants.SERVER_ROUTE_UNAVAILABLE_USER.getCode(), HttpResponseConstants.SERVER_ROUTE_UNAVAILABLE_USER.getMsg());
        }
        if (null == response) {
            throw new BusinessException(HttpResponseConstants.SERVER_ROUTE_UNAVAILABLE_USER.getCode(), HttpResponseConstants.SERVER_ROUTE_UNAVAILABLE_USER.getMsg());
        }
        if (!response.isSuccess()) {
            throw new BusinessException(HttpResponseConstants.USER_NOT_LOGIN.getCode(), HttpResponseConstants.USER_NOT_LOGIN.getMsg());
        }
        return response.getData();
    }

}
