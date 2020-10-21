package com.ihomefnt.sky.domain.dto;

import lombok.Data;

@Data
public class AiMonitorDto {

    /**
     * 监控类型：1 接口监控
     */
    private int monitorType;
    /**
     * 监控配置主键
     */
    private String monitorKey;

    /**
     * 告警等级
     */
    private String monitorLevel;

    /**
     * 告警信息
     */
    private String monitorDesc;

    /**
     * 钉钉群token
     */
    private String monitorDingToken;

    /**
     * 钉钉消息@对象手机号
     */
    private String monitorAtMobile;

}
