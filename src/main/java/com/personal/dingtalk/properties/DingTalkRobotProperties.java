/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.personal.dingtalk.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author sunpeikai
 * @version DingTalkRobotProperties, v0.1 2020/10/9 16:31
 * @description
 */
@ConfigurationProperties(prefix = "dingtalk.robots")
public class DingTalkRobotProperties {
    private String accessToken;
    private String secret;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }
}
