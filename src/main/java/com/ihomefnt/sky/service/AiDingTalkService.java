package com.ihomefnt.sky.service;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.ihomefnt.semporna.context.IhomeContextHandler;
import com.ihomefnt.sky.common.http.HttpClientUtil;
import com.ihomefnt.sky.domain.dto.AiDingTalkRecordDto;
import com.ihomefnt.sky.domain.dto.AiMonitorDto;
import com.ihomefnt.sky.proxy.AiDingTalkRecordProxy;
import com.ihomefnt.sky.proxy.AiMonitorConfigProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class AiDingTalkService  {

    private static final Logger LOG = LoggerFactory.getLogger(AiDingTalkService.class);

    @Autowired
    private AiDingTalkRecordProxy aiDingTalkRecordProxy;

    /**
     * DING_URL
     */
    private static final String DING_URL = "https://oapi.dingtalk.com/robot/send?access_token=";

    private static HttpClientUtil httpClientUtil = new HttpClientUtil();

    /**
     * ## traceId跳转链接
     */
    @NacosValue(value = "${sky.trace.id.url}", autoRefreshed = true)
    private String skyTraceIdUrl;

    /**
     * ## orderId跳转链接
     */
    @NacosValue(value = "${warn.order.id.url}", autoRefreshed = true)
    private String warnOrderIdUrl;

    @Autowired
    AiMonitorConfigProxy aiMonitorConfigProxy;

    /**
     * 组装并发送钉钉告警
     *
     * @param aiMonitorDto
     * @param url             接口名
     * @param params          入参
     * @param code            错误码
     * @param msg             错误信息
     */
    public void sendDingTalkWarn(AiMonitorDto aiMonitorDto, String url, String params, Long code, String msg) {
        if (null == aiMonitorDto) {
            return;
        }

        JSONObject sendParams = new JSONObject();
        sendParams.put("msgtype", "markdown");
        if (null != aiMonitorDto.getMonitorAtMobile() && !"".equals(aiMonitorDto.getMonitorAtMobile())) {
            JSONObject map2 = new JSONObject();
            map2.put("isAtAll", false);
            map2.put("atMobiles", aiMonitorDto.getMonitorAtMobile().split(","));
            sendParams.put("at", map2);
        }
        String currentTime = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new Date());
        JSONObject textObject = new JSONObject();
        String urlMsg = url ==null ? "#无#" : url;
        String paramsMsg = "";
        String mobileNum = "";
        if(params != null){
            JSONObject jsonObject = JSONObject.parseObject(params);
            mobileNum = jsonObject.getString("mobileNum");
            String orderNum = jsonObject.getString("orderNum");
            String appVersion = jsonObject.getString("appVersion");
            if(orderNum == null){
                orderNum = jsonObject.getString("orderId");
            }
            paramsMsg += mobileNum == null ? "" : "\n > - 手机号(" + mobileNum + ")";

            if(orderNum != null){
                String orderString = "["+ orderNum +"]("+ warnOrderIdUrl.replace("orderIdReplace",orderNum) +")";
                paramsMsg += "\n > - 订单号(" + orderString + ")";
            }
            paramsMsg += appVersion == null ? "" : "\n > - APP Version(" + appVersion + ")";
            if("".equals(paramsMsg)){
                paramsMsg = "\n > - 请求参数：" + params + "\r\n";
            }
        }else{
            paramsMsg = "\n > - 请求参数为空\r\n";
        }
        String codeMsg = code == null ? "#无#" : code.toString();
        String errorMsg = msg == null ? "#无#" : msg;

        String traceIdUrl = skyTraceIdUrl.replace("replaceTraceId", IhomeContextHandler.getIhomeContext().getTraceId());
        String content = "## " + aiMonitorDto.getMonitorDesc() +
                paramsMsg +
                "\n > - 风险等级：**" + aiMonitorDto.getMonitorLevel() + "**" +
                "\n > - 接口URL：" + urlMsg +
                "\n > - 响应信息：code：" + codeMsg +
                "\n > - 错误信息：" + errorMsg +
                "\n > - " + traceIdUrl +
                "\n > - 报错时间：" + currentTime ;

        textObject.put("title", "o2o接口告警");
        textObject.put("text", content );
        sendParams.put("markdown", textObject);
        //发送钉钉
        sendDingTalk(aiMonitorDto.getMonitorDingToken(), sendParams.toString());

        AiDingTalkRecordDto aiDingTalkRecordDto = new AiDingTalkRecordDto();
        aiDingTalkRecordDto.setRecordType(5).setRecordKey(aiMonitorDto.getMonitorKey())
                .setRecordMobile(mobileNum != null ? mobileNum : "")
                .setRecordDesc(aiMonitorDto.getMonitorDesc())
                .setRecordDingToken(aiMonitorDto.getMonitorDingToken())
                .setRecordDingMsg(content);
        aiDingTalkRecordProxy.addDingTalkRecord(aiDingTalkRecordDto);
    }

    /**
     * 发送钉钉消息
     *
     * @param token  钉钉群token
     * @param params 入参 {"msgtype":"text","at":"@手机号","text":{"content":"发送内容"}}
     * 钉钉机器人API：https://open-doc.dingtalk.com/microapp/serverapi2/qf2nxq
     */
    public void sendDingTalk(String token, String params) {
        String url = DING_URL + token;
        LOG.info("DingTalk.sendDingTalkWarn url:{} params:{}", url, params);
        String result = null;
        try {
            httpClientUtil = new HttpClientUtil();
            result = httpClientUtil.doPost(url, params);
        } catch (Exception e) {
            LOG.error("DingTalk.sendDingTalkWarn response:{}", e.getMessage());
        }
        LOG.info("DingTalk.sendDingTalkWarn response:{}", result);
    }

}
