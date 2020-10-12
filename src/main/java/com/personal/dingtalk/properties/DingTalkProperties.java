/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.personal.dingtalk.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * @author sunpeikai
 * @version DingTalkProperties, v0.1 2020/10/9 14:29
 * @description
 */
@ConfigurationProperties(prefix = "dingtalk")
public class DingTalkProperties {
    private boolean enable;
    private String atMobiles;
    private String appName;
    private String profile;
    private List<RobotProperties> robots;

    public static class RobotProperties{
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

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public String getAtMobiles() {
        return atMobiles;
    }

    public void setAtMobiles(String atMobiles) {
        this.atMobiles = atMobiles;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public List<RobotProperties> getRobots() {
        return robots;
    }

    public void setRobots(List<RobotProperties> robots) {
        this.robots = robots;
    }
}
