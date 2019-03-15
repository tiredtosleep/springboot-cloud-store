package com.leyou.order.config;

import com.github.wxpay.sdk.WXPayConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.InputStream;
@Component
@EnableConfigurationProperties(PayConfig.class)
public class WxPayConfig implements WXPayConfig {

    private final PayConfig payConfig;

    @Autowired
    public WxPayConfig(PayConfig payConfig) {
        this.payConfig = payConfig;
    }

    public String getNotifyUrl() {
        return payConfig.getNotifyUrl();
    } // 通知地址

    @Override
    public String getAppID() {
        return payConfig.getAppId();
    }

    @Override
    public String getMchID() {
        return payConfig.getMchId();
    }

    @Override
    public String getKey() {
        return payConfig.getKey();
    }

    @Override
    public InputStream getCertStream() {
        return null;
    }

    @Override
    public int getHttpConnectTimeoutMs() {
        return payConfig.getConnectTimeoutMs();
    }

    @Override
    public int getHttpReadTimeoutMs() {
        return payConfig.getReadTimeoutMs();
    }
}