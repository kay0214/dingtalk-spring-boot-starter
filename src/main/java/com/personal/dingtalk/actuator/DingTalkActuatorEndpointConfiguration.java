/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.personal.dingtalk.actuator;

import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * @author sunpeikai
 * @version DingTalkActuatorEndpointConfiguration, v0.1 2020/10/10 20:49
 * @description
 */
@ConditionalOnClass({DingTalkActuatorEndpoint.class})
public class DingTalkActuatorEndpointConfiguration {
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnAvailableEndpoint
    public DingTalkActuatorEndpoint dingTalkActuatorEndpoint(){
        return new DingTalkActuatorEndpoint();
    }
}
