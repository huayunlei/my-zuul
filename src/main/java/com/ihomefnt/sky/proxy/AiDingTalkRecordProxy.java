package com.ihomefnt.sky.proxy;

import com.ihomefnt.sky.common.constant.WcmConstants;
import com.ihomefnt.sky.common.http.HttpBaseResponse;
import com.ihomefnt.sky.domain.dto.AiDingTalkRecordDto;
import com.ihomefnt.zeus.finder.ServiceCaller;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Description:
 * @Author hua
 * @Date 2019-07-11 14:47
 */
@Service
public class AiDingTalkRecordProxy {
    @Resource
    private ServiceCaller serviceCaller;

    public void addDingTalkRecord(AiDingTalkRecordDto aiDingTalkRecordDto) {
        HttpBaseResponse<?> responseVo = null;
        try {
            responseVo = serviceCaller.post(WcmConstants.ADD_DING_TALK_RECORD, aiDingTalkRecordDto, HttpBaseResponse.class);
        } catch (Exception e) {
        }
    }
}
