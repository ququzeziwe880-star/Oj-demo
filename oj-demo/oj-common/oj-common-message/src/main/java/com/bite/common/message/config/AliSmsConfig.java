package com.bite.common.message.config;

import com.aliyun.auth.credentials.Credential;
import com.aliyun.auth.credentials.provider.StaticCredentialProvider;
import com.aliyun.sdk.service.dypnsapi20170525.AsyncClient;
import darabonba.core.client.ClientOverrideConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AliSmsConfig {

    @Value("${sms.aliyun.accessKeyId:}")
    private String accessKeyId;

    @Value("${sms.aliyun.accessKeySecret:}")
    private String accessKeySecret;

    @Value("${sms.aliyun.endpoint:dypnsapi.aliyuncs.com}")
    private String endpoint;

    @Bean("aliClient")
    public AsyncClient client() {

        StaticCredentialProvider provider = StaticCredentialProvider.create(
                Credential.builder()
                        .accessKeyId(accessKeyId)
                        .accessKeySecret(accessKeySecret)
                        .build()
        );

        return AsyncClient.builder()
                .region("ap-southeast-1")
                .credentialsProvider(provider)
                .overrideConfiguration(
                        ClientOverrideConfiguration.create().setEndpointOverride(endpoint)
                )
                .build();
    }
}