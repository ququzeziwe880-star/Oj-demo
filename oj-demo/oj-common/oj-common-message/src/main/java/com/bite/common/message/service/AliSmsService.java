package com.bite.common.message.service;

// 严格按照 2.0.0 依赖能找到的正确类名导入，保证不飘红
import com.aliyun.sdk.service.dypnsapi20170525.AsyncClient;
import com.aliyun.sdk.service.dypnsapi20170525.models.SendSmsVerifyCodeRequest;
import com.aliyun.sdk.service.dypnsapi20170525.models.SendSmsVerifyCodeResponse;
import com.aliyun.sdk.service.dypnsapi20170525.models.SendSmsVerifyCodeResponseBody;

import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
public class AliSmsService {

    @Autowired
    private AsyncClient aliClient;

    @Value("${sms.aliyun.templateCode:100001}")
    private String templateCode;

    @Value("${sms.aliyun.signName:速通互联验证码}")
    private String signName;

    @Value("${sms.send-message:true}")
    private boolean sendMessage;

    /**
     * 发送短信验证码
     *
     * @param phone 接收验证码的手机号
     * @param code  你自己后端生成的随机验证码
     */
    public boolean sendMobileCode(String phone, String code) {
        if (!sendMessage) {
            log.error("短信发送通道关闭，发送失败......" + phone);
            return false;
        }

        java.util.Map<String, String> templateMap = new java.util.HashMap<>();
        templateMap.put("code", code);
        templateMap.put("min", "5");

        SendSmsVerifyCodeRequest sendSmsVerifyCodeRequest = SendSmsVerifyCodeRequest.builder()
                .phoneNumber(phone)
                .signName(this.signName)
                .templateCode(this.templateCode)
                .templateParam(JSON.toJSONString(templateMap))
                .build();

        try {
            CompletableFuture<SendSmsVerifyCodeResponse> responseFuture = aliClient.sendSmsVerifyCode(sendSmsVerifyCodeRequest);
            SendSmsVerifyCodeResponse response = responseFuture.get();

            SendSmsVerifyCodeResponseBody responseBody = response.getBody();

            if (responseBody == null || !"OK".equalsIgnoreCase(responseBody.getCode())) {
                String errorMsg = responseBody != null ? responseBody.getMessage() : "响应体为空";
                log.error("短信{} 发送失败，失败原因:{}.... ",
                        JSON.toJSONString(sendSmsVerifyCodeRequest), errorMsg);
                return false;
            }
            return true;
        } catch (Exception e) {
            log.error("短信{} 发送失败，失败原因:{}.... ",
                    JSON.toJSONString(sendSmsVerifyCodeRequest), e.getMessage());
            return false;
        }
    }
}