package com.ihomefnt.sky.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Description:
 * @Author hua
 * @Date 2019-07-11 14:53
 */
@Data
@Accessors(chain = true)
public class AiDingTalkRecordDto {
    @ApiModelProperty("监控类型：1，o2o监控；2，wcm监控；3，app-collector监控；5，sky监控")
    private Integer recordType = 1;

    @ApiModelProperty("监控主键")
    private String recordKey;

    @ApiModelProperty("监控入参手机号")
    private String recordMobile = "";

    @ApiModelProperty("告警主要信息")
    private String recordDesc;

    @ApiModelProperty("钉钉群token")
    private String recordDingToken;

    @ApiModelProperty("告警消息体")
    private String recordDingMsg;

}
