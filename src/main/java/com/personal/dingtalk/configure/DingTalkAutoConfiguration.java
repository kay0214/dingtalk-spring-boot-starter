/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.personal.dingtalk.configure;

import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.personal.dingtalk.properties.DingTalkBean;
import com.personal.dingtalk.properties.DingTalkProperties;
import com.personal.dingtalk.utils.DingTalkUtil;
import com.personal.dingtalk.utils.SpringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author sunpeikai
 * @version DingTalkAutoConfiguration, v0.1 2020/10/9 14:31
 * @description
 */
@Configuration
@Import({SpringUtils.class})
@EnableConfigurationProperties({DingTalkProperties.class})
@ConditionalOnProperty(name = {"dingtalk.enable"}, havingValue = "true")
public class DingTalkAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(DingTalkAutoConfiguration.class);

    private static final String url = "https://oapi.dingtalk.com/robot/send?access_token=";

    private final DingTalkProperties dingTalkProperties;

    public DingTalkAutoConfiguration(DingTalkProperties dingTalkProperties) {
        this.dingTalkProperties = dingTalkProperties;
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(name = {"dingtalk.enable"}, havingValue = "true")
    public DingTalkBean dingTalkBean(){
        // 钉钉client数量
        int size = 0;
        // 钉钉client
        List<DingTalkClient> clients = new ArrayList<>();
        Assert.isTrue(dingTalkProperties.getRobots() != null && dingTalkProperties.getRobots().size() > 0, "robots can't be empty.");
        for (DingTalkProperties.RobotProperties robot : dingTalkProperties.getRobots()) {
            Assert.isTrue(!StringUtils.isEmpty(robot.getAccessToken()), "robot accessToken can't be empty.");
            if(StringUtils.isEmpty(robot.getSecret())){
                // 密钥为空 - 不需要签名
                clients.add(new DefaultDingTalkClient(url + robot.getAccessToken()));
            }else{
                // 密钥不为空 - 需要签名
                long timestamp = System.currentTimeMillis();
                clients.add(new DefaultDingTalkClient(url + robot.getAccessToken()
                        + "&timestamp=" + timestamp
                        + "&sign=" + DingTalkUtil.getSign(timestamp, robot.getSecret())));
            }
            size ++ ;
        }
        log.info("dingtalk client init ok, size = " + size);
        return new DingTalkBean(size, clients);
    }
}
