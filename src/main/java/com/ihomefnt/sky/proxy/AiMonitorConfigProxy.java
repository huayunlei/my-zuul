package com.ihomefnt.sky.proxy;

import com.ihomefnt.sky.common.constant.WcmConstants;
import com.ihomefnt.sky.common.http.HttpBaseResponse;
import com.ihomefnt.sky.domain.dto.AiMonitorDto;
import com.ihomefnt.zeus.finder.ServiceCaller;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AiMonitorConfigProxy {

    @Resource
    private ServiceCaller ServiceCaller;

    public List<AiMonitorDto> getMonitorByKey(String monitorKey) {

        Map<String, Object> param = new HashMap<>();
        param.put("monitorKey", monitorKey);

        HttpBaseResponse<List<AiMonitorDto>> response = ServiceCaller.post(
                WcmConstants.WCM_QUERY_MONITOR_CONFIG, param,
                new TypeReference<HttpBaseResponse<List<AiMonitorDto>>>() {});

        return response.getObj();
    }
}
